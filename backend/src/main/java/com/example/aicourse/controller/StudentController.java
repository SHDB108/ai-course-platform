package com.example.aicourse.controller;
import com.example.aicourse.entity.Student;
import com.example.aicourse.service.StudentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
@RestController @RequestMapping("/api/v1/students")
public class StudentController{
 @Autowired StudentService service;
 @GetMapping public Page<Student> page(@RequestParam(defaultValue="1") long page,@RequestParam(defaultValue="10") long size){
  return service.page(new Page<>(page,size));
 }
 @PostMapping public boolean save(@RequestBody Student s){return service.save(s);}
}