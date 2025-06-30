import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';
import { getQuizzesByCourse, getStudentSubmissions, Quiz, QuizSubmission } from '../common/QuizStore';
import { getEnrolledCourses } from '../common/CourseStore';

@Entry
@Component
struct QuizListPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State quizzes: Quiz[] = [];
  @State submissions: QuizSubmission[] = [];
  @State selectedType: string = 'all';
  @State isLoading: boolean = false;

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadQuizzes();
  }

  loadQuizzes() {
    this.isLoading = true;
    
    // 模拟网络延迟
    setTimeout(() => {
      if (this.currentUser) {
        // 这里应该根据用户角色和课程加载测验
        // 暂时加载所有测验作为示例
        this.quizzes = [
          {
            id: '1',
            courseId: '1',
            title: '人工智能基础知识测验',
            description: '测试对人工智能基本概念的理解',
            type: 'quiz',
            totalScore: 100,
            timeLimit: 60,
            startTime: '2024-01-15 09:00:00',
            endTime: '2024-01-15 10:00:00',
            status: 'published',
            isAutoGrade: true,
            createTime: '2024-01-10 00:00:00',
            updateTime: '2024-01-10 00:00:00'
          },
          {
            id: '2',
            courseId: '1',
            title: 'AI应用案例分析作业',
            description: '分析一个实际的人工智能应用案例',
            type: 'assignment',
            totalScore: 50,
            startTime: '2024-01-20 00:00:00',
            endTime: '2024-01-27 23:59:59',
            status: 'published',
            isAutoGrade: false,
            createTime: '2024-01-18 00:00:00',
            updateTime: '2024-01-18 00:00:00'
          }
        ];
        
        if (this.currentUser.role === 'student') {
          this.submissions = getStudentSubmissions(this.currentUser.id);
        }
      }
      this.isLoading = false;
    }, 500);
  }

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
      
      // 测验列表
      this.buildQuizList()
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
            .maxLines(2)
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
        
        // 状态和操作
        Column() {
          // 状态标签
          if (this.getSubmissionByQuizId(quiz.id)) {
            Text('已完成')
              .fontSize(12)
              .padding({ left: 8, right: 8, top: 4, bottom: 4 })
              .backgroundColor('#28a745')
              .fontColor(Color.White)
              .borderRadius(10)
              .margin({ bottom: 8 })
            
            Text(`得分：${this.getSubmissionByQuizId(quiz.id)?.totalScore || 0}`)
              .fontSize(12)
              .fontColor('#28a745')
          } else if (this.isQuizExpired(quiz.endTime)) {
            Text('已过期')
              .fontSize(12)
              .padding({ left: 8, right: 8, top: 4, bottom: 4 })
              .backgroundColor('#FF3B30')
              .fontColor(Color.White)
              .borderRadius(10)
              .margin({ bottom: 8 })
          } else {
            Text('进行中')
              .fontSize(12)
              .padding({ left: 8, right: 8, top: 4, bottom: 4 })
              .backgroundColor('#007AFF')
              .fontColor(Color.White)
              .borderRadius(10)
              .margin({ bottom: 8 })
          }
          
          // 操作按钮
          if (this.currentUser?.role === 'student') {
            if (this.getSubmissionByQuizId(quiz.id)) {
              Button('查看结果')
                .fontSize(12)
                .height(30)
                .backgroundColor('#007AFF')
                .borderRadius(15)
                .fontColor(Color.White)
                .onClick(() => {
                  this.viewResult(quiz, this.getSubmissionByQuizId(quiz.id));
                })
            } else if (!this.isQuizExpired(quiz.endTime)) {
              Button('开始答题')
                .fontSize(12)
                .height(30)
                .backgroundColor('#28a745')
                .borderRadius(15)
                .fontColor(Color.White)
                .onClick(() => {
                  this.startQuiz(quiz);
                })
            } else {
              Button('查看详情')
                .fontSize(12)
                .height(30)
                .backgroundColor('#999')
                .borderRadius(15)
                .fontColor(Color.White)
                .onClick(() => {
                  this.viewQuizDetail(quiz);
                })
            }
          } else {
            Button('管理')
              .fontSize(12)
              .height(30)
              .backgroundColor('#007AFF')
              .borderRadius(15)
              .fontColor(Color.White)
              .onClick(() => {
                this.manageQuiz(quiz);
              })
          }
        }
        .alignItems(HorizontalAlign.End)
      }
    }
    .width('100%')
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

  filterQuizzes() {
    // TODO: 实现测验筛选逻辑
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

  manageQuiz(quiz: Quiz) {
    // TODO: 跳转到测验管理页面
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