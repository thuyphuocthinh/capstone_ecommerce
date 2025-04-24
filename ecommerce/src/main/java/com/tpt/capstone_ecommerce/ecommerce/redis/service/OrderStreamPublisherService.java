package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.OrderRedisDTO;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.OrderStreamPublisher;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderStreamPublisherService implements OrderStreamPublisher {
    private final JedisPool jedisPool;

    private final ObjectMapper objectMapper;

    public OrderStreamPublisherService(JedisPool jedisPool, ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishOrderSuccess(OrderRedisDTO orderRedisDTO, List<String> orderItemIds, String ipAddress, String paymentThirdParty) throws JedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> eventData = new HashMap<>();
            String key = RedisSchema.getOrderStreamKey();
            String orderJson = objectMapper.writeValueAsString(orderRedisDTO);
            eventData.put("orderId", orderRedisDTO.getOrderId());
            eventData.put("order", orderJson);
            eventData.put("userId", orderRedisDTO.getUserId());
            eventData.put("orderItemIds", String.join(",", orderItemIds));
            eventData.put("paymentMethod", orderRedisDTO.getPaymentMethod());
            eventData.put("paymentThirdParty", paymentThirdParty);
            eventData.put("ipAddress", ipAddress);
            eventData.put("cartId", orderRedisDTO.getCartId());
            eventData.put("totalPrice", String.valueOf(orderRedisDTO.getTotalPrice()));
            eventData.put("email", orderRedisDTO.getUserEmail());
            eventData.put("notificationType", NOTIFICATION_TYPE.ORDER_SUCCESS.name());
            log.info("key: {}", key);
            log.info("StreamEntryID: {}", StreamEntryID.NEW_ENTRY);
            log.info("eventData: {}", eventData);
            jedis.xadd(key, StreamEntryID.NEW_ENTRY, eventData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publishOrderFailed(String orderId, List<String> shopIds) throws JedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> eventData = new HashMap<>();
            String key = RedisSchema.getOrderStreamKey();
            eventData.put("orderId", orderId);
            eventData.put("shopIds", String.join(",", shopIds));
            eventData.put("notificationType", NOTIFICATION_TYPE.ORDER_CANCELED.name());
            jedis.xadd(key, StreamEntryID.NEW_ENTRY, eventData);
        }
    }
}
