import { describe, it, expect } from '@ohos/hypium';
import { users, login, logout, getCurrentUser, updateUserInfo } from '../main/ets/common/UserStore';
import { courses, getAllCourses, getCourseById, createCourse, Course } from '../main/ets/common/CourseStore';
import { quizzes, getQuizById } from '../main/ets/common/QuizStore';
import { learningResources } from '../main/ets/common/LearningStore';

export default function CRUDStoreTest() {
  describe('UserStore_CRUD', () => {
    it('should_login_and_get_current_user', 0, () => {
      let user = login('admin', 'admin123');
      expect(user != null).assertTrue();
      expect(getCurrentUser() != null).assertTrue();
      logout();
      expect(getCurrentUser() == null).assertTrue();
    });
    it('should_update_user_info', 0, () => {
      login('admin', 'admin123');
      let result = updateUserInfo({ realName: '新管理员' });
      expect(result).assertTrue();
      let user = getCurrentUser();
      expect(user != null).assertTrue();
      if (user) {
        expect(user.realName).assertEqual('新管理员');
      }
      logout();
    });
  });

  describe('CourseStore_CRUD', () => {
    it('should_get_all_courses_and_get_by_id', 0, () => {
      let all = getAllCourses();
      expect(Array.isArray(all)).assertTrue();
      let course = getCourseById('1');
      expect(course != null).assertTrue();
    });
    it('should_create_a_new_course', 0, () => {
      let newCourse: Course = {
        id: '',
        courseCode: 'CS999',
        name: '测试课程',
        title: '测试课程',
        description: '自动化测试用',
        credits: 2,
        hours: 32,
        teacherId: '2',
        teacherName: '张教授',
        semester: '2024秋季',
        department: '测试学院',
        classroom: 'B101',
        schedule: '周三 1-2节',
        capacity: 30,
        enrolledCount: 0,
        studentCount: 0,
        category: '测试',
        coverImage: undefined,
        isEnrolled: undefined,
        status: 'active',
        createTime: '',
        updateTime: ''
      };
      let id = createCourse(newCourse);
      expect(typeof id).assertEqual('string');
      let course = getCourseById(id);
      expect(course != null).assertTrue();
      if (course) {
        expect(course.name).assertEqual('测试课程');
      }
    });
  });

  describe('QuizStore_CRUD', () => {
    it('should_get_quiz_by_id', 0, () => {
      let quiz = getQuizById('1');
      expect(quiz != null).assertTrue();
      if (quiz) {
        expect(quiz.title.indexOf('基础') >= 0).assertTrue();
      }
    });
  });

  describe('LearningStore_CRUD', () => {
    it('should_get_learning_resources', 0, () => {
      expect(Array.isArray(learningResources)).assertTrue();
      expect(learningResources.length > 0).assertTrue();
    });
  });
} 