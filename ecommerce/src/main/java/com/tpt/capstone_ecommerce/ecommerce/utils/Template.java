package com.tpt.capstone_ecommerce.ecommerce.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

public class Template {
    public static String getOtpHtmlTemplate(String otp) throws IOException {
        // Read the HTML template file
        ClassPathResource resource = new ClassPathResource("templates/otp_template.html");
        String template = Files.readString(resource.getFile().toPath());

        // Replace {{OTP}} with the actual OTP
        return template.replace("{{OTP}}", otp);
    }
}
