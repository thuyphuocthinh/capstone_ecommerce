package com.tpt.capstone_ecommerce.ecommerce.utils;

public class WebSocketUtil {
    public static String getShopQueueDestination() {
        return "/queue/shop-orders";
    }

    public static String getUserQueueDestination() {
        return "/queue/user-orders";
    }
}
