package org.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;

/**
 * 디버깅 관련 내용은 README 참고
 * Flink가 IDE JVM 내부에서 '임시 미니 클러스터(Local MiniCluster)'를 만들어서 독립적으로 실행
 * 이 프로젝트를 빌드하여 jar파일을 만들고 Flink UI에서 직접 제출해야 클러스터에서 실행시킬 수 있음
 * 빌드는 build.gradle에 shadowJar을 실행, 제출은 README 참고
 */
public class MeetupAnalysisJob {
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