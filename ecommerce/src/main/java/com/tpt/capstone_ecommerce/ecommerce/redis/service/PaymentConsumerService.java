package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.OrderRedisDTO;
import com.tpt.capstone_ecommerce.ecommerce.dto.request.PaymentRequest;
import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.enums.CURRENCY;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.StreamEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Qualifier(value = "paymentConsumerService")
public class PaymentConsumerService extends ConsumerService implements StreamConsumer {
    private final JedisPool jedisPool;

    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;

    public PaymentConsumerService(JedisPool jedisPool, ObjectMapper objectMapper, PaymentService paymentService) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @Override
    public void consume(String streamName, String consumerGroup, String consumerName) throws JedisException {
        while (true) {
            try(Jedis jedis = jedisPool.getResource()) {
                ensureConsumerGroupExists(jedis, streamName, consumerGroup);

                List<Map.Entry<String, List<StreamEntry>>> messages = super.readMessageFromStream(jedis, streamName, consumerGroup, consumerName);

                if (messages != null && !messages.isEmpty()) {
                    Map.Entry<String, List<StreamEntry>> latestMessage = messages.get(messages.size() - 1);
                    List<StreamEntry> entries = latestMessage.getValue();
                    if (entries != null && !entries.isEmpty()) {
                        StreamEntry latestEntry = entries.get(entries.size() - 1);
                        Map<String, String> fields = latestEntry.getFields();
                        String orderJson = fields.get("order");
                        OrderRedisDTO order = objectMapper.readValue(orderJson, OrderRedisDTO.class);
                        String paymentMethod = fields.get("paymentMethod");
                        String totalPrice = fields.get("totalPrice");
                        String ipAddress = fields.get("ipAddress");
                        String paymentThirdParty = fields.get("paymentThirdParty");
                        String orderId = fields.get("orderId");
                        if(paymentMethod != null && order != null) {
                            if(paymentMethod.equals(PAYMENT_METHOD.CASH.name())) {
                                paymentService.createPaymentCash(orderId);
                            } else {
                                this.paymentService.createPayment(
                                        new PaymentRequest(BigDecimal.valueOf(Long.parseLong(totalPrice)), CURRENCY.VND.name(), ipAddress), order.getOrderId(), paymentThirdParty
                                );
                                // realtime here
                            }
                            jedis.xack(streamName, consumerGroup, latestEntry.getID());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
