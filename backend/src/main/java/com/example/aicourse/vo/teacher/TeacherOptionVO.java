package com.example.aicourse.vo.teacher;

/**
 * 教师选项VO（用于下拉选择）
 */
public class TeacherOptionVO {
    
    private Long id;
    private String name;
    private String teacherNo;
    private String department;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}