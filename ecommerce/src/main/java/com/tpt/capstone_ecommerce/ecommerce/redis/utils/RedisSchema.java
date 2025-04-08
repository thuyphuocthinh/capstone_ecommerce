package com.tpt.capstone_ecommerce.ecommerce.redis.utils;

public class RedisSchema {
    public static String getOrderStreamKey() {
        return RedisKeyHelper.getKey("orderStream");
    }

    public static String getCartConsumerGroup() {
        return RedisKeyHelper.getKey("cartConsumerGroup");
    }

    public static String getCartConsumerName() {
        return RedisKeyHelper.getKey("cartConsumerName");
    }

    public static String getEmailConsumerGroup() {
        return RedisKeyHelper.getKey("emailConsumerGroup");
    }

    public static String getEmailConsumerName() {
        return RedisKeyHelper.getKey("emailConsumerName");
    }

    public static String getPaymentConsumerGroup() {
        return RedisKeyHelper.getKey("paymentConsumerGroup");
    }

    public static String getPaymentConsumerName() {
        return RedisKeyHelper.getKey("paymentConsumerName");
    }

    public static String getShopConsumerGroup() {
        return RedisKeyHelper.getKey("shopConsumerGroup");
    }

    public static String getUserConsumerGroup() {
        return RedisKeyHelper.getKey("userConsumerGroup");
    }

    public static String getShopConsumerName() {
        return RedisKeyHelper.getKey("shopConsumerName");
    }

    public static String getUserConsumerName(String userId) {
        return RedisKeyHelper.getKey("user:" + userId);
    }

    public static String getCategoryKey(Integer pageNumber) {
        return RedisKeyHelper.getKey("category:" + pageNumber);
    }

    public static String getCategoryKeyItem() {
        return RedisKeyHelper.getKey("totalItems");
    }

    // public static String getAccessTokenKey(String userId, String timestamp) {
    //    return RedisKeyHelper.getKey("accessToken:" + userId + ":" + timestamp);
    //}
}
