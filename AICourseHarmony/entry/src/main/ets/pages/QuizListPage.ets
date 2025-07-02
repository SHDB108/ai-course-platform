import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';
import { getQuizzesByCourse, getStudentSubmissions, getAllQuizzes } from '../common/QuizStore';
import { Quiz, QuizSubmission } from '../common/QuizTypes';
import { getEnrolledCourses } from '../common/CourseStore';

/**
 * 测验列表页面，支持测验的筛选、搜索、管理等功能
 */
@Entry
@Component
struct QuizListPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  /** 全部测验数据（用于筛选） */
  @State allQuizzes: Quiz[] = [];
  /** 当前筛选/搜索后的测验数据 */
  @State quizzes: Quiz[] = [];
  @State submissions: QuizSubmission[] = [];
  @State selectedType: string = 'all';
  /** 搜索关键词 */
  @State searchText: string = '';
  /** 加载状态 */
  @State isLoading: boolean = false;

  /**
   * 页面每次显示时自动刷新测验数据
   */
  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadQuizzes();
  }

  /**
   * 加载全部测验数据，并根据筛选条件刷新显示
   */
  loadQuizzes() {
    this.isLoading = true;
    
    // 模拟网络延迟
    setTimeout(() => {
      if (this.currentUser) {
        // 统一从全局QuizStore获取所有测验
        this.allQuizzes = getAllQuizzes();
        if (this.currentUser.role === 'student') {
          this.submissions = getStudentSubmissions(this.currentUser.id);
        }
      }
      this.filterQuizzes();
      this.isLoading = false;
    }, 500);
  }

  /**
   * 页面主结构
   */
  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Button('← 返回')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            router.back();
          })
        
        Text('测验作业')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
        
        Button('+ 新建')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            // TODO: 跳转到新建测验页面
          })
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor(Color.White)
      .border({ width: { bottom: 1 }, color: '#e0e0e0' })

      // 筛选标签
      this.buildFilterTabs()
      
      // 测验列表可滚动
      Scroll() {
        this.buildQuizList()
      }
      .flexGrow(1)
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildFilterTabs() {
    Row() {
      this.buildFilterTab('全部', 'all')
      this.buildFilterTab('测验', 'quiz')
      this.buildFilterTab('作业', 'assignment')
      this.buildFilterTab('已完成', 'completed')
    }
    .width('100%')
    .padding({ left: 20, right: 20, top: 15, bottom: 15 })
    .backgroundColor(Color.White)
  }

  @Builder
  buildFilterTab(label: string, value: string) {
    Button(label)
      .fontSize(14)
      .height(32)
      .backgroundColor(this.selectedType === value ? '#007AFF' : '#f0f0f0')
      .fontColor(this.selectedType === value ? Color.White : '#333')
      .borderRadius(16)
      .margin({ right: 10 })
      .onClick(() => {
        this.selectedType = value;
        this.filterQuizzes();
      })
  }

  /**
   * 测验列表渲染
   */
  @Builder
  buildQuizList() {
    if (this.isLoading) {
      Column() {
        LoadingProgress()
          .width(50)
          .height(50)
          .margin({ top: 100 })
        
        Text('加载中...')
          .fontSize(14)
          .fontColor('#666')
          .margin({ top: 10 })
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)
    } else if (this.quizzes.length === 0) {
      Column() {
        Text('📝')
          .fontSize(60)
          .margin({ top: 100, bottom: 20 })
        
        Text('暂无测验')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 10 })
        
        Text('您还没有任何测验或作业')
          .fontSize(14)
          .fontColor('#666')
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)
    } else {
      List() {
        ForEach(this.quizzes, (quiz: Quiz) => {
          ListItem() {
            this.buildQuizItem(quiz)
          }
        })
      }
      .width('100%')
      .height('100%')
      .padding({ left: 20, right: 20 })
    }
  }

  /**
   * 单个测验卡片渲染
   */
  @Builder
  buildQuizItem(quiz: Quiz) {
    Column() {
      Row() {
        // 测验图标和类型
        Column() {
          Text(quiz.type === 'quiz' ? '📝' : '📋')
            .fontSize(24)
          
          Text(quiz.type === 'quiz' ? '测验' : '作业')
            .fontSize(12)
            .fontColor('#666')
        }
        .alignItems(HorizontalAlign.Center)
        .margin({ right: 15 })
        
        // 测验信息
        Column() {
          Text(quiz.title)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 5 })
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
          
          Text(quiz.description)
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 5 })
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
          
          Row() {
            Text(`总分：${quiz.totalScore}分`)
              .fontSize(12)
              .fontColor('#999')
            
            if (quiz.timeLimit) {
              Text('•')
                .fontSize(12)
                .fontColor('#999')
                .margin({ left: 8, right: 8 })
              
              Text(`限时：${quiz.timeLimit}分钟`)
                .fontSize(12)
                .fontColor('#999')
            }
            
            Text('•')
              .fontSize(12)
              .fontColor('#999')
              .margin({ left: 8, right: 8 })
            
            Text(this.formatTime(quiz.endTime))
              .fontSize(12)
              .fontColor(this.isQuizExpired(quiz.endTime) ? '#FF3B30' : '#999')
          }
        }
        .alignItems(HorizontalAlign.Start)
        .flexGrow(1)
      }
      .width('100%')
      // 底部操作按钮组横向排列
      Row() {
        if (this.currentUser?.role === 'student') {
          if (this.getSubmissionByQuizId(quiz.id)) {
            Button('查看结果')
              .fontSize(11)
              .height(28)
              .width(70)
              .backgroundColor('#007AFF')
              .borderRadius(14)
              .fontColor(Color.White)
              .margin({ right: 10 })
              .onClick(() => {
                this.viewResult(quiz, this.getSubmissionByQuizId(quiz.id));
              })
          } else if (!this.isQuizExpired(quiz.endTime)) {
            Button('开始答题')
              .fontSize(11)
              .height(28)
              .width(70)
              .backgroundColor('#28a745')
              .borderRadius(14)
              .fontColor(Color.White)
              .margin({ right: 10 })
              .onClick(() => {
                this.startQuiz(quiz);
              })
          } else {
            Button('查看详情')
              .fontSize(11)
              .height(28)
              .width(70)
              .backgroundColor('#999')
              .borderRadius(14)
              .fontColor(Color.White)
              .margin({ right: 10 })
              .onClick(() => {
                this.viewQuizDetail(quiz);
              })
          }
        } else {
          Button('管理')
            .fontSize(11)
            .height(28)
            .width(70)
            .backgroundColor('#007AFF')
            .borderRadius(14)
            .fontColor(Color.White)
            .onClick(() => {
              this.manageQuiz(quiz);
            })
        }
      }
      .margin({ top: 8 })
    }
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 15 })
    .onClick(() => {
      this.viewQuizDetail(quiz);
    })
  }

  getSubmissionByQuizId(quizId: string): QuizSubmission | undefined {
    return this.submissions.find(s => s.quizId === quizId);
  }

  /**
   * 筛选测验
   */
  filterQuizzes() {
    this.isLoading = true;
    
    setTimeout(() => {
      let filteredQuizzes = this.allQuizzes;
      
      // 按类型筛选
      if (this.selectedType === 'quiz') {
        filteredQuizzes = filteredQuizzes.filter(quiz => quiz.type === 'quiz');
      } else if (this.selectedType === 'assignment') {
        filteredQuizzes = filteredQuizzes.filter(quiz => quiz.type === 'assignment');
      } else if (this.selectedType === 'completed') {
        filteredQuizzes = filteredQuizzes.filter(quiz => 
          this.submissions.some(sub => sub.quizId === quiz.id)
        );
      }
      
      this.quizzes = filteredQuizzes;
      this.isLoading = false;
    }, 300);
  }

  viewQuizDetail(quiz: Quiz) {
    // TODO: 跳转到测验详情页面
  }

  startQuiz(quiz: Quiz) {
    // TODO: 跳转到答题页面
  }

  viewResult(quiz: Quiz, submission: QuizSubmission | undefined) {
    // TODO: 跳转到结果查看页面
  }

  /**
   * 跳转到测验编辑页面
   */
  manageQuiz(quiz: Quiz) {
    router.pushUrl({ url: 'pages/EditQuizPage', params: { quizId: quiz.id } });
  }

  isQuizExpired(endTime: string): boolean {
    return new Date() > new Date(endTime);
  }

  formatTime(timeStr: string): string {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = date.getTime() - now.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    
    if (diff < 0) {
      return '已结束';
    } else if (days > 0) {
      return `剩余${days}天`;
    } else if (hours > 0) {
      return `剩余${hours}小时`;
    } else {
      return '即将结束';
    }
  }
} 