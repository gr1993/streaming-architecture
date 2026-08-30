package org.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // 실행 환경 생성
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source =
                KafkaSource.<String>builder()
                        .setBootstrapServers("kafka1:9091")
                        .setTopics("meetup-raw-rsvps")
                        .setGroupId("flink-meetup-analysis")
                        .setStartingOffsets(
                                OffsetsInitializer.earliest()
                        )
                        .setValueOnlyDeserializer(
                                new SimpleStringSchema()
                        )
                        .build();

        DataStream<String> stream =
                env.fromSource(
                        source,
                        WatermarkStrategy.noWatermarks(),
                        "Kafka Source"
                );

        KafkaSink<String> sink =
                KafkaSink.<String>builder()
                        .setBootstrapServers("kafka1:9091")
                        .setRecordSerializer(
                                KafkaRecordSerializationSchema.builder()
                                        .setTopic("flink-output")
                                        .setValueSerializationSchema(
                                                new SimpleStringSchema()
                                        )
                                        .build()
                        )
                        .build();

        stream.sinkTo(sink);

        // Flink 잡 실행
        env.execute("Meetup Analysis");
    }
}