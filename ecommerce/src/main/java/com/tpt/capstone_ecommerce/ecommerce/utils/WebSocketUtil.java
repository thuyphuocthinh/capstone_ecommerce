package com.tpt.capstone_ecommerce.ecommerce.utils;

public class WebSocketUtil {
    public static String getShopQueueDestination(String shopId) {
        return "/user/" + shopId + "/queue/shop-orders";
    }

    public static String getUserQueueDestination(String userId) {
        return "/user/" + userId + "/queue/user-orders";
    }
}
