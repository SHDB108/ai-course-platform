import router from '@ohos.router';
import { getCurrentUser, UserInfo, UserRole } from '../common/UserStore';
import { getEnrolledCourses, getAllCourses, getCoursesByTeacher, Course } from '../common/CourseStore';
import { getActiveRecommendations, LearningRecommendation } from '../common/LearningStore';
import { getStudentSubmissions, QuizSubmission } from '../common/QuizStore';

@Entry
@Component
struct MainPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State courses: Course[] = [];
  @State recommendations: LearningRecommendation[] = [];
  @State submissions: QuizSubmission[] = [];
  @State recentSubmissions: QuizSubmission[] = [];
  @State currentTab: number = 0;

  aboutToAppear() {
    if (this.currentUser) {
      this.loadUserData();
      this.loadRecommendations();
      this.loadRecentSubmissions();
    }
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

  build() {
    Column() {
      // 顶部导航栏
      this.buildHeader()
      
      // 内容区域
      if (this.currentTab === 0) {
        this.buildHomeTab()
      } else if (this.currentTab === 1) {
        this.buildCourseTab()
      } else if (this.currentTab === 2) {
        this.buildQuizTab()
      } else if (this.currentTab === 3) {
        this.buildProfileTab()
      }
      
      // 底部导航栏
      this.buildBottomNav()
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
    .padding({ left: 20, right: 20, top: 50, bottom: 20 })
    .backgroundColor('#007AFF')
  }

  @Builder
  buildHomeTab() {
    Scroll() {
      Column() {
        // 快速操作卡片
        this.buildQuickActions()
        
        // AI学习推荐
        this.buildRecommendations()
        
        // 最近活动
        this.buildRecentActivity()
      }
      .padding({ left: 20, right: 20, bottom: 100 })
    }
  }

  @Builder
  buildQuickActions() {
    Column() {
      Text('快速操作')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      Grid() {
        // 课程管理
        GridItem() {
          this.buildActionCard('📚', '我的课程', '查看和管理课程', () => {
            router.pushUrl({ url: 'pages/CourseListPage' });
          })
        }
        
        // 测验作业
        GridItem() {
          this.buildActionCard('📝', '测验作业', '查看测验和作业', () => {
            router.pushUrl({ url: 'pages/QuizListPage' });
          })
        }
        
        // 学习进度
        GridItem() {
          this.buildActionCard('📊', '学习进度', '查看学习统计', () => {
            // TODO: 跳转到学习进度页面
          })
        }
        
        // 用户管理（仅管理员）
        if (this.currentUser?.role === 'admin') {
          GridItem() {
            this.buildActionCard('👥', '用户管理', '管理系统用户', () => {
              router.pushUrl({ url: 'pages/UserManagePage' });
            })
          }
        }
      }
      .columnsTemplate('1fr 1fr')
      .rowsTemplate('1fr 1fr')
      .columnsGap(15)
      .rowsGap(15)
    }
    .margin({ bottom: 30 })
  }

  @Builder
  buildActionCard(icon: string, title: string, desc: string, onClick: () => void) {
    Column() {
      Text(icon)
        .fontSize(32)
        .margin({ bottom: 10 })
      
      Text(title)
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 5 })
      
      Text(desc)
        .fontSize(12)
        .fontColor('#666')
        .textAlign(TextAlign.Center)
    }
    .width('100%')
    .height(120)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .justifyContent(FlexAlign.Center)
    .onClick(onClick)
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
      Text('课程功能开发中...')
        .fontSize(16)
        .fontColor('#666')
        .textAlign(TextAlign.Center)
        .margin({ top: 100 })
    }
  }

  @Builder
  buildQuizTab() {
    Column() {
      Text('测验功能开发中...')
        .fontSize(16)
        .fontColor('#666')
        .textAlign(TextAlign.Center)
        .margin({ top: 100 })
    }
  }

  @Builder
  buildProfileTab() {
    Column() {
      Text('个人中心功能开发中...')
        .fontSize(16)
        .fontColor('#666')
        .textAlign(TextAlign.Center)
        .margin({ top: 100 })
    }
  }

  @Builder
  buildBottomNav() {
    Row() {
      this.buildNavItem('🏠', '首页', 0)
      this.buildNavItem('📚', '课程', 1)
      this.buildNavItem('📝', '测验', 2)
      this.buildNavItem('👤', '我的', 3)
    }
    .width('100%')
    .height(60)
    .backgroundColor(Color.White)
    .border({ width: { top: 1 }, color: '#e0e0e0' })
    .position({ x: 0, y: '100%' })
    .translate({ y: -60 })
  }

  @Builder
  buildNavItem(icon: string, label: string, index: number) {
    Column() {
      Text(icon)
        .fontSize(20)
        .fontColor(this.currentTab === index ? '#007AFF' : '#666')
      
      Text(label)
        .fontSize(12)
        .fontColor(this.currentTab === index ? '#007AFF' : '#666')
        .margin({ top: 2 })
    }
    .flexGrow(1)
    .justifyContent(FlexAlign.Center)
    .onClick(() => {
      this.currentTab = index;
    })
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
} 