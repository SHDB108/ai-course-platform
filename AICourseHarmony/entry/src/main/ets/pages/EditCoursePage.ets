import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';
import { getCourseById, createCourse, updateCourse, Course } from '../common/CourseStore';

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

@Entry
@Component
struct EditCoursePage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State courseId: string = '';
  @State isEdit: boolean = false;
  @State courseData: Course = {
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

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    const params = router.getParams() as RouteParams;
    if (params && params.courseId) {
      this.courseId = params.courseId;
      this.isEdit = true;
      this.loadCourseData();
    }
  }

  loadCourseData() {
    const course = getCourseById(this.courseId);
    if (course) {
      this.courseData = course;
    } else {
      // 赋默认Course对象，防止undefined
      this.courseData = {
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
      Button('←')
        .fontSize(20)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          router.back();
        })
      
      Text(this.isEdit ? '编辑课程' : '新建课程')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      
      Button('保存')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
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

  @Builder
  buildForm() {
    Scroll() {
      Column() {
        // 基本信息
        this.buildFormSection('基本信息', [
          {
            label: '课程代码',
            placeholder: '请输入课程代码',
            value: this.courseData.courseCode,
            onChange: (value: string) => {
              this.courseData.courseCode = value;
            }
          } as FormField,
          {
            label: '课程名称',
            placeholder: '请输入课程名称',
            value: this.courseData.name,
            onChange: (value: string) => {
              this.courseData.name = value;
            }
          } as FormField,
          {
            label: '课程描述',
            placeholder: '请输入课程描述',
            value: this.courseData.description,
            onChange: (value: string) => {
              this.courseData.description = value;
            },
            multiline: true
          } as FormField
        ] as FormField[])

        // 课程设置
        this.buildFormSection('课程设置', [
          {
            label: '学分',
            placeholder: '请输入学分',
            value: this.courseData.credits.toString(),
            onChange: (value: string) => {
              this.courseData.credits = parseInt(value) || 3;
            },
            type: 'number'
          } as FormField,
          {
            label: '学时',
            placeholder: '请输入学时',
            value: this.courseData.hours.toString(),
            onChange: (value: string) => {
              this.courseData.hours = parseInt(value) || 48;
            },
            type: 'number'
          } as FormField,
          {
            label: '课程容量',
            placeholder: '请输入课程容量',
            value: this.courseData.capacity.toString(),
            onChange: (value: string) => {
              this.courseData.capacity = parseInt(value) || 50;
            },
            type: 'number'
          } as FormField
        ] as FormField[])

        // 上课信息
        this.buildFormSection('上课信息', [
          {
            label: '学期',
            placeholder: '请选择学期',
            value: this.courseData.semester,
            onChange: (value: string) => {
              this.courseData.semester = value;
            },
            type: 'select',
            options: ['2024春季', '2024秋季', '2025春季']
          } as FormField,
          {
            label: '学院',
            placeholder: '请选择学院',
            value: this.courseData.department,
            onChange: (value: string) => {
              this.courseData.department = value;
            },
            type: 'select',
            options: ['计算机学院', '数学学院', '物理学院', '化学学院']
          } as FormField,
          {
            label: '教室',
            placeholder: '请输入教室',
            value: this.courseData.classroom,
            onChange: (value: string) => {
              this.courseData.classroom = value;
            }
          } as FormField,
          {
            label: '上课时间',
            placeholder: '请输入上课时间',
            value: this.courseData.schedule,
            onChange: (value: string) => {
              this.courseData.schedule = value;
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

  saveCourse() {
    // 验证必填字段
    if (!this.courseData.courseCode.trim()) {
      this.showError('请输入课程代码');
      return;
    }
    if (!this.courseData.name.trim()) {
      this.showError('请输入课程名称');
      return;
    }
    if (!this.courseData.description.trim()) {
      this.showError('请输入课程描述');
      return;
    }
    if (this.currentUser) {
      this.courseData.teacherId = this.currentUser.id;
      this.courseData.teacherName = this.currentUser.realName;
    }
    let success = false;
    if (this.isEdit) {
      // 确保传递完整的Course对象
      const updateData: Course = {
        id: this.courseData.id,
        courseCode: this.courseData.courseCode,
        name: this.courseData.name,
        title: this.courseData.title,
        description: this.courseData.description,
        credits: this.courseData.credits,
        hours: this.courseData.hours,
        teacherId: this.courseData.teacherId,
        teacherName: this.courseData.teacherName,
        semester: this.courseData.semester,
        department: this.courseData.department,
        classroom: this.courseData.classroom,
        schedule: this.courseData.schedule,
        capacity: this.courseData.capacity,
        enrolledCount: this.courseData.enrolledCount,
        studentCount: this.courseData.studentCount,
        category: this.courseData.category,
        status: this.courseData.status,
        createTime: this.courseData.createTime,
        updateTime: this.courseData.updateTime,
        coverImage: this.courseData.coverImage,
        isEnrolled: this.courseData.isEnrolled
      };
      success = updateCourse(this.courseId, updateData);
    } else {
      // 确保传递完整的Course对象
      const createData: Course = {
        id: '',
        courseCode: this.courseData.courseCode,
        name: this.courseData.name,
        title: this.courseData.title,
        description: this.courseData.description,
        credits: this.courseData.credits,
        hours: this.courseData.hours,
        teacherId: this.courseData.teacherId,
        teacherName: this.courseData.teacherName,
        semester: this.courseData.semester,
        department: this.courseData.department,
        classroom: this.courseData.classroom,
        schedule: this.courseData.schedule,
        capacity: this.courseData.capacity,
        enrolledCount: 0,
        studentCount: 0,
        category: this.courseData.category,
        status: 'active',
        createTime: '',
        updateTime: '',
        coverImage: this.courseData.coverImage,
        isEnrolled: false
      };
      const courseId = createCourse(createData);
      success = !!courseId;
    }
    if (success) {
      this.showSuccess(this.isEdit ? '课程更新成功' : '课程创建成功');
      setTimeout(() => {
        router.back();
      }, 1500);
    } else {
      this.showError(this.isEdit ? '课程更新失败' : '课程创建失败');
    }
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