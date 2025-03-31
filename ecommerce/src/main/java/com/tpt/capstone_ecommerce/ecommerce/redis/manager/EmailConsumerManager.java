package com.tpt.capstone_ecommerce.ecommerce.redis.manager;

import com.tpt.capstone_ecommerce.ecommerce.redis.repository.StreamConsumer;
import com.tpt.capstone_ecommerce.ecommerce.redis.utils.RedisSchema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class EmailConsumerManager {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isRunning = false;
    private final StreamConsumer streamConsumer;

    public EmailConsumerManager(@Qualifier(value = "emailConsumerService") StreamConsumer streamConsumer) {
        this.streamConsumer = streamConsumer;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndStartConsumer() {
        if (!isRunning) {
            isRunning = true;
            executorService.submit(() ->
                    streamConsumer.consume(RedisSchema.getOrderStreamKey(), RedisSchema.getEmailConsumerGroup(), RedisSchema.getEmailConsumerName())
            );
        }
    }
}
