package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ResourceType {
    VIDEO("VIDEO"),
    IMAGE("IMAGE"),
    PDF("PDF"),
    PPT("PPT"),
    DOC("DOC"),
    OTHER("OTHER");

    @EnumValue // 标记数据库存的值是 code
    @JsonValue // 标记 Jackson 序列化/反序列化的值是 code
    private final String code;

    ResourceType(String code) {
        this.code = code;
    }
}