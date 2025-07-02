// 课程列表页面，支持课程的筛选、搜索、管理等功能
import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';
import { getEnrolledCourses, getAllCourses, getCoursesByTeacher, enrollCourse } from '../common/CourseStore';
import { Course } from '../common/CourseTypes';

/**
 * 课程列表页面组件
 * 展示所有课程，支持筛选、搜索、管理等操作
 */
@Entry
@Component
struct CourseListPage {
  /** 当前登录用户信息 */
  @State currentUser: UserInfo | null = getCurrentUser();
  /** 全部课程数据（用于筛选） */
  @State allCourses: Course[] = [];
  /** 当前筛选/搜索后的课程数据 */
  @State courses: Course[] = [];
  /** 搜索关键词 */
  @State searchText: string = '';
  /** 当前选中的学院和类别 */
  @State selectedDepartment: string = 'all';
  @State selectedCategory: string = 'all';
  /** 加载状态 */
  @State isLoading: boolean = false;

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadCourses();
  }

  /**
   * 页面每次显示时自动刷新课程数据
   */
  onShow() {
    this.loadCourses();
  }

  /**
   * 加载全部课程数据，并根据筛选条件刷新显示
   */
  loadCourses() {
    this.isLoading = true;
    setTimeout(() => {
      // 统一从全局CourseStore获取所有课程
      this.allCourses = getAllCourses();
      this.filterCourses();
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

      // 搜索和双重筛选
      this.buildSearchAndDoubleFilter()
      
      // 课程列表可滚动
      Scroll() {
        this.buildCourseList()
      }
      .flexGrow(1)
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  /**
   * 搜索与双重筛选区域
   */
  @Builder
  buildSearchAndDoubleFilter() {
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
          .width('85%')
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(20)
          .padding({ left: 15, right: 15 })
          .placeholderColor('#999')
        Button('🔍')
          .width('15%')
          .height(40)
          .backgroundColor('#007AFF')
          .borderRadius(20)
          .fontColor(Color.White)
          .onClick(() => {
            this.filterCourses();
          })
      }
      .width('100%')
      .alignItems(VerticalAlign.Center)
      .margin({ bottom: 10 })

      // 学院筛选Tab
      Scroll() {
        Row() {
          this.buildDepartmentChip('全部学院', 'all')
          ForEach(this.getAllDepartments(), (dep: string) => this.buildDepartmentChip(dep, dep))
        }
        .padding({ left: 20, right: 20 })
      }
      .scrollable(ScrollDirection.Horizontal)
      .margin({ bottom: 10 })

      // 类别筛选Tab
      Scroll() {
        Row() {
          this.buildCategoryChip('全部类别', 'all')
          ForEach(this.getAllCategories(), (cat: string) => this.buildCategoryChip(cat, cat))
        }
        .padding({ left: 20, right: 20 })
      }
      .scrollable(ScrollDirection.Horizontal)
      .margin({ bottom: 10 })
    }
    .padding({ left: 20, right: 20, top: 15 })
    .backgroundColor(Color.White)
  }

  /**
   * 获取所有学院和类别（用于筛选Tab）
   */
  getAllDepartments(): string[] {
    const deps = this.allCourses.map(c => c.department).filter(dep => !!dep);
    return Array.from(new Set(deps));
  }
  getAllCategories(): string[] {
    const cats = this.allCourses.map(c => c.category).filter(cat => !!cat);
    return Array.from(new Set(cats));
  }

  @Builder
  buildDepartmentChip(label: string, value: string) {
    Button(label)
      .fontSize(14)
      .height(32)
      .backgroundColor(this.selectedDepartment === value ? '#007AFF' : '#f0f0f0')
      .fontColor(this.selectedDepartment === value ? Color.White : '#333')
      .borderRadius(16)
      .margin({ right: 10 })
      .onClick(() => {
        this.selectedDepartment = value;
        this.filterCourses();
      })
  }
  @Builder
  buildCategoryChip(label: string, value: string) {
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

  /**
   * 课程列表渲染
   */
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

  /**
   * 单个课程卡片渲染
   */
  @Builder
  buildCourseItem(course: Course) {
    Column() {
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
            .maxLines(1)
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
      }
      .width('100%')
      // 底部操作按钮组横向排列
      Row() {
        if (this.currentUser?.role === 'student') {
          if (course.isEnrolled) {
            Button('继续学习')
              .fontSize(11)
              .height(28)
              .width(70)
              .backgroundColor('#007AFF')
              .borderRadius(14)
              .fontColor(Color.White)
              .margin({ right: 10 })
              .onClick(() => {
                this.enterCourse(course);
              })
          } else {
            Button('加入课程')
              .fontSize(11)
              .height(28)
              .width(70)
              .backgroundColor('#28a745')
              .borderRadius(14)
              .fontColor(Color.White)
              .margin({ right: 10 })
              .onClick(() => {
                this.enrollCourse(course);
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
              this.manageCourse(course);
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
      this.viewCourseDetail(course);
    })
  }

  /**
   * 筛选课程
   */
  filterCourses(): void {
    this.isLoading = true;
    setTimeout(() => {
      let filteredCourses = this.allCourses;
      // 按学院筛选
      if (this.selectedDepartment !== 'all') {
        filteredCourses = filteredCourses.filter(course => course.department === this.selectedDepartment);
      }
      // 按类别筛选
      if (this.selectedCategory !== 'all') {
        filteredCourses = filteredCourses.filter(course => course.category === this.selectedCategory);
      }
      // 按搜索文本筛选
      if (this.searchText.trim()) {
        const searchLower = this.searchText.toLowerCase();
        filteredCourses = filteredCourses.filter(course => 
          course.name.toLowerCase().includes(searchLower) ||
          course.description.toLowerCase().includes(searchLower) ||
          course.teacherName.toLowerCase().includes(searchLower)
        );
      }
      // 保证全部条件下，字段为空的课程也能显示
      if (this.selectedDepartment === 'all' && this.selectedCategory === 'all' && !this.searchText.trim()) {
        filteredCourses = this.allCourses;
      }
      this.courses = filteredCourses;
      this.isLoading = false;
    }, 300);
  }

  viewCourseDetail(course: Course) {
    // TODO: 跳转到课程详情页面
  }

  enterCourse(course: Course) {
    // TODO: 进入课程学习页面
  }

  enrollCourse(course: Course) {
    if (!this.currentUser) return;
    
    // 调用选课函数
    const success = enrollCourse(this.currentUser.id, course.id);
    
    if (success) {
      // 更新课程状态
      course.isEnrolled = true;
      course.enrolledCount++;
      
      // 显示成功提示
      // 这里可以添加Toast提示
      
      // 重新加载课程列表
      this.loadCourses();
    } else {
      // 显示失败提示
      // 这里可以添加Toast提示
    }
  }

  /**
   * 跳转到课程编辑页面
   */
  manageCourse(course: Course) {
    router.pushUrl({ url: 'pages/EditCoursePage', params: { courseId: course.id } });
  }
} 