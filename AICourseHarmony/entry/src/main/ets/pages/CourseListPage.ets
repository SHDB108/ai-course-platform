import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';
import { getEnrolledCourses, getAllCourses, getCoursesByTeacher, Course } from '../common/CourseStore';

@Entry
@Component
struct CourseListPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State courses: Course[] = [];
  @State searchText: string = '';
  @State selectedCategory: string = 'all';
  @State isLoading: boolean = false;

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadCourses();
  }

  loadCourses() {
    this.isLoading = true;
    
    // 模拟网络延迟
    setTimeout(() => {
      if (this.currentUser) {
        if (this.currentUser.role === 'student') {
          this.courses = getEnrolledCourses(this.currentUser.id);
        } else if (this.currentUser.role === 'teacher') {
          this.courses = getCoursesByTeacher(this.currentUser.id);
        } else if (this.currentUser.role === 'admin') {
          this.courses = getAllCourses();
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
        
        Text('课程列表')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
        
        Button('+ 新建')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            // TODO: 跳转到新建课程页面
          })
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor(Color.White)
      .border({ width: { bottom: 1 }, color: '#e0e0e0' })

      // 搜索和筛选
      this.buildSearchAndFilter()
      
      // 课程列表
      this.buildCourseList()
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildSearchAndFilter() {
    Column() {
      // 搜索框
      Row() {
        TextInput({ 
          placeholder: '搜索课程名称或教师', 
          text: this.searchText 
        })
          .onChange((value: string) => {
            this.searchText = value;
            this.filterCourses();
          })
          .flexGrow(1)
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(20)
          .padding({ left: 15, right: 15 })
          .placeholderColor('#999')
        
        Button('🔍')
          .width(40)
          .height(40)
          .backgroundColor('#007AFF')
          .borderRadius(20)
          .fontColor(Color.White)
          .margin({ left: 10 })
          .onClick(() => {
            this.filterCourses();
          })
      }
      .margin({ bottom: 15 })

      // 分类筛选
      Scroll() {
        Row() {
          this.buildFilterChip('全部', 'all')
          this.buildFilterChip('人工智能', 'ai')
          this.buildFilterChip('机器学习', 'ml')
          this.buildFilterChip('深度学习', 'dl')
          this.buildFilterChip('计算机视觉', 'cv')
          this.buildFilterChip('自然语言处理', 'nlp')
        }
        .padding({ left: 20, right: 20 })
      }
      .scrollable(ScrollDirection.Horizontal)
      .margin({ bottom: 15 })
    }
    .padding({ left: 20, right: 20, top: 15 })
    .backgroundColor(Color.White)
  }

  @Builder
  buildFilterChip(label: string, value: string) {
    Button(label)
      .fontSize(14)
      .height(32)
      .backgroundColor(this.selectedCategory === value ? '#007AFF' : '#f0f0f0')
      .fontColor(this.selectedCategory === value ? Color.White : '#333')
      .borderRadius(16)
      .margin({ right: 10 })
      .onClick(() => {
        this.selectedCategory = value;
        this.filterCourses();
      })
  }

  @Builder
  buildCourseList() {
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
    } else if (this.courses.length === 0) {
      Column() {
        Text('📚')
          .fontSize(60)
          .margin({ top: 100, bottom: 20 })
        
        Text('暂无课程')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 10 })
        
        Text('您还没有任何课程')
          .fontSize(14)
          .fontColor('#666')
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)
    } else {
      List() {
        ForEach(this.courses, (course: Course) => {
          ListItem() {
            this.buildCourseItem(course)
          }
        })
      }
      .width('100%')
      .height('100%')
      .padding({ left: 20, right: 20 })
    }
  }

  @Builder
  buildCourseItem(course: Course) {
    Row() {
      // 课程封面
      Image(course.coverImage || $r('app.media.foreground'))
        .width(80)
        .height(60)
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
        
        Text(course.description)
          .fontSize(14)
          .fontColor('#666')
          .margin({ bottom: 5 })
          .maxLines(2)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
        
        Row() {
          Text(course.teacherName)
            .fontSize(12)
            .fontColor('#999')
          
          Text('•')
            .fontSize(12)
            .fontColor('#999')
            .margin({ left: 8, right: 8 })
          
          Text(`${course.enrolledCount}人学习`)
            .fontSize(12)
            .fontColor('#999')
          
          Text('•')
            .fontSize(12)
            .fontColor('#999')
            .margin({ left: 8, right: 8 })
          
          Text('人工智能')
            .fontSize(12)
            .fontColor('#999')
        }
      }
      .alignItems(HorizontalAlign.Start)
      .flexGrow(1)
      .margin({ left: 15 })
      
      // 操作按钮
      Column() {
        if (this.currentUser?.role === 'student') {
          if (course.isEnrolled) {
            Button('继续学习')
              .fontSize(12)
              .height(30)
              .backgroundColor('#007AFF')
              .borderRadius(15)
              .fontColor(Color.White)
              .onClick(() => {
                this.enterCourse(course);
              })
          } else {
            Button('加入课程')
              .fontSize(12)
              .height(30)
              .backgroundColor('#28a745')
              .borderRadius(15)
              .fontColor(Color.White)
              .onClick(() => {
                this.enrollCourse(course);
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
              this.manageCourse(course);
            })
        }
      }
      .alignItems(HorizontalAlign.End)
    }
    .width('100%')
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 15 })
    .onClick(() => {
      this.viewCourseDetail(course);
    })
  }

  filterCourses() {
    // TODO: 实现课程筛选逻辑
  }

  viewCourseDetail(course: Course) {
    // TODO: 跳转到课程详情页面
  }

  enterCourse(course: Course) {
    // TODO: 进入课程学习页面
  }

  enrollCourse(course: Course) {
    // TODO: 实现选课功能
  }

  manageCourse(course: Course) {
    // TODO: 跳转到课程管理页面
  }
} 