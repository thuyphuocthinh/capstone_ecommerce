package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(25)
public class UserExcel {
    @ExcelProperty("id")
    private String id;

    @ExcelProperty("email")
    private String email;

    @ExcelProperty("firstName")
    private String firstName;

    @ExcelProperty("lastName")
    private String lastName;
}
