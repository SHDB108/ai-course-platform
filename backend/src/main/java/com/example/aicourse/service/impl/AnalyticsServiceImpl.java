package com.example.aicourse.service.impl;

import com.example.aicourse.repository.QuizSubmissionMapper;
import com.example.aicourse.repository.TaskSubmissionMapper;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.vo.TrendPointVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired private TaskSubmissionMapper tsMapper;
    @Autowired private QuizSubmissionMapper qsMapper;

    @Override
    public List<TrendPointVO> getTrend(Long courseId) {
        return tsMapper.selectByCourse(courseId).stream()
                .map(r -> new TrendPointVO(r.getDate(), r.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Resource> exportCsv(Long courseId) {
        List<String> lines = qsMapper.selectRows(courseId).stream()
                .map(r -> r.getStudentId()+","+r.getScore())
                .collect(Collectors.toList());
        String csv = String.join("\n", lines);
        ByteArrayResource body = new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=score.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }
}