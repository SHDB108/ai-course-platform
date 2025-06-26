package com.example.aicourse.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.dto.task.TaskSubmissionCreateDTO;
import com.example.aicourse.dto.task.TaskSubmissionGradeDTO;
import com.example.aicourse.entity.Student;
import com.example.aicourse.entity.Task;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.repository.StudentMapper; // 导入 StudentMapper
import com.example.aicourse.repository.TaskMapper; // 导入 TaskMapper
import com.example.aicourse.repository.TaskSubmissionMapper;
import com.example.aicourse.service.StorageService; // 导入 StorageService
import com.example.aicourse.service.TaskSubmissionService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import com.example.aicourse.vo.task.TaskSubmissionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service public class TaskSubmissionServiceImpl extends ServiceImpl<TaskSubmissionMapper,TaskSubmission> implements TaskSubmissionService{

    @Autowired
    private TaskSubmissionMapper taskSubmissionMapper; // 显式注入 mapper
    @Autowired
    private TaskMapper taskMapper; // 用于查询任务信息
    @Autowired
    private StudentMapper studentMapper; // 用于查询学生信息
    @Autowired
    private StorageService storageService; // 用于文件上传

    /**
     * [内部方法] 获取当前用户ID的占位实现 (与AuthServiceImpl中相同，可在通用Util中管理)
     * TODO: 应替换为从Spring Security等安全上下文中获取真实用户ID的逻辑
     * @return 写死的ID 1L
     */
    private Long currentUserId() {
        return 1L; // 假设当前用户ID为1
    }

    /**
     * API 6.1 学生提交任务 (在线/链接提交)
     */
    @Override
    @Transactional
    public Long createSubmissionOnlineOrLink(TaskSubmissionCreateDTO dto) {
        // 校验任务是否存在
        Task task = taskMapper.selectById(dto.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        // 校验学生是否存在
        Student student = studentMapper.selectById(dto.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        TaskSubmission submission = new TaskSubmission();
        BeanUtils.copyProperties(dto, submission);
        submission.setSubmittedAt(LocalDateTime.now()); // 设置提交时间
        submission.setStatus("PENDING"); // 默认状态为待批改

        // answerPath 存储在线/链接内容
        submission.setAnswerPath(dto.getAnswerContent()); // 文档中 answerPath 可为JSON或文本

        int rows = taskSubmissionMapper.insert(submission);
        if (rows == 0) {
            throw new RuntimeException("提交任务失败");
        }
        return submission.getId();
    }

    /**
     * API 6.1 学生提交任务 (文件提交)
     */
    @Override
    @Transactional
    public Long createSubmissionFile(Long taskId, Long studentId, MultipartFile file) throws IOException {
        // 校验任务是否存在
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        // 校验学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 上传文件
        String filePath = storageService.upload(file); // 使用StorageService上传文件

        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(taskId);
        submission.setStudentId(studentId);
        submission.setSubmittedAt(LocalDateTime.now()); // 设置提交时间
        submission.setStatus("PENDING"); // 默认状态为待批改
        submission.setAnswerPath(filePath); // answerPath 存储文件路径

        int rows = taskSubmissionMapper.insert(submission);
        if (rows == 0) {
            throw new RuntimeException("提交任务文件失败");
        }
        return submission.getId();
    }

    /**
     * API 6.2 获取学生在某课程中的所有任务提交
     */
    @Override
    public List<TaskSubmissionVO> getSubmissionsByStudentAndCourse(Long studentId, Long courseId, String status) {
        // TODO: 实际需要通过 JOIN Task 表来过滤 courseId
        // 目前 TaskSubmission 表没有直接的 courseId 字段，只能通过 taskId 间接关联
        // 这里简化为：先查询学生所有提交，然后过滤掉不属于该课程的 (通过查询 Task)
        LambdaQueryWrapper<TaskSubmission> queryWrapper = Wrappers.<TaskSubmission>lambdaQuery()
                .eq(TaskSubmission::getStudentId, studentId);

        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(TaskSubmission::getStatus, status);
        }

        List<TaskSubmission> submissions = taskSubmissionMapper.selectList(queryWrapper);

        // 过滤掉不属于指定课程的提交，并填充拓展字段
        return submissions.stream()
                .map(submission -> {
                    Task task = taskMapper.selectById(submission.getTaskId());
                    // 如果任务不存在或者不属于该课程，则跳过此提交
                    if (task == null || !task.getCourseId().equals(courseId)) {
                        return null; // 返回null，稍后过滤
                    }

                    TaskSubmissionVO vo = new TaskSubmissionVO();
                    BeanUtils.copyProperties(submission, vo);
                    vo.setTaskTitle(task.getTitle()); // 填充任务标题
                    return vo;
                })
                .filter(java.util.Objects::nonNull) // 过滤掉 null
                .collect(Collectors.toList());
    }

    /**
     * API 6.3 获取特定任务的所有提交
     */
    @Override
    public PageVO<TaskSubmissionVO> getSubmissionsByTask(Long taskId, String status, Long pageNum, Long pageSize) {
        Page<TaskSubmission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TaskSubmission> queryWrapper = Wrappers.<TaskSubmission>lambdaQuery()
                .eq(TaskSubmission::getTaskId, taskId);

        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(TaskSubmission::getStatus, status);
        }

        IPage<TaskSubmission> submissionPage = taskSubmissionMapper.selectPage(page, queryWrapper);

        // 获取所有涉及的学生ID，进行批量查询以避免N+1问题
        List<Long> studentIds = submissionPage.getRecords().stream()
                .map(TaskSubmission::getStudentId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Student> studentMap = studentIds.isEmpty() ? Map.of() :
                studentMapper.selectBatchIds(studentIds).stream()
                        .collect(Collectors.toMap(Student::getId, Function.identity()));


        List<TaskSubmissionVO> taskSubmissionVOs = submissionPage.getRecords().stream()
                .map(submission -> {
                    TaskSubmissionVO vo = new TaskSubmissionVO();
                    BeanUtils.copyProperties(submission, vo);

                    // 填充拓展字段：学生姓名和学号
                    Student student = studentMap.get(submission.getStudentId());
                    if (student != null) {
                        vo.setStudentName(student.getName());
                        vo.setStuNo(student.getStuNo());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        PageVO<TaskSubmissionVO> pageVO = new PageVO<>();
        pageVO.setRecords(taskSubmissionVOs);
        pageVO.setTotal(submissionPage.getTotal());
        pageVO.setSize(submissionPage.getSize());
        pageVO.setCurrent(submissionPage.getCurrent());

        return pageVO;
    }

    /**
     * API 6.4 获取单次任务提交详情
     */
    @Override
    public TaskSubmissionVO getSubmissionDetails(Long id) {
        TaskSubmission submission = taskSubmissionMapper.selectById(id);
        if (submission == null) {
            return null;
        }

        TaskSubmissionVO submissionVO = new TaskSubmissionVO();
        BeanUtils.copyProperties(submission, submissionVO);

        // 填充拓展字段：任务标题、学生姓名、学号
        Task task = taskMapper.selectById(submission.getTaskId());
        if (task != null) {
            submissionVO.setTaskTitle(task.getTitle());
        }
        Student student = studentMapper.selectById(submission.getStudentId());
        if (student != null) {
            submissionVO.setStudentName(student.getName());
            submissionVO.setStuNo(student.getStuNo());
        }

        return submissionVO;
    }

    /**
     * API 6.5 更新特定任务提交的得分和反馈 (教师批改)
     */
    @Override
    @Transactional
    public boolean gradeTaskSubmission(Long id, TaskSubmissionGradeDTO dto) {
        TaskSubmission submission = taskSubmissionMapper.selectById(id);
        if (submission == null) {
            throw new RuntimeException("任务提交记录不存在");
        }

        // 校验 graderId 是否有效 (例如，是否是教师或管理员)
        // TODO: 真实项目中需要校验 graderId 的角色和权限

        submission.setScore(dto.getScore());
        submission.setFeedback(dto.getFeedback());
        submission.setGraderId(dto.getGraderId());
        submission.setGradeTime(LocalDateTime.now()); // 设置批改时间
        submission.setStatus("GRADED"); // 更新状态为已批改

        int rows = taskSubmissionMapper.updateById(submission);
        if (rows == 0) {
            throw new RuntimeException("批改任务提交失败");
        }
        return true;
    }

    /**
     * API 6.6 智能批改学生报告/简答题
     */
    @Override
    public IntelligentGradeResultVO intelligentGrade(Long submissionId, IntelligentGradeRequestDTO dto) {
        TaskSubmission submission = taskSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new RuntimeException("任务提交记录不存在");
        }

        // TODO: 实际业务中，这里会调用一个大语言模型（LLM）API
        // 例如：通过 RestTemplate/WebClient 调用外部 AI 服务，传入报告内容（通过 answerPath 获取）
        // 和 gradingRules, llmModel 等参数。
        // 并根据LLM返回的结果填充 score, feedback, suggestions。

        System.out.println("模拟智能批改：任务提交ID " + submissionId);
        System.out.println("批改规则: " + dto.getGradingRules());
        System.out.println("LLM 模型: " + dto.getLlmModel());
        System.out.println("报告路径/内容: " + submission.getAnswerPath());

        // 模拟智能批改结果
        IntelligentGradeResultVO resultVO = new IntelligentGradeResultVO();
        resultVO.setScore(new BigDecimal("88.0")); // 模拟得分
        resultVO.setFeedback("模拟反馈：报告结构清晰，但在某些论点上可以提供更深入的分析。");
        resultVO.setSuggestions(List.of("增加数据支持", "引用更多权威文献", "重新组织第三段的论证逻辑"));

        // (可选) 智能批改后，可以自动更新任务提交的得分和反馈
        // submission.setScore(resultVO.getScore());
        // submission.setFeedback(resultVO.getFeedback());
        // submission.setGraderId(currentUserId()); // 假设批改人是触发智能批改的用户
        // submission.setGradeTime(LocalDateTime.now());
        // submission.setStatus("GRADED");
        // taskSubmissionMapper.updateById(submission);

        return resultVO;
    }
}