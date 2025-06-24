package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper qMapper;
    @Autowired
    private QuestionOptionMapper optMapper;

    @Override
    @Transactional
    public Long create(QuestionDTO dto) {
        Question q = new Question();
        BeanUtils.copyProperties(dto, q);
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
    public List<QuestionVO> listByCourse(Long courseId) {
        List<Question> qs = qMapper.selectList(
                Wrappers.<Question>lambdaQuery().eq(Question::getCourseId, courseId)
        );

        return qs.stream().map(q -> {
            QuestionVO vo = new QuestionVO();
            BeanUtils.copyProperties(q, vo);
            if ("SC".equals(q.getType()) || "MC".equals(q.getType())) {
                vo.setOptions(optMapper.selectList(
                        Wrappers.<QuestionOption>lambdaQuery()
                                .eq(QuestionOption::getQuestionId, q.getId())
                ));
            }
            return vo;
        }).collect(Collectors.toList());
    }
}