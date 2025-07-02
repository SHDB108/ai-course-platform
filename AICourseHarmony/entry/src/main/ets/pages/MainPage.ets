import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo, UserRole } from '../common/UserTypes';
import { getEnrolledCourses, getAllCourses, getCoursesByTeacher } from '../common/CourseStore';
import { Course } from '../common/CourseTypes';
import { getStudentSubmissions, getAllQuizzes } from '../common/QuizStore';
import { Quiz, QuizSubmission } from '../common/QuizTypes';
import { getActiveRecommendations, LearningRecommendation } from '../common/LearningStore';

interface QuickAction {
  icon: string;
  title: string;
  desc: string;
  onClick: () => void;
}

interface NavItem {
  icon: string;
  label: string;
  index: number;
}

// 主页面，AI课程平台的核心导航与功能入口
// 包含课程、测验、学习进度、用户管理等主要模块的导航与展示

/**
 * 主页面组件
 * 提供平台主要功能的导航入口和概览
 */
@Entry
@Component
struct MainPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State courses: Course[] = [];
  @State recommendations: LearningRecommendation[] = [];
  @State submissions: QuizSubmission[] = [];
  @State recentSubmissions: QuizSubmission[] = [];
  @State currentTab: number = 0;
  @State navItems: NavItem[] = [
    { icon: '🏠', label: '首页', index: 0 },
    { icon: '📚', label: '课程', index: 1 },
    { icon: '📝', label: '测验', index: 2 },
    { icon: '👤', label: '我的', index: 3 }
  ];
  @State actions: QuickAction[] = [];
  @State quizzes: Quiz[] = [];

  aboutToAppear() {
    if (this.currentUser) {
      let actions: QuickAction[] = [];
      if (this.currentUser.role === 'admin') {
        actions = [
          { icon: '👥', title: '用户管理', desc: '管理系统用户', onClick: () => router.pushUrl({ url: 'pages/UserManagePage' }) },
          { icon: '📚', title: '课程管理', desc: '管理所有课程', onClick: () => router.pushUrl({ url: 'pages/CourseListPage' }) },
          { icon: '📝', title: '测验管理', desc: '管理所有测验', onClick: () => router.pushUrl({ url: 'pages/QuizListPage' }) },
          { icon: '📊', title: '学习进度', desc: '查看全员学习统计', onClick: () => router.pushUrl({ url: 'pages/LearningProgressPage' }) },
          { icon: '⚙️', title: '系统设置', desc: '平台参数配置', onClick: () => router.pushUrl({ url: 'pages/SystemSettingPage' }) }
        ];
      } else if (this.currentUser.role === 'teacher') {
        actions = [
          { icon: '📚', title: '课程管理', desc: '管理我的课程', onClick: () => router.pushUrl({ url: 'pages/CourseListPage' }) },
          { icon: '📝', title: '测验管理', desc: '管理课程测验', onClick: () => router.pushUrl({ url: 'pages/QuizListPage' }) },
          { icon: '📊', title: '学习进度', desc: '查看学生学习统计', onClick: () => router.pushUrl({ url: 'pages/LearningProgressPage' }) }
        ];
      } else if (this.currentUser.role === 'student') {
        actions = [
          { icon: '📚', title: '我的课程', desc: '查看和选修课程', onClick: () => router.pushUrl({ url: 'pages/CourseListPage' }) },
          { icon: '📝', title: '测验作业', desc: '参加测验和作业', onClick: () => router.pushUrl({ url: 'pages/QuizListPage' }) },
          { icon: '📊', title: '学习进度', desc: '查看我的学习统计', onClick: () => router.pushUrl({ url: 'pages/LearningProgressPage' }) }
        ];
      }
      this.actions = actions;
      this.loadUserData();
      this.loadRecommendations();
      this.loadRecentSubmissions();
      this.loadQuizzes();
    }
  }

  onShow() {
    this.loadUserData();
  }

  loadUserData() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }

    // 根据用户角色加载不同数据
    if (this.currentUser.role === 'student') {
      this.courses = getEnrolledCourses(this.currentUser.id);
      this.recommendations = getActiveRecommendations(this.currentUser.id);
      this.submissions = getStudentSubmissions(this.currentUser.id);
    } else if (this.currentUser.role === 'teacher') {
      this.courses = getCoursesByTeacher(this.currentUser.id);
    } else if (this.currentUser.role === 'admin') {
      this.courses = getAllCourses();
    }
  }

  loadQuizzes() {
    this.quizzes = getAllQuizzes();
  }

  build() {
    Scroll() {
      Column() {
        this.buildHeader()
        if (this.currentTab === 0) {
          this.buildHomeTabContent()
        } else if (this.currentTab === 1) {
          this.buildCourseTab()
        } else if (this.currentTab === 2) {
          this.buildQuizTab()
        } else if (this.currentTab === 3) {
          this.buildProfileTab()
        }
        this.buildBottomNav()
      }
      .width('100%')
      .backgroundColor('#f5f5f5')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildHeader() {
    Row() {
      Column() {
        Text('AI课程平台')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(Color.White)
        if (this.currentUser) {
          Text(`欢迎，${this.currentUser.realName}`)
            .fontSize(14)
            .fontColor(Color.White)
            .opacity(0.8)
        }
      }
      .alignItems(HorizontalAlign.Start)
      .flexGrow(1)
      Button('设置')
        .fontSize(14)
        .backgroundColor(Color.Transparent)
        .fontColor(Color.White)
        .onClick(() => {
          router.pushUrl({ url: 'pages/SystemSettingPage' });
        })
    }
    .width('100%')
    .height(60)
    .backgroundColor('#007AFF')
  }

  @Builder
  buildHomeTabContent() {
    Column() {
      // 快速操作卡片
      if (this.currentUser) {
        this.buildQuickActions()
      }
      // AI学习推荐
      this.buildRecommendations()
      // 最近活动
      this.buildRecentActivity()
      // 自适应空白填充
      if (this.actions.length < 5) {
        Text(' ')
          .height(300)
      } else {
        Text(' ')
          .height(30)
      }
    }
    .padding({ left: 20, right: 20, bottom: 30 })
  }

  @Builder
  buildQuickActions() {
    Column() {
      Text('快速操作')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      List() {
        ForEach(this.actions, (action: QuickAction, index: number) => {
        ListItem() {
            Button(action.title)
              .width('100%')
              .height(56)
              .margin({ top: 6, bottom: 6 })
              .onClick(action.onClick)
          }
        })
      }
    }
    .margin({ bottom: 30 })
  }

  @Builder
  buildRecommendations() {
    Column() {
      Row() {
        Text('AI学习推荐')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
        
        Button('更多')
          .fontSize(14)
          .backgroundColor(Color.Transparent)
          .fontColor('#007AFF')
          .onClick(() => {
            // TODO: 跳转到推荐页面
          })
      }
      .margin({ bottom: 15 })
      
      if (this.recommendations.length > 0) {
        Column() {
          ForEach(this.recommendations.slice(0, 3), (item: LearningRecommendation) => {
            this.buildRecommendationItem(item)
          })
        }
      } else {
        Text('暂无推荐内容')
          .fontSize(14)
          .fontColor('#999')
          .textAlign(TextAlign.Center)
          .padding({ top: 20, bottom: 20 })
      }
    }
    .margin({ bottom: 30 })
  }

  @Builder
  buildRecommendationItem(item: LearningRecommendation) {
    Row() {
      Column() {
        Text(item.title)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 5 })
        
        Text(item.description)
          .fontSize(14)
          .fontColor('#666')
          .margin({ bottom: 5 })
        
        Text(item.reason)
          .fontSize(12)
          .fontColor('#999')
      }
      .alignItems(HorizontalAlign.Start)
      .flexGrow(1)
      
      Button('查看')
        .fontSize(12)
        .height(30)
        .backgroundColor('#007AFF')
        .borderRadius(15)
        .onClick(() => {
          // TODO: 跳转到推荐内容
        })
    }
    .width('100%')
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 10 })
    .justifyContent(FlexAlign.SpaceBetween)
  }

  @Builder
  buildRecentActivity() {
    Column() {
      Text('最近活动')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      if (this.recentSubmissions.length > 0) {
        Column() {
          ForEach(this.recentSubmissions.slice(0, 5), (item: QuizSubmission) => {
            this.buildActivityItem(item)
          })
        }
      } else {
        Text('暂无最近活动')
          .fontSize(14)
          .fontColor('#999')
          .textAlign(TextAlign.Center)
          .padding({ top: 20, bottom: 20 })
      }
    }
  }

  @Builder
  buildActivityItem(item: QuizSubmission) {
    Row() {
      Text('📝')
        .fontSize(20)
        .margin({ right: 15 })
      
      Column() {
        Text(`提交了测验`)
          .fontSize(14)
          .fontWeight(FontWeight.Medium)
        
        Text(`得分：${item.totalScore}分`)
          .fontSize(12)
          .fontColor('#666')
      }
      .alignItems(HorizontalAlign.Start)
      .flexGrow(1)
      
      Text(this.formatTime(item.submittedTime))
        .fontSize(12)
        .fontColor('#999')
    }
    .width('100%')
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 10 })
  }

  @Builder
  buildCourseTab() {
    Column() {
      if (this.currentUser) {
        if (this.currentUser.role === 'admin') {
          Button('+ 新建课程')
            .width('100%')
            .height(50)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .fontSize(16)
            .margin({ top: 20, bottom: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/EditCoursePage' });
            })
          this.buildCourseList('all')
        } else if (this.currentUser.role === 'teacher') {
          Button('+ 新建课程')
            .width('100%')
            .height(50)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .fontSize(16)
            .margin({ top: 20, bottom: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/EditCoursePage' });
            })
          this.buildCourseList('teacher')
        } else if (this.currentUser.role === 'student') {
          Button('选修新课程')
            .width('100%')
            .height(50)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .fontSize(16)
            .margin({ top: 20, bottom: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/CourseListPage' });
            })
          this.buildCourseList('student')
        }
        // 自适应空白填充
        if (this.courses.length < 5) {
          Text(' ')
            .height(300)
        } else {
          Text(' ')
            .height(30)
        }
      } else {
        Text('请先登录')
          .fontSize(16)
          .fontColor('#666')
          .textAlign(TextAlign.Center)
          .margin({ top: 100 })
      }
    }
  }

  @Builder
  buildQuizTab() {
    Column() {
      if (this.currentUser) {
        if (this.currentUser.role === 'admin') {
          Button('+ 新建测验')
            .width('100%')
            .height(50)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .fontSize(16)
            .margin({ top: 20, bottom: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/EditQuizPage' });
            })
          this.buildQuizList('all')
        } else if (this.currentUser.role === 'teacher') {
          Button('+ 新建测验')
            .width('100%')
            .height(50)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .fontSize(16)
            .margin({ top: 20, bottom: 20 })
            .onClick(() => {
              router.pushUrl({ url: 'pages/EditQuizPage' });
            })
          this.buildQuizList('teacher')
        } else if (this.currentUser.role === 'student') {
          this.buildQuizList('student')
        }
        // 自适应空白填充
        if (this.submissions.length < 5) {
          Text(' ')
            .height(300)
        } else {
          Text(' ')
            .height(30)
        }
      } else {
        Text('请先登录')
          .fontSize(16)
          .fontColor('#666')
          .textAlign(TextAlign.Center)
          .margin({ top: 100 })
      }
    }
  }

  @Builder
  buildProfileTab() {
    Column() {
      if (this.currentUser) {
        Text('个人中心')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .margin({ top: 30, bottom: 20 })
        Text(`角色：${this.getRoleText(this.currentUser.role)}`)
          .fontSize(14)
          .fontColor('#666')
          .margin({ bottom: 20 })
        Button('个人信息')
          .width('100%')
          .height(50)
          .backgroundColor('#f0f0f0')
          .fontColor('#333')
          .fontSize(16)
          .margin({ bottom: 15 })
          .onClick(() => {
            router.pushUrl({ url: 'pages/UserInfoPage' });
          })
        Button('系统设置')
          .width('100%')
          .height(50)
          .backgroundColor('#f0f0f0')
          .fontColor('#333')
          .fontSize(16)
          .margin({ bottom: 15 })
          .onClick(() => {
            router.pushUrl({ url: 'pages/SystemSettingPage' });
          })
        Button('退出登录')
          .width('100%')
          .height(50)
          .backgroundColor('#FF3B30')
          .fontColor(Color.White)
          .fontSize(16)
          .margin({ bottom: 15 })
          .onClick(() => {
            router.replaceUrl({ url: 'pages/LoginPage' });
          })
        // 自适应空白填充
        Text(' ')
          .height(300)
      } else {
        Text('请先登录')
          .fontSize(16)
          .fontColor('#666')
          .textAlign(TextAlign.Center)
          .margin({ top: 100 })
      }
    }
  }

  @Builder
  buildBottomNav() {
    Row() {
      Column() {
        Text('🏠')
          .fontSize(20)
          .fontColor(this.currentTab === 0 ? '#007AFF' : '#666')
        Text('首页')
          .fontSize(12)
          .fontColor(this.currentTab === 0 ? '#007AFF' : '#666')
          .margin({ top: 2 })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.Center)
      .onClick(() => { this.currentTab = 0 })

      Column() {
        Text('📚')
          .fontSize(20)
          .fontColor(this.currentTab === 1 ? '#007AFF' : '#666')
        Text('课程')
          .fontSize(12)
          .fontColor(this.currentTab === 1 ? '#007AFF' : '#666')
          .margin({ top: 2 })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.Center)
      .onClick(() => { this.currentTab = 1 })

      Column() {
        Text('📝')
          .fontSize(20)
          .fontColor(this.currentTab === 2 ? '#007AFF' : '#666')
        Text('测验')
          .fontSize(12)
          .fontColor(this.currentTab === 2 ? '#007AFF' : '#666')
          .margin({ top: 2 })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.Center)
      .onClick(() => { this.currentTab = 2 })

      Column() {
        Text('👤')
          .fontSize(20)
          .fontColor(this.currentTab === 3 ? '#007AFF' : '#666')
        Text('我的')
          .fontSize(12)
          .fontColor(this.currentTab === 3 ? '#007AFF' : '#666')
          .margin({ top: 2 })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.Center)
      .onClick(() => { this.currentTab = 3 })
    }
    .width('100%')
    .height(60)
    .backgroundColor(Color.White)
    .border({ width: { top: 1 }, color: '#e0e0e0' })
  }

  loadRecommendations() {
    if (this.currentUser?.role === 'student') {
      this.recommendations = getActiveRecommendations(this.currentUser.id);
    }
  }

  loadRecentSubmissions() {
    if (this.currentUser?.role === 'student') {
      this.recentSubmissions = getStudentSubmissions(this.currentUser.id);
    }
  }

  formatTime(timeStr: string): string {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (days === 0) {
      return '今天';
    } else if (days === 1) {
      return '昨天';
    } else if (days < 7) {
      return `${days}天前`;
    } else {
      return date.toLocaleDateString();
    }
  }

  @Builder
  buildCourseList(type: string) {
    // 这里只做简单演示，实际可根据type筛选不同课程
    Column() {
      Text(type === 'all' ? '全部课程列表' : type === 'teacher' ? '我的课程列表' : '已选课程列表')
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .margin({ bottom: 10 })
      ForEach(this.courses, (course: Course) => {
        Row() {
          Text(course.name)
            .fontSize(14)
            .margin({ right: 10 })
          Text(course.teacherName)
            .fontSize(12)
            .fontColor('#666')
        }
        .margin({ bottom: 8 })
      })
    }
    .padding({ left: 20, right: 20 })
  }

  @Builder
  buildQuizList(type: string) {
    Column() {
      Text(type === 'all' ? '全部测验列表' : type === 'teacher' ? '课程测验列表' : '可参加的测验列表')
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .margin({ bottom: 10 })
      ForEach(this.quizzes, (quiz: Quiz) => {
        Row() {
          Text(quiz.title)
            .fontSize(14)
            .margin({ right: 10 })
          Text(quiz.type === 'quiz' ? '测验' : '作业')
            .fontSize(12)
            .fontColor('#666')
        }
        .margin({ bottom: 8 })
      })
    }
    .padding({ left: 20, right: 20 })
  }

  getRoleText(role: string): string {
    switch (role) {
      case 'admin':
        return '系统管理员';
      case 'teacher':
        return '教师';
      case 'student':
        return '学生';
      default:
        return '未知';
    }
  }
} 