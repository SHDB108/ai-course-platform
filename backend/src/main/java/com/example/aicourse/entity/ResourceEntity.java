package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@TableName("t_resource")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ResourceEntity {
    @TableId
    private Long id; //
    private String filename; //
    private String path; //
    private ResourceType type; //
    private Long size; //
    private Long uploaderId; //
    private Long courseId;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate; //

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}