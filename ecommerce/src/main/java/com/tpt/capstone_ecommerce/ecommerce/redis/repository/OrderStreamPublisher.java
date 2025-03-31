package com.tpt.capstone_ecommerce.ecommerce.redis.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

public interface OrderStreamPublisher {
    public void publishOrderSuccess(Order order, List<String> orderItemIds, String ipAddress, String paymentThirdParty) throws JedisException;
    public void publishOrderFailed(String orderId, List<String> shopIds) throws JedisException;
}
