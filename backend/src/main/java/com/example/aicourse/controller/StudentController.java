package com.example.aicourse.controller;
import com.example.aicourse.dto.student.StudentCreateDTO;
import com.example.aicourse.dto.student.StudentUpdateDTO;
import com.example.aicourse.entity.Student;
import com.example.aicourse.service.StudentService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.student.ImportResultVO;
import com.example.aicourse.vo.student.StudentVO;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/students")

public class StudentController{
    final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    /**
     * API 3.1 获取学生列表 (分页)
     * @param page 当前页码
     * @param size 每页数量
     * @param keyword 搜索关键词
     * @param major 专业筛选
     * @param grade 年级筛选
     * @return 分页学生列表
     */
    @GetMapping
    public Result<PageVO<StudentVO>> getStudentPage(
            @RequestParam(defaultValue="1") Long page,
            @RequestParam(defaultValue="10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String grade){
        try {
            PageVO<StudentVO> studentPage = service.getStudentPage(page, size, keyword, major, grade);
            return Result.ok(studentPage);
        } catch (Exception e) {
            return Result.error("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * API 3.2 获取学生详情
     * @param id 学生ID
     * @return 学生详细信息
     */
    @GetMapping("/{id}")
    public Result<StudentVO> getStudentDetails(@PathVariable Long id) {
        try {
            StudentVO studentVO = service.getStudentDetails(id);
            if (studentVO == null) {
                return Result.error("学生不存在");
            }
            return Result.ok(studentVO);
        } catch (Exception e) {
            return Result.error("获取学生详情失败: " + e.getMessage());
        }
    }

    /**
     * API 3.3 新增学生
     * @param dto 学生创建请求DTO
     * @return 新创建的学生ID
     */
    @PostMapping
    public Result<Long> createStudent(@Valid @RequestBody StudentCreateDTO dto){
        try {
            Long newStudentId = service.createStudent(dto);
            return Result.ok(newStudentId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 3.4 更新学生信息
     * @param id 学生ID
     * @param dto 学生更新请求DTO
     * @return null
     */
    @PutMapping("/{id}")
    public Result<Void> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateDTO dto){
        try {
            service.updateStudent(id, dto);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 3.5 删除学生
     * @param id 学生ID
     * @return null
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id){
        try {
            service.deleteStudent(id);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 3.6 批量导入学生
     * consumes = MediaType.MULTIPART_FORM_DATA_VALUE
     * @param file 包含学生信息的Excel或CSV文件
     * @return 导入结果
     */
    @PostMapping(value = "/import", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ImportResultVO> importStudents(@RequestPart("file") MultipartFile file) {
        try {
            ImportResultVO result = service.importStudents(file);
            return Result.ok(result);
        } catch (IOException e) {
            return Result.error("文件导入失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("导入过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * API 3.7 导出学生信息
     * @param major 专业筛选
     * @param grade 年级筛选
     * @param format 导出文件格式 ("xlsx" 或 "csv")
     * @return 包含文件资源的ResponseEntity
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> exportStudents(
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String grade,
            @RequestParam(defaultValue = "csv") String format){ // 默认导出csv
        try {
            return service.exportStudents(major, grade, format);
        } catch (IOException e) {
            // 导出失败直接返回500或其他错误状态码，ResponseEntity的错误处理与Result不同
            return ResponseEntity.status(500).body(null);
        }
    }

 @GetMapping public Page<Student> page(@RequestParam(defaultValue="1") long page,@RequestParam(defaultValue="10") long size){
  return service.page(new Page<>(page,size));
 }

 @PostMapping public boolean save(@RequestBody Student s){return service.save(s);}
}