package com.tpt.capstone_ecommerce.ecommerce.service;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

public interface ExcelService {
    void exportExcel(HttpServletResponse response) throws IOException, java.io.IOException;
}
