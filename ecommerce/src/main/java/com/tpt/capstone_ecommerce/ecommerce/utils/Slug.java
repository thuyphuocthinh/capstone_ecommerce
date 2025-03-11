package com.tpt.capstone_ecommerce.ecommerce.utils;

import java.text.Normalizer;
import java.util.Locale;

public class Slug {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Chuẩn hóa Unicode, chuyển về chữ thường
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "") // Loại bỏ dấu tiếng Việt
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "") // Loại bỏ ký tự đặc biệt
                .replaceAll("\\s+", "-") // Thay khoảng trắng bằng dấu "-"
                .replaceAll("-+", "-") // Xóa dấu "-" dư thừa
                .trim();

        return slug;
    }
}
