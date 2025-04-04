package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ConsumerService {
    public List<Map.Entry<byte[], List<StreamEntry>>> readMessageFromStream(Jedis jedis, String streamName, String consumerGroup, String consumerName) throws JedisException {
        byte[] streamNameBytes = streamName.getBytes(StandardCharsets.UTF_8);
        byte[] consumerGroupBytes = consumerGroup.getBytes(StandardCharsets.UTF_8);
        byte[] consumerNameBytes = consumerName.getBytes(StandardCharsets.UTF_8);

        jedis.xgroupCreate(streamName, consumerGroup, new StreamEntryID("0-0"), true);

        // Đọc tin nhắn từ stream
        @SuppressWarnings("unchecked")
        List<Map.Entry<byte[], List<StreamEntry>>> messages = jedis.xreadGroup(
                consumerGroup.getBytes(StandardCharsets.UTF_8),
                consumerName.getBytes(StandardCharsets.UTF_8),
                new XReadGroupParams().count(1).block(5000),
                new Map.Entry[]{
                        new AbstractMap.SimpleImmutableEntry<>(
                                streamName.getBytes(StandardCharsets.UTF_8),
                                ">".getBytes(StandardCharsets.UTF_8)
                        )
                }
        );
        return messages;
    }
}
