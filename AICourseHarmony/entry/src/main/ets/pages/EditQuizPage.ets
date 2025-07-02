import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';
import { getQuizById, createQuiz, updateQuiz, Quiz } from '../common/QuizStore';
import { getEnrolledCourses, getAllCourses, Course } from '../common/CourseStore';

// 声明FormField接口
interface FormField {
  label: string;
  placeholder: string;
  value: string;
  onChange: (value: string) => void;
  type?: 'text' | 'number' | 'select' | 'multiline' | 'datetime';
  options?: string[];
  multiline?: boolean;
}

// 声明路由参数接口
interface RouteParams {
  quizId?: string;
}

@Entry
@Component
struct EditQuizPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State quizId: string = '';
  @State isEdit: boolean = false;
  @State courses: Course[] = [];
  @State quizData: Quiz = {
    id: '',
    courseId: '',
    title: '',
    description: '',
    type: 'quiz',
    totalScore: 100,
    timeLimit: 60,
    startTime: '',
    endTime: '',
    status: 'draft',
    isAutoGrade: true,
    createTime: '',
    updateTime: ''
  };

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }

    this.loadCourses();

    // 检查是否有传入的测验ID（编辑模式）
    const params = router.getParams() as RouteParams;
    if (params && params.quizId) {
      this.quizId = params.quizId;
      this.isEdit = true;
      this.loadQuizData();
    } else {
      // 新建模式，设置默认时间
      const now = new Date();
      const startTime = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 明天
      const endTime = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 一周后
      
      this.quizData.startTime = startTime.toISOString().slice(0, 10);
      this.quizData.endTime = endTime.toISOString().slice(0, 10);
    }
  }

  loadCourses() {
    if (this.currentUser?.role === 'teacher') {
      this.courses = getEnrolledCourses(this.currentUser.id);
    } else if (this.currentUser?.role === 'admin') {
      this.courses = getAllCourses();
    }
  }

  loadQuizData() {
    const quiz = getQuizById(this.quizId);
    if (quiz) {
      this.quizData = quiz;
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
      
      Text(this.isEdit ? '编辑测验' : '新建测验')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      
      Button('保存')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          this.saveQuiz();
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
            label: '测验标题',
            placeholder: '请输入测验标题',
            value: this.quizData.title,
            onChange: (value: string) => {
              this.quizData.title = value;
            }
          } as FormField,
          {
            label: '测验描述',
            placeholder: '请输入测验描述',
            value: this.quizData.description,
            onChange: (value: string) => {
              this.quizData.description = value;
            },
            multiline: true
          } as FormField,
          {
            label: '测验类型',
            placeholder: '请选择测验类型',
            value: this.quizData.type,
            onChange: (value: string) => {
              this.quizData.type = value as 'quiz' | 'assignment';
            },
            type: 'select',
            options: ['quiz', 'assignment']
          } as FormField
        ] as FormField[])

        // 课程选择
        this.buildFormSection('课程设置', [
          {
            label: '所属课程',
            placeholder: '请选择课程',
            value: this.quizData.courseId,
            onChange: (value: string) => {
              this.quizData.courseId = value;
            },
            type: 'select',
            options: this.courses.map(course => course.id)
          } as FormField
        ] as FormField[])

        // 测验设置
        this.buildFormSection('测验设置', [
          {
            label: '总分',
            placeholder: '请输入总分',
            value: this.quizData.totalScore.toString(),
            onChange: (value: string) => {
              this.quizData.totalScore = parseInt(value) || 100;
            },
            type: 'number'
          } as FormField,
          {
            label: '时间限制（分钟）',
            placeholder: '请输入时间限制',
            value: this.quizData.timeLimit?.toString() || '60',
            onChange: (value: string) => {
              this.quizData.timeLimit = parseInt(value) || 60;
            },
            type: 'number'
          } as FormField,
          {
            label: '自动批改',
            placeholder: '是否启用自动批改',
            value: this.quizData.isAutoGrade ? 'true' : 'false',
            onChange: (value: string) => {
              this.quizData.isAutoGrade = value === 'true';
            },
            type: 'select',
            options: ['true', 'false']
          } as FormField
        ] as FormField[])

        // 时间设置
        this.buildFormSection('时间设置', [
          {
            label: '开始时间',
            placeholder: '请选择开始时间',
            value: this.quizData.startTime,
            onChange: (value: string) => {
              this.quizData.startTime = value;
            },
            type: 'datetime'
          } as FormField,
          {
            label: '结束时间',
            placeholder: '请选择结束时间',
            value: this.quizData.endTime,
            onChange: (value: string) => {
              this.quizData.endTime = value;
            },
            type: 'datetime'
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
      } else if (field.type === 'datetime') {
        DatePicker({
          start: new Date('2024-01-01'),
          end: new Date('2030-12-31'),
          selected: new Date(field.value || Date.now())
        })
          .onChange((value: DatePickerResult) => {
            const dateStr = `${value.year}-${String(value.month).padStart(2, '0')}-${String(value.day).padStart(2, '0')}`;
            field.onChange(dateStr);
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

  saveQuiz() {
    // 验证必填字段
    if (!this.quizData.title.trim()) {
      this.showError('请输入测验标题');
      return;
    }
    if (!this.quizData.description.trim()) {
      this.showError('请输入测验描述');
      return;
    }
    if (!this.quizData.courseId) {
      this.showError('请选择所属课程');
      return;
    }
    if (!this.quizData.startTime) {
      this.showError('请设置开始时间');
      return;
    }
    if (!this.quizData.endTime) {
      this.showError('请设置结束时间');
      return;
    }

    // 验证时间逻辑
    const startTime = new Date(this.quizData.startTime);
    const endTime = new Date(this.quizData.endTime);
    if (startTime >= endTime) {
      this.showError('结束时间必须晚于开始时间');
      return;
    }

    let success = false;
    if (this.isEdit) {
      // 确保传递完整的Quiz对象
      const updateData: Quiz = {
        id: this.quizData.id,
        courseId: this.quizData.courseId,
        title: this.quizData.title,
        description: this.quizData.description,
        type: this.quizData.type,
        totalScore: this.quizData.totalScore,
        timeLimit: this.quizData.timeLimit,
        startTime: this.quizData.startTime,
        endTime: this.quizData.endTime,
        status: this.quizData.status,
        isAutoGrade: this.quizData.isAutoGrade,
        createTime: this.quizData.createTime,
        updateTime: this.quizData.updateTime
      };
      success = updateQuiz(this.quizId, updateData);
    } else {
      // 确保传递完整的Quiz对象
      const createData: Quiz = {
        id: '',
        courseId: this.quizData.courseId,
        title: this.quizData.title,
        description: this.quizData.description,
        type: this.quizData.type,
        totalScore: this.quizData.totalScore,
        timeLimit: this.quizData.timeLimit,
        startTime: this.quizData.startTime,
        endTime: this.quizData.endTime,
        status: 'draft',
        isAutoGrade: this.quizData.isAutoGrade,
        createTime: '',
        updateTime: ''
      };
      const quizId = createQuiz(createData);
      success = !!quizId;
    }

    if (success) {
      this.showSuccess(this.isEdit ? '测验更新成功' : '测验创建成功');
      setTimeout(() => {
        router.back();
      }, 1500);
    } else {
      this.showError(this.isEdit ? '测验更新失败' : '测验创建失败');
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