package org.example;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopNProcessFunction extends KeyedProcessFunction<String, TopicCount, String> {

    private final int topN;

    private transient MapState<String, Long> topicCounts;

    public TopNProcessFunction(int topN) {
        this.topN = topN;
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        MapStateDescriptor<String, Long> descriptor =
                new MapStateDescriptor<>(
                        "topic-counts",
                        String.class,
                        Long.class
                );

        topicCounts =
                getRuntimeContext().getMapState(descriptor);
    }

    @Override
    public void processElement(
            TopicCount value,
            Context ctx,
            Collector<String> out)
            throws Exception {

        topicCounts.put(
                value.getTopic(),
                value.getCount()
        );

        List<Map.Entry<String, Long>> topTopics =
                new ArrayList<>();

        for (Map.Entry<String, Long> entry :
                topicCounts.entries()) {

            topTopics.add(entry);
        }

        topTopics.sort(
                Map.Entry.<String, Long>comparingByValue()
                        .reversed()
        );

        if (topTopics.size() > topN) {
            topTopics =
                    topTopics.subList(0, topN);
        }

        out.collect(
                topTopics.toString()
        );
    }
}
