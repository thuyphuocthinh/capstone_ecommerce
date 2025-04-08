package com.tpt.capstone_ecommerce.ecommerce.redis.manager;

import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UserConsumerManager {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isRunning = false;
    private final StreamConsumer streamConsumer;

    public UserConsumerManager(@Qualifier(value = "userConsumerService") StreamConsumer streamConsumer) {
        this.streamConsumer = streamConsumer;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndStartConsumer() {
        if (!isRunning) {
            isRunning = true;
            // TODO: FIX get user consumer name => id
//            executorService.submit(() ->
//                    streamConsumer.consume(RedisSchema.getOrderStreamKey(), RedisSchema.getUserConsumerGroup(), RedisSchema.getUserConsumerName())
//            );
        }
    }
}
