package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.service.CartService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;
import java.util.Map;

@Service
@Qualifier(value = "cartConsumerService")
public class CartConsumerService extends ConsumerService implements StreamConsumer {
    private final CartService cartService;

    private final JedisPool jedisPool;

    public CartConsumerService(CartService cartService, JedisPool jedisPool) {
        this.cartService = cartService;
        this.jedisPool = jedisPool;
    }

    @Override
    public void consume(String streamName, String consumerGroup, String consumerName) throws JedisException {
        try(Jedis jedis = jedisPool.getResource()) {

            jedis.xgroupCreate(streamName, consumerGroup, new StreamEntryID("0-0"), true);

            List<Map.Entry<byte[], List<StreamEntry>>> messages = super.readMessageFromStream(jedis, streamName, consumerGroup, consumerName);

            if (messages != null && !messages.isEmpty()) {
                Map.Entry<byte[], List<StreamEntry>> latestMessage = messages.get(messages.size() - 1);
                List<StreamEntry> entries = latestMessage.getValue();
                if (entries != null && !entries.isEmpty()) {
                    StreamEntry latestEntry = entries.get(entries.size() - 1);
                    Map<String, String> fields = latestEntry.getFields();
                    String orderItemIds = fields.get("orderItemIds");
                    String cartId = fields.get("cartId");
                    if(orderItemIds != null && !orderItemIds.isEmpty()){
                        cartService.clearCart(cartId, List.of(orderItemIds.split(",")));
                        jedis.xack(streamName, consumerGroup, latestEntry.getID());
                    }
                }
            }

        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
