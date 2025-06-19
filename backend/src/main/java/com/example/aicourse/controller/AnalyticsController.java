package com.example.aicourse.controller;

import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.vo.TrendPointVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    @Autowired
    AnalyticsService service;

    @GetMapping("/course/{courseId}/trend")
    public com.example.aicourse.utils.Result<List<TrendPointVO>> trend(@PathVariable Long courseId){
        return com.example.aicourse.utils.Result.ok(service.getTrend(courseId));   // 返回折线图所需数据
    }

    @GetMapping("/course/{courseId}/export")
    public ResponseEntity<ResponseEntity<Resource>> exportCsv(@PathVariable Long courseId){
        var res = service.exportCsv(courseId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=score.csv")
                .contentType(MediaType.TEXT_PLAIN).body(res);
    }
}