package com.example.aicourse.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aicourse.dto.task.TaskCreateDTO; // 导入 TaskCreateDTO
import com.example.aicourse.dto.task.TaskUpdateDTO; // 导入 TaskUpdateDTO
import com.example.aicourse.entity.Task;
import com.example.aicourse.vo.PageVO; // 导入 PageVO
import com.example.aicourse.vo.task.TaskVO; // 导入 TaskVO
import java.util.List; // 导入 List

public interface TaskService extends IService<Task>{
    /**
     * API 5.1 获取课程任务列表
     * @param courseId 课程ID
     * @param type 任务类型过滤 (可选)
     * @param status 任务状态过滤 (可选)
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 分页的任务信息列表
     */
    PageVO<TaskVO> getCourseTasks(Long courseId, String type, String status, Long pageNum, Long pageSize);

    /**
     * API 5.2 获取任务详情
     * @param id 任务ID
     * @return 任务详细信息
     */
    TaskVO getTaskDetails(Long id);

    /**
     * API 5.3 创建新任务
     * @param dto 任务创建请求DTO
     * @return 新创建的任务ID
     */
    Long createTask(TaskCreateDTO dto);

    /**
     * API 5.4 更新任务信息
     * @param id 任务ID
     * @param dto 任务更新请求DTO
     * @return 更新成功返回true
     */
    boolean updateTask(Long id, TaskUpdateDTO dto);

    /**
     * API 5.5 删除任务
     * @param id 任务ID
     * @return 删除成功返回true
     */
    boolean deleteTask(Long id);

    /**
     * API 5.6 & 5.7 更新任务状态 (发布/撤销发布)
     * @param id 任务ID
     * @param newStatus 新的任务状态 ("PUBLISHED", "DRAFT", "ARCHIVED"等)
     * @return 更新成功返回true
     */
    boolean updateTaskStatus(Long id, String newStatus);
}