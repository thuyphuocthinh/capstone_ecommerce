package com.tpt.capstone_ecommerce.ecommerce.redis.service;

import com.tpt.capstone_ecommerce.ecommerce.constant.NotificationConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.ShopErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.constant.UserErrorConstant;
import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.enums.NOTIFICATION_TYPE;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;
import java.util.Map;

@Service
@Qualifier(value = "userConsumerService")
@RequiredArgsConstructor
public class UserConsumerService extends ConsumerService implements StreamConsumer {
    private final JedisPool jedisPool;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    @Override
    public void consume(String streamName, String consumerGroup, String consumerName) throws JedisException {
        while(true) {
            try(Jedis jedis = jedisPool.getResource()) {
                ensureConsumerGroupExists(jedis, streamName, consumerGroup);
                List<Map.Entry<String, List<StreamEntry>>> messages = super.readMessageFromStream(jedis, streamName, consumerGroup, consumerName);
                if (messages != null && !messages.isEmpty()) {
                    Map.Entry<String, List<StreamEntry>> latestMessage = messages.get(messages.size() - 1);
                    List<StreamEntry> entries = latestMessage.getValue();
                    if (entries != null && !entries.isEmpty()) {
                        StreamEntry latestEntry = entries.get(entries.size() - 1);
                        Map<String, String> fields = latestEntry.getFields();
                        String orderId = fields.get("orderId");
                        String userId = fields.get("userId");
                        NOTIFICATION_TYPE type = NOTIFICATION_TYPE.valueOf(fields.get("notificationType"));
                        User findUser = this.userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException(UserErrorConstant.USER_NOT_FOUND));

                        this.notificationService.addNewNotificationForUser(findUser.getEmail(), orderId, type, NotificationConstant.ORDER_CANCELED);
                    }
                }
            } catch (BadRequestException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
