package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.PaperGenDTO;
import com.example.aicourse.entity.Question;
import com.example.aicourse.entity.QuestionOption;
import com.example.aicourse.entity.QuizPaper;
import com.example.aicourse.entity.QuizPaperQuestion;
import com.example.aicourse.repository.QuestionMapper;
import com.example.aicourse.repository.QuestionOptionMapper;
import com.example.aicourse.repository.QuizPaperMapper;
import com.example.aicourse.repository.QuizPaperQuestionMapper;
import com.example.aicourse.service.PaperService;
import com.example.aicourse.vo.paper.QuizPaperDetailsVO;
import com.example.aicourse.vo.paper.QuizPaperVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired private QuizPaperMapper pMapper;
    @Autowired private QuizPaperQuestionMapper pqMapper;
    @Autowired private QuestionMapper questionMapper;
    @Autowired private QuestionOptionMapper optionMapper;


    @Override
    @Transactional
    public Long generatePaper(PaperGenDTO dto) {
        // TODO: 根据 dto.getStrategy() 实现不同的抽题策略
        // 这里暂时使用原有逻辑作为 "RANDOM" 策略

        LambdaQueryWrapper<Question> queryWrapper = Wrappers.<Question>lambdaQuery()
                .eq(dto.getDifficulty() != null, Question::getDifficulty, dto.getDifficulty())
                .in(!CollectionUtils.isEmpty(dto.getKnowledge()), Question::getKnowledge, dto.getKnowledge());

        List<Question> questionBank = questionMapper.selectList(queryWrapper);
        List<Long> pool = questionBank.stream().map(Question::getId).collect(Collectors.toList());
        Collections.shuffle(pool);

        List<Long> selected = pool.stream().limit(dto.getCount()).toList();

        if (selected.isEmpty()) {
            throw new IllegalStateException("未找到符合条件的题目，无法生成试卷");
        }

        QuizPaper paper = new QuizPaper();
        BeanUtils.copyProperties(dto, paper);
        // paper.setDurationMinutes(dto.getDurationMinutes()); // 实体类中需要添加该字段
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

    @Override
    public Page<QuizPaperVO> list(long page, long size, Long courseId, String title) {
        LambdaQueryWrapper<QuizPaper> query = Wrappers.<QuizPaper>lambdaQuery()
                .eq(courseId != null, QuizPaper::getCourseId, courseId)
                .like(StringUtils.hasText(title), QuizPaper::getTitle, title)
                .orderByDesc(QuizPaper::getGmtCreate);

        Page<QuizPaper> entityPage = pMapper.selectPage(new Page<>(page, size), query);

        Page<QuizPaperVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<QuizPaperVO> voList = entityPage.getRecords().stream().map(p -> {
            QuizPaperVO vo = new QuizPaperVO();
            BeanUtils.copyProperties(p, vo);
            // vo.setDurationMinutes(p.getDurationMinutes()); // 实体类中需要添加该字段
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public QuizPaperDetailsVO getPaperDetailsById(Long id) {
        QuizPaper paper = pMapper.selectById(id);
        if (paper == null) return null;

        QuizPaperDetailsVO vo = new QuizPaperDetailsVO();
        BeanUtils.copyProperties(paper, vo);

        // 查询试卷包含的所有问题
        List<QuizPaperQuestion> paperQuestions = pqMapper.selectList(
                Wrappers.<QuizPaperQuestion>lambdaQuery().eq(QuizPaperQuestion::getPaperId, id)
        );

        // 组装问题详情
        List<QuizPaperDetailsVO.QuestionInPaperVO> questionVOs = paperQuestions.stream().map(pq -> {
            Question q = questionMapper.selectById(pq.getQuestionId());
            QuizPaperDetailsVO.QuestionInPaperVO qVO = new QuizPaperDetailsVO.QuestionInPaperVO();
            BeanUtils.copyProperties(q, qVO);
            qVO.setQuestionId(q.getId());
            qVO.setScore(pq.getScore());

            // 如果是选择题，附带选项（不含答案）
            if ("SC".equals(q.getType()) || "MC".equals(q.getType())) {
                List<QuizPaperDetailsVO.OptionInPaperVO> optionVOs = optionMapper
                        .selectList(Wrappers.<QuestionOption>lambdaQuery().eq(QuestionOption::getQuestionId, q.getId()))
                        .stream().map(opt -> {
                            QuizPaperDetailsVO.OptionInPaperVO optVO = new QuizPaperDetailsVO.OptionInPaperVO();
                            optVO.setId(opt.getId());
                            optVO.setContent(opt.getContent());
                            return optVO;
                        }).collect(Collectors.toList());
                qVO.setOptions(optionVOs);
            }
            return qVO;
        }).collect(Collectors.toList());

        vo.setQuestions(questionVOs);
        return vo;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pMapper.deleteById(id);
        // 同时删除试卷-问题的关联记录
        pqMapper.delete(Wrappers.<QuizPaperQuestion>lambdaQuery().eq(QuizPaperQuestion::getPaperId, id));
    }
}