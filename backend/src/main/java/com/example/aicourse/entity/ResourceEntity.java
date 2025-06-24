package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("t_resource")
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntity {
    @TableId
    private Long id; //
    private String filename; //
    private String path; //
    private String type; //
    private Long size; //
    private Long uploaderId; //
    private Long courseId;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate; //

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    public ResourceEntity(String filename, String path, String type, Long size, Long uploaderId) {
        this.filename = filename;
        this.path = path;
        this.type = type;
        this.size = size;
        this.uploaderId = uploaderId;
    }
}