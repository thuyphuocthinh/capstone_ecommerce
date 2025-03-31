package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

import redis.clients.jedis.exceptions.JedisException;

public interface StreamConsumer {
    void consume(String streamName, String consumerGroup, String consumerName) throws JedisException;
}
