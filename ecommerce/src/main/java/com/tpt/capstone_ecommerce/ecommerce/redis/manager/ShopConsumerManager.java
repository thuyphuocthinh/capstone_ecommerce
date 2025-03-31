package com.tpt.capstone_ecommerce.ecommerce.redis.manager;

import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import com.tpt.capstone_ecommerce.ecommerce.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ShopConsumerManager {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isRunning = false;
    private final StreamConsumer streamConsumer;

    public ShopConsumerManager(@Qualifier(value = "shopConsumerService") StreamConsumer streamConsumer) {
        this.streamConsumer = streamConsumer;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndStartConsumer() {
        if (!isRunning) {
            isRunning = true;
            executorService.submit(() ->
                    streamConsumer.consume(RedisSchema.getOrderStreamKey(), RedisSchema.getShopConsumerGroup(), RedisSchema.getShopConsumerName())
            );
        }
    }
}
