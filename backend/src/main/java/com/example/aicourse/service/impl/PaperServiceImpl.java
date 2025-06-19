package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.PaperGenDTO;
import com.example.aicourse.entity.Question;
import com.example.aicourse.entity.QuizPaper;
import com.example.aicourse.entity.QuizPaperQuestion;
import com.example.aicourse.repository.QuestionMapper;
import com.example.aicourse.repository.QuizPaperMapper;
import com.example.aicourse.repository.QuizPaperQuestionMapper;
import com.example.aicourse.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired private QuizPaperMapper pMapper;
    @Autowired private QuizPaperQuestionMapper pqMapper;
    @Autowired private QuestionMapper questionMapper; // 注入 QuestionMapper

    @Override
    @Transactional
    public Long generatePaper(PaperGenDTO dto) {
        LambdaQueryWrapper<Question> queryWrapper = Wrappers.lambdaQuery();

        if (dto.getDifficulty() != null) {
            queryWrapper.eq(Question::getDifficulty, dto.getDifficulty());
        }
        if (!CollectionUtils.isEmpty(dto.getKnowledge())) {
            queryWrapper.in(Question::getKnowledge, dto.getKnowledge());
        }

        List<Question> questionBank = questionMapper.selectList(queryWrapper);
        List<Long> pool = questionBank.stream().map(Question::getId).collect(Collectors.toList());
        Collections.shuffle(pool);

        List<Long> selected = pool.stream().limit(dto.getCount()).toList();

        if (selected.isEmpty()) {
            throw new IllegalStateException("未找到符合条件的题目，无法生成试卷");
        }

        QuizPaper paper = new QuizPaper();
        paper.setCourseId(dto.getCourseId());
        paper.setTitle(dto.getTitle());
        paper.setTotalScore(dto.getTotalScore());
        pMapper.insert(paper);

        BigDecimal per = dto.getTotalScore()
                .divide(BigDecimal.valueOf(selected.size()), 1, RoundingMode.HALF_UP);
        for (Long qid : selected) {
            QuizPaperQuestion pq = new QuizPaperQuestion();
            pq.setPaperId(paper.getId());
            pq.setQuestionId(qid);
            pq.setScore(per);
            pqMapper.insert(pq);
        }

        return paper.getId();
    }
}