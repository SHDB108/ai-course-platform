package com.example.aicourse.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.aicourse.dto.paper.OptionDTO;
import com.example.aicourse.dto.paper.QuestionDTO;
import com.example.aicourse.dto.paper.QuestionExcelModel;
import com.example.aicourse.service.QuestionService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionImportListener extends AnalysisEventListener<QuestionExcelModel> {

    private final QuestionService questionService;
    private final Long courseId;
    private final Long creatorId;

    public QuestionImportListener(QuestionService questionService, Long courseId, Long creatorId) {
        this.questionService = questionService;
        this.courseId = courseId;
        this.creatorId = creatorId;
    }

    @Override
    public void invoke(QuestionExcelModel data, AnalysisContext context) {
        // 将Excel模型转换为业务DTO
        QuestionDTO dto = new QuestionDTO();
        dto.setStem(data.getStem());
        dto.setType(data.getType());
        dto.setDifficulty(data.getDifficulty());
        dto.setAnswer(data.getAnswer());
        dto.setCourseId(courseId);
        dto.setCreatorId(creatorId);

        if (StringUtils.hasText(data.getKnowledge())) {
            dto.setKnowledge(Arrays.asList(data.getKnowledge().split("[,，]")));
        }

        // 处理选择题选项
        if ("SC".equals(data.getType()) || "MC".equals(data.getType())) {
            List<OptionDTO> options = new ArrayList<>();
            addOptionIfPresent(options, data.getOptionA(), data.getIsCorrectA());
            addOptionIfPresent(options, data.getOptionB(), data.getIsCorrectB());
            addOptionIfPresent(options, data.getOptionC(), data.getIsCorrectC());
            addOptionIfPresent(options, data.getOptionD(), data.getIsCorrectD());
            dto.setOptions(options);
        }

        // 调用服务创建题目
        questionService.create(dto);
    }

    private void addOptionIfPresent(List<OptionDTO> options, String content, Boolean isCorrect) {
        if (StringUtils.hasText(content)) {
            OptionDTO option = new OptionDTO();
            option.setContent(content);
            option.setIsCorrect(isCorrect != null && isCorrect);
            options.add(option);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有题目数据解析完成！");
    }
}