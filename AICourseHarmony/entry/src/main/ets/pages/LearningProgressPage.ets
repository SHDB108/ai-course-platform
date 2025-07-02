import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';
import { getCourseProgress, getLearningProgress, LearningProgress } from '../common/LearningStore';
import { getEnrolledCourses } from '../common/CourseStore';
import { Course } from '../common/CourseTypes';

// 学习进度页面，展示用户各课程的学习进度与统计信息

/**
 * 学习进度页面组件
 * 展示当前用户在各课程的学习进度、测验成绩等
 */
@Entry
@Component
struct LearningProgressPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State courses: Course[] = [];
  @State isLoading: boolean = false;
  @State selectedTimeRange: string = 'week'; // week, month, semester

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    
    setTimeout(() => {
      if (this.currentUser) {
        this.courses = getEnrolledCourses(this.currentUser.id);
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
        
        Text('学习进度')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor(Color.White)
      .border({ width: { bottom: 1 }, color: '#e0e0e0' })

      // 内容区域
      Scroll() {
        Column() {
          // 总体统计
          this.buildOverallStats()
          
          // 时间范围选择
          this.buildTimeRangeSelector()
          
          // 课程进度列表
          this.buildCourseProgressList()
        }
        .padding({ left: 20, right: 20, bottom: 100 })
      }
      .flexGrow(1)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildOverallStats() {
    Column() {
      Text('学习统计')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      Row() {
        // 总课程数
        Column() {
          Text(this.courses.length.toString())
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
            .fontColor('#007AFF')
          
          Text('总课程数')
            .fontSize(12)
            .fontColor('#666')
            .margin({ top: 5 })
        }
        .flexGrow(1)
        .alignItems(HorizontalAlign.Center)
        
        // 平均进度
        Column() {
          Text(this.getAverageProgress() + '%')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
            .fontColor('#28a745')
          
          Text('平均进度')
            .fontSize(12)
            .fontColor('#666')
            .margin({ top: 5 })
        }
        .flexGrow(1)
        .alignItems(HorizontalAlign.Center)
        
        // 学习时长
        Column() {
          Text(this.getTotalStudyTime() + 'h')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
            .fontColor('#FF9500')
          
          Text('学习时长')
            .fontSize(12)
            .fontColor('#666')
            .margin({ top: 5 })
        }
        .flexGrow(1)
        .alignItems(HorizontalAlign.Center)
      }
      .width('100%')
      .padding(20)
      .backgroundColor(Color.White)
      .borderRadius(12)
      .margin({ bottom: 20 })
    }
  }

  @Builder
  buildTimeRangeSelector() {
    Row() {
      Text('时间范围')
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 10 })
      
      Row() {
        ForEach(['week', 'month', 'semester'], (range: string) => {
          Button(this.getTimeRangeText(range))
            .fontSize(14)
            .height(32)
            .backgroundColor(this.selectedTimeRange === range ? '#007AFF' : '#f0f0f0')
            .fontColor(this.selectedTimeRange === range ? Color.White : '#333')
            .borderRadius(16)
            .margin({ right: 10 })
            .onClick(() => {
              this.selectedTimeRange = range;
              this.loadData();
            })
        })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.End)
    }
    .width('100%')
    .margin({ bottom: 20 })
  }

  @Builder
  buildCourseProgressList() {
    Column() {
      Text('课程进度')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      if (this.courses.length === 0) {
        Column() {
          Text('📚')
            .fontSize(60)
            .margin({ top: 50, bottom: 20 })
          
          Text('暂无课程')
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 10 })
          
          Text('您还没有选择任何课程')
            .fontSize(14)
            .fontColor('#666')
        }
        .width('100%')
        .alignItems(HorizontalAlign.Center)
      } else {
        ForEach(this.courses, (course: Course) => {
          this.buildCourseProgressItem(course)
        })
      }
    }
  }

  @Builder
  buildCourseProgressItem(course: Course) {
    Column() {
      Row() {
        // 课程封面
        Image(course.coverImage || $r('app.media.foreground'))
          .width(60)
          .height(45)
          .borderRadius(8)
          .backgroundColor('#f0f0f0')
        
        // 课程信息
        Column() {
          Text(course.name)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 5 })
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
          
          Text(course.teacherName)
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 5 })
          
          Row() {
            Text('进度')
              .fontSize(12)
              .fontColor('#999')
            
            Text(`${getCourseProgress(this.currentUser!.id, course.id)}%`)
              .fontSize(12)
              .fontColor('#007AFF')
              .fontWeight(FontWeight.Medium)
              .margin({ left: 5 })
          }
        }
        .alignItems(HorizontalAlign.Start)
        .flexGrow(1)
        .margin({ left: 15 })
        
        // 进度条
        Column() {
          Progress({ value: getCourseProgress(this.currentUser!.id, course.id), total: 100 })
            .width(100)
            .height(6)
            .backgroundColor('#f0f0f0')
            .color('#007AFF')
            .borderRadius(3)
          
          Text(this.getProgressStatus(getCourseProgress(this.currentUser!.id, course.id)))
            .fontSize(12)
            .fontColor(this.getProgressColor(getCourseProgress(this.currentUser!.id, course.id)))
            .margin({ top: 5 })
        }
        .alignItems(HorizontalAlign.End)
      }
      
      // 最近学习记录
      if (getCourseProgress(this.currentUser!.id, course.id) > 0) {
        Row() {
          Text('最近学习：')
            .fontSize(12)
            .fontColor('#999')
          
          Text(this.getLastStudyTime(course.id))
            .fontSize(12)
            .fontColor('#666')
        }
        .width('100%')
        .margin({ top: 10 })
        .justifyContent(FlexAlign.Start)
      }
    }
    .width('100%')
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 15 })
  }

  getAverageProgress(): number {
    if (this.courses.length === 0) return 0;
    
    const totalProgress = this.courses.reduce((sum, course) => {
      return sum + getCourseProgress(this.currentUser!.id, course.id);
    }, 0);
    
    return Math.round(totalProgress / this.courses.length);
  }

  getTotalStudyTime(): number {
    // 模拟计算总学习时长
    return Math.round(this.courses.length * 2.5);
  }

  getTimeRangeText(range: string): string {
    switch (range) {
      case 'week':
        return '本周';
      case 'month':
        return '本月';
      case 'semester':
        return '本学期';
      default:
        return '本周';
    }
  }

  getProgressStatus(progress: number): string {
    if (progress === 0) return '未开始';
    if (progress < 30) return '刚开始';
    if (progress < 70) return '进行中';
    if (progress < 100) return '即将完成';
    return '已完成';
  }

  getProgressColor(progress: number): string {
    if (progress === 0) return '#999';
    if (progress < 30) return '#FF9500';
    if (progress < 70) return '#007AFF';
    if (progress < 100) return '#28a745';
    return '#28a745';
  }

  getLastStudyTime(courseId: string): string {
    // 模拟获取最近学习时间
    const days = Math.floor(Math.random() * 7) + 1;
    if (days === 1) return '昨天';
    if (days === 2) return '前天';
    return `${days}天前`;
  }
} 