package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.vo.teacher.TeacherVO;
import com.example.aicourse.vo.teacher.TeacherOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper 
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 检查教师工号是否存在
     */
    default boolean existsByTeacherNo(String teacherNo) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_no", teacherNo);
        return selectCount(queryWrapper) > 0;
    }
    
    /**
     * 分页查询教师列表
     */
    @Select("""
        <script>
        SELECT 
            t.id, t.teacher_no, t.name, t.department, t.title, t.phone, t.email,
            t.gmt_create as createdAt, t.gmt_modified as updatedAt,
            u.username, u.email as userEmail, u.status,
            0 as courseCount, 0 as studentCount
        FROM t_teacher t
        LEFT JOIN t_user u ON t.id = u.id
        WHERE u.status = 1
        <if test="keyword != null and keyword != ''">
            AND (t.name LIKE CONCAT('%', #{keyword}, '%') 
                 OR t.teacher_no LIKE CONCAT('%', #{keyword}, '%')
                 OR u.username LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="department != null and department != ''">
            AND t.department LIKE CONCAT('%', #{department}, '%')
        </if>
        <if test="status != null and status != ''">
            AND u.status = 1
        </if>
        ORDER BY t.gmt_create DESC
        </script>
    """)
    List<TeacherVO> selectTeachersPage(Page<TeacherVO> page, @Param("keyword") String keyword, 
                                      @Param("department") String department, @Param("status") String status);
    
    /**
     * 获取教师选项列表
     */
    @Select("""
        <script>
        SELECT t.id, t.name, t.teacher_no as teacherNo, t.department
        FROM t_teacher t
        LEFT JOIN t_user u ON t.id = u.id
        WHERE u.status = 1
        <if test="keyword != null and keyword != ''">
            AND (t.name LIKE CONCAT('%', #{keyword}, '%') 
                 OR t.teacher_no LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY t.name
        </script>
    """)
    List<TeacherOptionVO> selectTeacherOptions(@Param("keyword") String keyword);
    
    /**
     * 根据ID查询教师详情
     */
    @Select("""
        SELECT 
            t.id, t.teacher_no, t.name, t.department, t.title, t.phone, t.email,
            t.gmt_create as createdAt, t.gmt_modified as updatedAt,
            u.username, u.email as userEmail, u.status,
            0 as courseCount, 0 as studentCount
        FROM t_teacher t
        LEFT JOIN t_user u ON t.id = u.id
        WHERE t.id = #{id}
    """)
    TeacherVO selectTeacherById(@Param("id") Long id);
}
