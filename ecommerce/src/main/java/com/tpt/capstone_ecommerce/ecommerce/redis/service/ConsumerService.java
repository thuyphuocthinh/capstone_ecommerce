package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamGroupInfo;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ConsumerService {
    public List<Map.Entry<String, List<StreamEntry>>> readMessageFromStream(Jedis jedis, String streamName, String consumerGroup, String consumerName) throws JedisException {
        byte[] streamNameBytes = streamName.getBytes(StandardCharsets.UTF_8);
        byte[] consumerGroupBytes = consumerGroup.getBytes(StandardCharsets.UTF_8);
        byte[] consumerNameBytes = consumerName.getBytes(StandardCharsets.UTF_8);

        // jedis.xgroupCreate(streamName, consumerGroup, new StreamEntryID("0-0"), true);
        // ensureConsumerGroupExists(jedis, streamName, consumerGroup);

        // Đọc tin nhắn từ stream
        @SuppressWarnings("unchecked")
        Map<String, StreamEntryID> streams = new HashMap<>();
        streams.put(streamName, StreamEntryID.UNRECEIVED_ENTRY);

        List<Map.Entry<String, List<StreamEntry>>> messages = jedis.xreadGroup(
                consumerGroup,  // Consumer Group name
                consumerName,   // Consumer name
                XReadGroupParams.xReadGroupParams().count(1).block(5000),
                streams         // Đảm bảo streams là một Map hợp lệ
        );

        System.out.println("Reading from stream: " + streamName + " with group: " + consumerGroup);
        if (messages != null && !messages.isEmpty()) {
            System.out.println("Detail Messages: " + messages);
        } else {
            System.out.println("No messages received.");
        }

        return messages;
    }

    public void ensureConsumerGroupExists(Jedis jedis, String streamName, String consumerGroup) {
        try {
            List<StreamGroupInfo> groups = jedis.xinfoGroups(streamName);
            boolean exists = groups.stream().anyMatch(group -> consumerGroup.equals(group.getName()));
            if (!exists) {
                jedis.xgroupCreate(streamName, consumerGroup, new StreamEntryID("0-0"), true);
                log.info("Created consumer group '{}' on stream '{}'", consumerGroup, streamName);
            }
        } catch (JedisDataException e) {
            if (e.getMessage().contains("no such key")) {
                // Stream chưa tồn tại → tạo mới
                jedis.xgroupCreate(streamName, consumerGroup, new StreamEntryID("0-0"), true);
                log.info("Created stream and consumer group '{}' on stream '{}'", consumerGroup, streamName);
            } else {
                throw e;
            }
        }
    }
}
