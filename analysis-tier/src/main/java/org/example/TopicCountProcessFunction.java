package org.example;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class TopicCountProcessFunction extends KeyedProcessFunction<String, String, TopicCount> {

    private ValueState<Long> countState;

    @Override
    public void open(Configuration parameters) throws Exception {

        ValueStateDescriptor<Long> descriptor =
                new ValueStateDescriptor<>(
                        "topic-count",
                        Long.class
                );

        countState = getRuntimeContext()
                .getState(descriptor);
    }

    @Override
    public void processElement(
            String topic,
            Context ctx,
            Collector<TopicCount> out)
            throws Exception {

        Long count = countState.value();

        if (count == null) {
            count = 0L;
        }

        count++;

        countState.update(count);

        out.collect(
                new TopicCount(topic, count)
        );
    }
}
