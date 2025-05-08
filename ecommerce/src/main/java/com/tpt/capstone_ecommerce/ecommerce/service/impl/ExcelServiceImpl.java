package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import com.tpt.capstone_ecommerce.ecommerce.entity.UserExcel;
import com.tpt.capstone_ecommerce.ecommerce.repository.UserRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.ExcelService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final UserRepository userRepository;

    @Override
    public void exportExcel(HttpServletResponse response) throws IOException {
        Long startTime = System.currentTimeMillis();
        List<User> users = userRepository.findAll();
        List<UserExcel> userExcelList = users.stream().map(user -> {
            UserExcel ue = new UserExcel();
            ue.setId(user.getId());
            ue.setFirstName(user.getFirstName());
            ue.setEmail(user.getEmail());
            ue.setLastName(user.getLastName());
            return ue;
        }).toList();
        log.info("DONE - Get user list: {} ms", System.currentTimeMillis() - startTime);
        if (users.isEmpty()) return;
        String fileName = "data-user-" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        ExcelWriterBuilder writerBuilder = EasyExcelFactory.write(response.getOutputStream(), UserExcel.class);
        writerBuilder.registerWriteHandler(new WriteHandler() {}).excelType(ExcelTypeEnum.XLSX).sheet().doWrite(userExcelList);
        log.info("DONE - export: {} ms", System.currentTimeMillis() - startTime);
    }
}
