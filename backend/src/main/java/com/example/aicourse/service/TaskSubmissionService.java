package com.example.aicourse.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.dto.task.TaskSubmissionCreateDTO; // 导入 TaskSubmissionCreateDTO
import com.example.aicourse.dto.task.TaskSubmissionGradeDTO; // 导入 TaskSubmissionGradeDTO
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import com.example.aicourse.vo.task.TaskSubmissionVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskSubmissionService extends IService<TaskSubmission>{

    /**
     * API 6.1 学生提交任务 (在线/链接提交)
     * @param dto 任务提交DTO
     * @return 新创建的任务提交ID
     */
    Long createSubmissionOnlineOrLink(TaskSubmissionCreateDTO dto);

    /**
     * API 6.1 学生提交任务 (文件提交)
     * @param taskId 任务ID
     * @param studentId 学生ID
     * @param file 提交的文件
     * @return 新创建的任务提交ID
     * @throws IOException 文件上传异常
     */
    Long createSubmissionFile(Long taskId, Long studentId, MultipartFile file) throws IOException;

    /**
     * API 6.2 获取学生在某课程中的所有任务提交
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param status 提交状态过滤 (可选)
     * @return 任务提交信息列表
     */
    List<TaskSubmissionVO> getSubmissionsByStudentAndCourse(Long studentId, Long courseId, String status);

    /**
     * API 6.3 获取特定任务的所有提交
     * @param taskId 任务ID
     * @param status 提交状态过滤 (可选)
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 分页的任务提交信息列表
     */
    PageVO<TaskSubmissionVO> getSubmissionsByTask(Long taskId, String status, Long pageNum, Long pageSize);

    /**
     * API 6.4 获取单次任务提交详情
     * @param id 任务提交ID
     * @return 任务提交详细信息
     */
    TaskSubmissionVO getSubmissionDetails(Long id);

    /**
     * API 6.5 更新特定任务提交的得分和反馈 (教师批改)
     * @param id 任务提交ID
     * @param dto 批改请求DTO
     * @return 批改成功返回true
     */
    boolean gradeTaskSubmission(Long id, TaskSubmissionGradeDTO dto);

    /**
     * API 6.6 智能批改学生报告/简答题
     * @param submissionId 任务提交ID
     * @param dto 智能批改请求DTO
     * @return 智能批改结果
     */
    IntelligentGradeResultVO intelligentGrade(Long submissionId, IntelligentGradeRequestDTO dto);
}