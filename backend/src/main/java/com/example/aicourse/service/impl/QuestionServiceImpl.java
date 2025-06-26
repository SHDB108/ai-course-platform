package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.OptionDTO;
import com.example.aicourse.dto.paper.QuestionDTO;
import com.example.aicourse.entity.Question;
import com.example.aicourse.entity.QuestionOption;
import com.example.aicourse.repository.QuestionMapper;
import com.example.aicourse.repository.QuestionOptionMapper;
import com.example.aicourse.service.QuestionService;
import com.example.aicourse.vo.paper.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.alibaba.excel.EasyExcel;
import com.example.aicourse.dto.paper.QuestionExcelModel;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper qMapper;
    private final QuestionOptionMapper optMapper;

    @Autowired
    public QuestionServiceImpl(QuestionMapper qMapper, QuestionOptionMapper optMapper) {
        this.qMapper = qMapper;
        this.optMapper = optMapper;
    }
    
    @Override
    @Transactional
    public Long create(QuestionDTO dto) {
        Question q = new Question();
        BeanUtils.copyProperties(dto, q);
        if (dto.getKnowledge() != null) {
            q.setKnowledge(String.join(",", dto.getKnowledge()));
        }
        qMapper.insert(q);

        if ("SC".equals(dto.getType()) || "MC".equals(dto.getType())) {
            for (OptionDTO o : dto.getOptions()) {
                QuestionOption opt = new QuestionOption();
                opt.setQuestionId(q.getId());
                opt.setContent(o.getContent());
                opt.setIsCorrect(o.getIsCorrect());
                optMapper.insert(opt);
            }
        }
        return q.getId();
    }

    @Override
    public Page<QuestionVO> list(long page, long size, Long courseId, String type, Integer difficulty, String knowledge, String keyword) {
        LambdaQueryWrapper<Question> query = Wrappers.<Question>lambdaQuery()
                .eq(courseId != null, Question::getCourseId, courseId)
                .eq(StringUtils.hasText(type), Question::getType, type)
                .eq(difficulty != null, Question::getDifficulty, difficulty)
                .like(StringUtils.hasText(knowledge), Question::getKnowledge, knowledge)
                .like(StringUtils.hasText(keyword), Question::getStem, keyword)
                .orderByDesc(Question::getGmtCreate);

        Page<Question> entityPage = qMapper.selectPage(new Page<>(page, size), query);

        Page<QuestionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<QuestionVO> voList = entityPage.getRecords().stream().map(this::mapToVO).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public QuestionVO getVOById(Long id) {
        Question q = qMapper.selectById(id);
        if (q == null) return null;
        return mapToVO(q);
    }

    @Override
    @Transactional
    public void update(Long id, QuestionDTO dto) {
        Question q = new Question();
        BeanUtils.copyProperties(dto, q);
        q.setId(id);
        if (dto.getKnowledge() != null) {
            q.setKnowledge(String.join(",", dto.getKnowledge()));
        }
        qMapper.updateById(q);

        // 先删除旧选项，再插入新选项
        optMapper.delete(Wrappers.<QuestionOption>lambdaQuery().eq(QuestionOption::getQuestionId, id));
        if ("SC".equals(dto.getType()) || "MC".equals(dto.getType())) {
            for (OptionDTO o : dto.getOptions()) {
                QuestionOption opt = new QuestionOption();
                opt.setQuestionId(id);
                opt.setContent(o.getContent());
                opt.setIsCorrect(o.getIsCorrect());
                optMapper.insert(opt);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        qMapper.deleteById(id);
        optMapper.delete(Wrappers.<QuestionOption>lambdaQuery().eq(QuestionOption::getQuestionId, id));
    }

    /**
     * 辅助方法，将 Question Entity 转换为 QuestionVO
     */
    private QuestionVO mapToVO(Question q) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(q, vo);
        if ("SC".equals(q.getType()) || "MC".equals(q.getType())) {
            vo.setOptions(optMapper.selectList(
                    Wrappers.<QuestionOption>lambdaQuery()
                            .eq(QuestionOption::getQuestionId, q.getId())
            ));
        }
        return vo;
    }

    @Override
    public void importQuestions(MultipartFile file, Long courseId, Long creatorId) throws IOException {
        EasyExcel.read(file.getInputStream(), QuestionExcelModel.class, new QuestionImportListener(this, courseId, creatorId))
                .sheet()
                .doRead();
    }

    @Override
    public void exportQuestions(HttpServletResponse response, Long courseId) throws IOException {
        // 1. 设置HTTP响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("题目列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 2. 查询数据库中的题目数据
        List<Question> questions = qMapper.selectList(
                Wrappers.<Question>lambdaQuery().eq(courseId != null, Question::getCourseId, courseId)
        );

        // 3. 将实体转换为Excel模型
        List<QuestionExcelModel> excelData = questions.stream().map(q -> {
            QuestionExcelModel model = new QuestionExcelModel();
            model.setStem(q.getStem());
            model.setType(q.getType());
            model.setDifficulty(q.getDifficulty());
            model.setKnowledge(q.getKnowledge());
            model.setAnswer(q.getAnswer());

            if ("SC".equals(q.getType()) || "MC".equals(q.getType())) {
                List<QuestionOption> options = optMapper.selectList(
                        Wrappers.<QuestionOption>lambdaQuery().eq(QuestionOption::getQuestionId, q.getId())
                );
                // 填充前4个选项到Excel模型中
                if (options.size() > 0) {
                    model.setOptionA(options.get(0).getContent());
                    model.setIsCorrectA(options.get(0).getIsCorrect());
                }
                if (options.size() > 1) {
                    model.setOptionB(options.get(1).getContent());
                    model.setIsCorrectB(options.get(1).getIsCorrect());
                }
                if (options.size() > 2) {
                    model.setOptionC(options.get(2).getContent());
                    model.setIsCorrectC(options.get(2).getIsCorrect());
                }
                if (options.size() > 3) {
                    model.setOptionD(options.get(3).getContent());
                    model.setIsCorrectD(options.get(3).getIsCorrect());
                }
            }
            return model;
        }).collect(Collectors.toList());

        // 4. 使用EasyExcel写入到响应流
        EasyExcel.write(response.getOutputStream(), QuestionExcelModel.class).sheet("题目列表").doWrite(excelData);
    }
}