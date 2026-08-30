package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class ExtractTopics implements FlatMapFunction<String, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void flatMap(String json, Collector<String> out) throws Exception {
        JsonNode root = objectMapper.readTree(json);

        String groupName =
                root.path("group")
                        .path("group_name")
                        .asText();

        out.collect(groupName);
    }
}