package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.exceptions.JedisException;

@Service
@Qualifier(value = "userConsumerService")
public class UserConsumerService extends ConsumerService implements StreamConsumer {

    @Override
    public void consume(String streamName, String consumerGroup, String consumerName) throws JedisException {

    }
}
