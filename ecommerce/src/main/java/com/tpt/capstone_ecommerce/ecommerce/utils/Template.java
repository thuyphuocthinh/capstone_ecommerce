package com.tpt.capstone_ecommerce.ecommerce.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class Template {
    public static String getOtpHtmlTemplateAuth(String otp) throws IOException {
        // Read the HTML template file
        ClassPathResource resource = new ClassPathResource("templates/otp_template_auth.html");
        String template = Files.readString(resource.getFile().toPath());

        // Replace {{OTP}} with the actual OTP
        return template.replace("{{OTP}}", otp);
    }

    public static String getOtpHtmlTemplateForgot(String otp) throws IOException {
        // Read the HTML template file
        ClassPathResource resource = new ClassPathResource("templates/otp_template_forgot.html");
        String template = Files.readString(resource.getFile().toPath());

        // Replace {{OTP}} with the actual OTP
        return template.replace("{{OTP}}", otp);
    }

    public static String getOtpHtmlTemplateOrder(String orderId, String totalPrice) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/otp_template_order.html");

        String template;
        try (InputStream inputStream = resource.getInputStream()) {
            template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        Map<String, String> replacements = Map.of(
                "{{orderId}}", orderId,
                "{{totalPrice}}", totalPrice
        );

        return replacements.entrySet().stream()
                .reduce(template, (temp, entry) -> temp.replace(entry.getKey(), entry.getValue()), (t1, t2) -> t1);
    }

}
