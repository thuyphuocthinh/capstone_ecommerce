package com.tpt.capstone_ecommerce.ecommerce.enums;

public enum ORDER_STATUS {
    PENDING,      // Đang chờ xử lý
    PROCESSING,   // Đang xử lý (tức là ít nhất một shop đã xác nhận)
    PARTIALLY_SHIPPED, // Một số sản phẩm đã được gửi
    SHIPPED,      // Tất cả sản phẩm đã được gửi
    COMPLETED,    // Đã nhận đủ hàng
    CANCELLED,    // Đã hủy toàn bộ đơn
    REFUNDED      // Đơn đã được hoàn tiền
}
