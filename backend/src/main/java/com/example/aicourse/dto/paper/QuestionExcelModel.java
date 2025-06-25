package com.example.aicourse.dto.paper;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QuestionExcelModel {
    @ExcelProperty("题目描述(Stem)")
    private String stem;

    @ExcelProperty("题型(Type)")
    private String type; // SC, MC, TF, FILL, SA, CODE

    @ExcelProperty("难度(Difficulty)")
    private Integer difficulty; // 1-5

    @ExcelProperty("知识点(Knowledge)")
    private String knowledge; // 多个知识点用逗号分隔

    @ExcelProperty("非选择题答案(Answer)")
    private String answer;

    @ExcelProperty("选项A内容")
    private String optionA;

    @ExcelProperty("选项A是否正确")
    private Boolean isCorrectA;

    @ExcelProperty("选项B内容")
    private String optionB;

    @ExcelProperty("选项B是否正确")
    private Boolean isCorrectB;

    @ExcelProperty("选项C内容")
    private String optionC;

    @ExcelProperty("选项C是否正确")
    private Boolean isCorrectC;

    @ExcelProperty("选项D内容")
    private String optionD;

    @ExcelProperty("选项D是否正确")
    private Boolean isCorrectD;
}