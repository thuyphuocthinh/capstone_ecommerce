package com.tpt.capstone_ecommerce.ecommerce.enums;

public enum ORDER_ITEM_STATUS {
    PENDING,    // Chưa xác nhận từ shop
    CONFIRMED,  // Được shop xác nhận
    SHIPPED,    // Đã gửi hàng
    DELIVERED,  // Đã nhận hàng
    CANCELLED   // Hủy item (có thể do shop hoặc khách)
}
