package com.example.aicourse.service;

import com.example.aicourse.vo.TrendPointVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnalyticsService {
    /** 获取某课程成绩趋势折线点 */
    List<TrendPointVO> getTrend(Long courseId);
    /** 导出某课程成绩 CSV */
    ResponseEntity<Resource> exportCsv(Long courseId);
}