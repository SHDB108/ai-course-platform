// 编辑课程页面，支持课程信息的查看、编辑与保存
import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';
import { getCourseById, createCourse, updateCourse } from '../common/CourseStore';
import { Course } from '../common/CourseTypes';

// 声明FormField接口
interface FormField {
  label: string;
  placeholder: string;
  value: string;
  onChange: (value: string) => void;
  type?: 'text' | 'number' | 'select' | 'multiline';
  options?: string[];
  multiline?: boolean;
}

// 声明路由参数接口
interface RouteParams {
  courseId?: string;
}

// 声明Select选项接口
interface SelectOption {
  value: string;
}

/**
 * 编辑课程页面组件
 * 用于展示和编辑指定课程的详细信息
 */
@Entry
@Component
struct EditCoursePage {
  /** 当前编辑的课程ID */
  private courseId: string = '';
  /** 当前课程信息 */
  @State course: Course | null = null;
  /** 编辑状态 */
  @State isEditing: boolean = false;
  @State currentUser: UserInfo | null = getCurrentUser();
  @State isSaving: boolean = false;

  /**
   * 页面加载时初始化课程信息
   */
  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    const params = router.getParams() as RouteParams;
    if (params && params.courseId) {
      this.courseId = params.courseId;
      this.isEditing = true;
      this.loadCourseData();
    }
  }

  loadCourseData() {
    this.course = getCourseById(this.courseId);
    if (!this.course) {
      // 赋默认Course对象，防止undefined
      this.course = {
        id: '',
        courseCode: '',
        name: '',
        title: '',
        description: '',
        credits: 3,
        hours: 48,
        teacherId: '',
        teacherName: '',
        semester: '2024春季',
        department: '计算机学院',
        classroom: 'A101',
        schedule: '周一 1-2节',
        capacity: 50,
        enrolledCount: 0,
        studentCount: 0,
        category: '',
        status: 'active',
        createTime: '',
        updateTime: '',
        coverImage: '',
        isEnrolled: false
      };
    }
  }

  /**
   * 页面主结构
   */
  build() {
    Column() {
      this.buildHeader()
      this.buildForm()
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildHeader() {
    Row() {
      Button('\u2190')
        .fontSize(20)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          router.back();
        })
      Text(this.isEditing ? '\u7f16\u8f91\u8bfe\u7a0b' : '\u65b0\u5efa\u8bfe\u7a0b')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      Button('\u4fdd\u5b58')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .enabled(!this.isSaving)
        .onClick(() => {
          this.saveCourse();
        })
    }
    .width('100%')
    .height(60)
    .backgroundColor(Color.White)
    .padding({ left: 20, right: 20 })
    .border({ width: { bottom: 1 }, color: '#e0e0e0' })
  }

  /**
   * 课程信息编辑表单
   */
  @Builder
  buildForm() {
    Scroll() {
      Column() {
        // 基本信息
        this.buildFormSection('基本信息', [
          {
            label: '课程代码',
            placeholder: '请输入课程代码',
            value: this.course?.courseCode || '',
            onChange: (value: string) => {
              this.course!.courseCode = value;
            }
          } as FormField,
          {
            label: '课程名称',
            placeholder: '请输入课程名称',
            value: this.course?.name || '',
            onChange: (value: string) => {
              this.course!.name = value;
            }
          } as FormField,
          {
            label: '课程描述',
            placeholder: '请输入课程描述',
            value: this.course?.description || '',
            onChange: (value: string) => {
              this.course!.description = value;
            },
            multiline: true
          } as FormField
        ] as FormField[])

        // 课程设置
        this.buildFormSection('课程设置', [
          {
            label: '学分',
            placeholder: '请输入学分',
            value: this.course?.credits.toString() || '',
            onChange: (value: string) => {
              this.course!.credits = parseInt(value) || 3;
            },
            type: 'number'
          } as FormField,
          {
            label: '学时',
            placeholder: '请输入学时',
            value: this.course?.hours.toString() || '',
            onChange: (value: string) => {
              this.course!.hours = parseInt(value) || 48;
            },
            type: 'number'
          } as FormField,
          {
            label: '课程容量',
            placeholder: '请输入课程容量',
            value: this.course?.capacity.toString() || '',
            onChange: (value: string) => {
              this.course!.capacity = parseInt(value) || 50;
            },
            type: 'number'
          } as FormField
        ] as FormField[])

        // 上课信息
        this.buildFormSection('上课信息', [
          {
            label: '学期',
            placeholder: '请选择学期',
            value: this.course?.semester || '',
            onChange: (value: string) => {
              this.course!.semester = value;
            },
            type: 'select',
            options: ['2024春季', '2024秋季', '2025春季']
          } as FormField,
          {
            label: '学院',
            placeholder: '请选择学院',
            value: this.course?.department || '',
            onChange: (value: string) => {
              this.course!.department = value;
            },
            type: 'select',
            options: ['计算机学院', '数学学院', '物理学院', '化学学院']
          } as FormField,
          {
            label: '教室',
            placeholder: '请输入教室',
            value: this.course?.classroom || '',
            onChange: (value: string) => {
              this.course!.classroom = value;
            }
          } as FormField,
          {
            label: '上课时间',
            placeholder: '请输入上课时间',
            value: this.course?.schedule || '',
            onChange: (value: string) => {
              this.course!.schedule = value;
            }
          } as FormField
        ] as FormField[])
      }
      .padding(20)
    }
    .flexGrow(1)
  }

  @Builder
  buildFormSection(title: string, fields: FormField[]) {
    Column() {
      Text(title)
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      ForEach(fields, (field: FormField) => {
        this.buildFormField(field)
      })
    }
    .width('100%')
    .padding(20)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 20 })
  }

  @Builder
  buildFormField(field: FormField) {
    Column() {
      Text(field.label)
        .fontSize(14)
        .fontWeight(FontWeight.Medium)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 8 })
      
      if (field.type === 'select') {
        Select((field.options || []).map(option => ({ value: option } as SelectOption)))
          .selected((field.options || []).indexOf(field.value))
          .onSelect((index: number, value: string) => {
            field.onChange(value);
          })
          .width('100%')
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(8)
      } else if (field.multiline) {
        TextArea({ placeholder: field.placeholder, text: field.value })
          .onChange((value: string) => {
            field.onChange(value);
          })
          .width('100%')
          .height(100)
          .backgroundColor('#f8f9fa')
          .borderRadius(8)
          .padding(12)
      } else {
        TextInput({ 
          placeholder: field.placeholder, 
          text: field.value
        })
          .onChange((value: string) => {
            field.onChange(value);
          })
          .width('100%')
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(8)
          .padding({ left: 12, right: 12 })
      }
    }
    .width('100%')
    .margin({ bottom: 16 })
  }

  /**
   * 保存课程信息
   */
  saveCourse() {
    if (this.isSaving) return;
    this.isSaving = true;
    // 验证必填字段
    if (!this.course?.courseCode.trim()) {
      this.showError('请输入课程代码');
      this.isSaving = false;
      return;
    }
    if (!this.course?.name.trim()) {
      this.showError('请输入课程名称');
      this.isSaving = false;
      return;
    }
    if (!this.course?.description.trim()) {
      this.showError('请输入课程描述');
      this.isSaving = false;
      return;
    }
    if (this.currentUser) {
      this.course.teacherId = this.currentUser.id;
      this.course.teacherName = this.currentUser.realName;
    }
    // 自动补全department/category默认值
    if (!this.course.department) {
      this.course.department = '人工智能学院';
    }
    if (!this.course.category) {
      this.course.category = '基础';
    }
    let success = false;
    if (this.isEditing) {
      // 确保传递完整的Course对象
      const updateData: Course = {
        id: this.course.id,
        courseCode: this.course.courseCode,
        name: this.course.name,
        title: this.course.title,
        description: this.course.description,
        credits: this.course.credits,
        hours: this.course.hours,
        teacherId: this.course.teacherId,
        teacherName: this.course.teacherName,
        semester: this.course.semester,
        department: this.course.department,
        classroom: this.course.classroom,
        schedule: this.course.schedule,
        capacity: this.course.capacity,
        enrolledCount: this.course.enrolledCount,
        studentCount: this.course.studentCount,
        category: this.course.category,
        status: this.course.status,
        createTime: this.course.createTime,
        updateTime: this.course.updateTime,
        coverImage: this.course.coverImage,
        isEnrolled: this.course.isEnrolled
      };
      success = updateCourse(this.courseId, updateData);
    } else {
      // 确保传递完整的Course对象
      const createData: Course = {
        id: '',
        courseCode: this.course.courseCode,
        name: this.course.name,
        title: this.course.title,
        description: this.course.description,
        credits: this.course.credits,
        hours: this.course.hours,
        teacherId: this.course.teacherId,
        teacherName: this.course.teacherName,
        semester: this.course.semester,
        department: this.course.department,
        classroom: this.course.classroom,
        schedule: this.course.schedule,
        capacity: this.course.capacity,
        enrolledCount: 0,
        studentCount: 0,
        category: this.course.category,
        status: 'active',
        createTime: '',
        updateTime: '',
        coverImage: this.course.coverImage,
        isEnrolled: false
      };
      const courseId = createCourse(createData);
      success = !!courseId;
    }
    if (success) {
      this.showSuccess(this.isEditing ? '\u8bfe\u7a0b\u66f4\u65b0\u6210\u529f' : '\u8bfe\u7a0b\u521b\u5efa\u6210\u529f');
      // 立即跳转到课程列表页
      router.replaceUrl({ url: 'pages/CourseListPage' });
    } else {
      this.showError(this.isEditing ? '\u8bfe\u7a0b\u66f4\u65b0\u5931\u8d25' : '\u8bfe\u7a0b\u521b\u5efa\u5931\u8d25');
    }
    this.isSaving = false;
  }

  showError(message: string) {
    console.error(message);
    // TODO: 显示错误提示
  }

  showSuccess(message: string) {
    console.log(message);
    // TODO: 显示成功提示
  }
} 