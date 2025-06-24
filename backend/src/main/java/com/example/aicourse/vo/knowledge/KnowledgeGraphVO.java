package com.example.aicourse.vo.knowledge;

import java.util.List;
import lombok.Data;

/**
 * API 11.1 知识图谱数据响应
 */
@Data
public class KnowledgeGraphVO {
    private List<Node> nodes;
    private List<Edge> edges;

    @Data
    public static class Node {
        private Long id;
        private String name;
        private String type;
        private String description;
    }

    @Data
    public static class Edge {
        private Long source;
        private Long target;
        private String relation;
    }
}