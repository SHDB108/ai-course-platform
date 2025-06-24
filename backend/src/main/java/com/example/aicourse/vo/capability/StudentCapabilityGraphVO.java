package com.example.aicourse.vo.capability;

import java.math.BigDecimal;
import java.util.List;

import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;
import lombok.Data;

/**
 * API 12.2 学生能力图谱响应
 */
@Data
public class StudentCapabilityGraphVO {
    private Long studentId;
    private Long courseId;
    private List<CapabilityScore> capabilityScores;
    private GraphStructure graphStructure;

    @Data
    public static class CapabilityScore {
        private Long capabilityId;
        private String name;
        private BigDecimal score;
    }

    @Data
    public static class GraphStructure {
        private List<CapabilityPointVO> nodes;
        private List<KnowledgeGraphVO.Edge> edges;
    }
}