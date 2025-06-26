package com.example.aicourse.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.dto.task.TaskCreateDTO;
import com.example.aicourse.dto.task.TaskUpdateDTO;
import com.example.aicourse.entity.Task;
import com.example.aicourse.repository.TaskMapper;
import com.example.aicourse.service.TaskService;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.task.TaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service public class TaskServiceImpl extends ServiceImpl<TaskMapper,Task> implements TaskService{

    @Autowired
    private TaskMapper taskMapper; // 显式注入 mapper

    /**
     * API 5.1 获取课程任务列表
     */
    @Override
    public PageVO<TaskVO> getCourseTasks(Long courseId, String type, String status, Long pageNum, Long pageSize) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> queryWrapper = Wrappers.<Task>lambdaQuery();

        queryWrapper.eq(Task::getCourseId, courseId); // 必须属于特定课程

        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(Task::getType, type);
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(Task::getStatus, status);
        }

        Page<Task> taskPage = taskMapper.selectPage(page, queryWrapper);

        List<TaskVO> taskVOs = taskPage.getRecords().stream()
                .map(task -> {
                    TaskVO vo = new TaskVO();
                    BeanUtils.copyProperties(task, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        PageVO<TaskVO> pageVO = new PageVO<>();
        pageVO.setRecords(taskVOs);
        pageVO.setTotal(taskPage.getTotal());
        pageVO.setSize(taskPage.getSize());
        pageVO.setCurrent(taskPage.getCurrent());

        return pageVO;
    }

    /**
     * API 5.2 获取任务详情
     */
    @Override
    public TaskVO getTaskDetails(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            return null; // 或者抛出 TaskNotFoundException
        }
        TaskVO taskVO = new TaskVO();
        BeanUtils.copyProperties(task, taskVO);
        return taskVO;
    }

    /**
     * API 5.3 创建新任务
     */
    @Override
    @Transactional
    public Long createTask(TaskCreateDTO dto) {
        // 校验任务名称在课程内是否重复 (可选)
        if (taskMapper.selectCount(Wrappers.<Task>lambdaQuery()
                .eq(Task::getCourseId, dto.getCourseId())
                .eq(Task::getTitle, dto.getTitle())) > 0) {
            throw new RuntimeException("该课程下已存在同名任务");
        }
        // TODO: 校验 courseId 和 creatorId 是否有效（课程是否存在，创建者是否存在且为教师）

        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setStatus("DRAFT"); // 新创建的任务默认为草稿状态

        int rows = taskMapper.insert(task);
        if (rows == 0) {
            throw new RuntimeException("创建任务失败");
        }
        return task.getId(); // MyBatis-Plus插入后会回填ID
    }

    /**
     * API 5.4 更新任务信息
     */
    @Override
    @Transactional
    public boolean updateTask(Long id, TaskUpdateDTO dto) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 校验更新后的任务名称在课程内是否重复 (如果名称被修改且新名称已存在于其他任务)
        if (StringUtils.isNotBlank(dto.getTitle()) && !dto.getTitle().equals(task.getTitle())) {
            if (taskMapper.selectCount(Wrappers.<Task>lambdaQuery()
                    .eq(Task::getCourseId, task.getCourseId()) // 同一课程下
                    .eq(Task::getTitle, dto.getTitle())
                    .ne(Task::getId, id)) > 0) { // 排除当前任务本身
                throw new RuntimeException("该课程下已存在同名任务");
            }
        }
        // TODO: 校验更新的 deadline 是否在未来 (如果使用 @Future 注解，可以省略这里)

        BeanUtils.copyProperties(dto, task); // 将DTO中的非空属性复制到实体
        task.setId(id); // 确保ID不被覆盖

        int rows = taskMapper.updateById(task);
        if (rows == 0) {
            throw new RuntimeException("更新任务信息失败");
        }
        return true;
    }

    /**
     * API 5.5 删除任务
     */
    @Override
    public boolean deleteTask(Long id) {
        // TODO: 删除任务前，需要检查是否有相关的任务提交记录、测验记录等。
        // 如果有，可能需要先删除相关记录，或者不允许删除。
        // 这里简化为直接删除
        int rows = taskMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("删除任务失败，任务不存在");
        }
        return true;
    }

    /**
     * API 5.6 & 5.7 更新任务状态 (发布/撤销发布)
     */
    @Override
    @Transactional
    public boolean updateTaskStatus(Long id, String newStatus) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 校验新的状态值是否合法（可根据实际需求扩展合法状态列表）
        if (!("DRAFT".equals(newStatus) || "PUBLISHED".equals(newStatus) || "ARCHIVED".equals(newStatus))) {
            throw new RuntimeException("无效的任务状态: " + newStatus);
        }

        task.setStatus(newStatus);
        int rows = taskMapper.updateById(task);
        if (rows == 0) {
            throw new RuntimeException("更新任务状态失败");
        }
        return true;
    }
}