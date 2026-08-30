package org.example;

public class TopicCount {

    private String topic;
    private long count;

    public TopicCount() {
    }

    public TopicCount(String topic, long count) {
        this.topic = topic;
        this.count = count;
    }

    public String getTopic() {
        return topic;
    }

    public long getCount() {
        return count;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TopicCount{" +
                "topic='" + topic + '\'' +
                ", count=" + count +
                '}';
    }
}