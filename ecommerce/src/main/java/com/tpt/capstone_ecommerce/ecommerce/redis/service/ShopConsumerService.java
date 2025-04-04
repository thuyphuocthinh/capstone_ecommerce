package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.constant.NotificationConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.NotificationService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.StreamEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Qualifier(value = "shopConsumerService")
public class ShopConsumerService extends ConsumerService implements StreamConsumer {
    private final JedisPool jedisPool;

    private final NotificationService notificationService;

    private final ShopRepository shopRepository;

    public ShopConsumerService(JedisPool jedisPool, NotificationService notificationService, ShopRepository shopRepository) {
        this.jedisPool = jedisPool;
        this.notificationService = notificationService;
        this.shopRepository = shopRepository;
    }

    @Override
    public void consume(String streamName, String consumerGroup, String consumerName) throws JedisException {
        try(Jedis jedis = jedisPool.getResource()) {
            List<Map.Entry<byte[], List<StreamEntry>>> messages = super.readMessageFromStream(jedis, streamName, consumerGroup, consumerName);
            if (messages != null && !messages.isEmpty()) {
                Map.Entry<byte[], List<StreamEntry>> latestMessage = messages.get(messages.size() - 1);
                List<StreamEntry> entries = latestMessage.getValue();
                if (entries != null && !entries.isEmpty()) {
                    StreamEntry latestEntry = entries.get(entries.size() - 1);
                    Map<String, String> fields = latestEntry.getFields();
                    String orderId = fields.get("orderId");
                    String shopIdsString = fields.get("shopIds");
                    String[] shopIds = shopIdsString.split(",");
                    int count = 0;
                    for(String shopId: shopIds) {
                        Shop findShop = this.shopRepository.findById(shopId)
                                .orElseThrow(() -> new NotFoundException(ShopErrorConstant.SHOP_NOT_FOUND));
                        this.notificationService.addNewNotificationForShop(findShop.getId(), orderId, NOTIFICATION_TYPE.ORDER_CANCELED, NotificationConstant.ORDER_CANCELED);
                        count++;
                    }
                    if(count == shopIds.length) {
                        jedis.xack(streamName, consumerGroup, latestEntry.getID());
                    }
                }
            }
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
