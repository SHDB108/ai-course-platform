import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';

// 声明FormField接口
interface FormField {
  label: string;
  placeholder: string;
  value: string;
  onChange: (value: string) => void;
  type?: 'text' | 'select' | 'multiline';
  options?: string[];
  multiline?: boolean;
}

interface Feedback {
  id: string;
  type: 'bug' | 'feature' | 'suggestion' | 'complaint';
  title: string;
  content: string;
  contact: string;
  priority: 'low' | 'medium' | 'high';
  status: 'pending' | 'processing' | 'resolved' | 'closed';
  createTime: string;
  userId: string;
  userName: string;
}

// 反馈页面，支持用户提交意见和建议

/**
 * 反馈页面组件
 * 提供用户反馈表单与提交逻辑
 */
@Entry
@Component
struct FeedbackPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State feedbackType: string = 'suggestion';
  @State title: string = '';
  @State content: string = '';
  @State contact: string = '';
  @State priority: string = 'medium';
  @State isSubmitting: boolean = false;

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
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
      Button('\u2190')
        .fontSize(20)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          router.back();
        })
      Text('\u610f\u89c1\u53cd\u9988')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      Button('\u63d0\u4ea4')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor(this.isSubmitting ? '#999' : '#007AFF')
        .onClick(() => {
          if (!this.isSubmitting) {
            this.submitFeedback();
          }
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
        // 反馈类型
        this.buildFormSection('反馈类型', [
          {
            label: '反馈类型',
            placeholder: '请选择反馈类型',
            value: this.feedbackType,
            onChange: (value: string) => {
              this.feedbackType = value;
            },
            type: 'select',
            options: ['bug', 'feature', 'suggestion', 'complaint']
          } as FormField
        ] as FormField[])

        // 基本信息
        this.buildFormSection('基本信息', [
          {
            label: '标题',
            placeholder: '请简要描述您的反馈（必填）',
            value: this.title,
            onChange: (value: string) => {
              this.title = value;
            }
          } as FormField,
          {
            label: '详细描述',
            placeholder: '请详细描述您的问题或建议（必填）',
            value: this.content,
            onChange: (value: string) => {
              this.content = value;
            },
            multiline: true
          } as FormField
        ] as FormField[])

        // 优先级和联系方式
        this.buildFormSection('其他信息', [
          {
            label: '优先级',
            placeholder: '请选择优先级',
            value: this.priority,
            onChange: (value: string) => {
              this.priority = value;
            },
            type: 'select',
            options: ['low', 'medium', 'high']
          } as FormField,
          {
            label: '联系方式',
            placeholder: '请留下您的邮箱或手机号（可选）',
            value: this.contact,
            onChange: (value: string) => {
              this.contact = value;
            }
          } as FormField
        ] as FormField[])

        // 反馈说明
        this.buildInfoSection()
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
          .height(120)
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

  @Builder
  buildInfoSection() {
    Column() {
      Text('反馈说明')
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      Column() {
        this.buildInfoItem('📝 问题反馈', '如果您在使用过程中遇到问题，请详细描述问题现象和复现步骤。')
        this.buildInfoItem('💡 功能建议', '如果您希望平台增加新功能，请说明功能用途和使用场景。')
        this.buildInfoItem('🔧 改进建议', '如果您对现有功能有改进建议，请说明改进方向和预期效果。')
        this.buildInfoItem('⚠️ 投诉举报', '如果您发现违规内容或行为，请提供相关证据和详细说明。')
      }
      .width('100%')
      .padding(16)
      .backgroundColor('#f8f9fa')
      .borderRadius(8)
    }
    .width('100%')
    .padding(20)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 20 })
  }

  @Builder
  buildInfoItem(title: string, description: string) {
    Row() {
      Text(title)
        .fontSize(14)
        .fontWeight(FontWeight.Medium)
        .fontColor('#333')
        .flexGrow(1)
      
      Text(description)
        .fontSize(12)
        .fontColor('#666')
        .flexGrow(2)
        .lineHeight(16)
    }
    .width('100%')
    .margin({ bottom: 12 })
    .alignItems(VerticalAlign.Top)
  }

  submitFeedback() {
    // 验证必填字段
    if (!this.title.trim()) {
      this.showError('请输入反馈标题');
      return;
    }
    if (!this.content.trim()) {
      this.showError('请输入详细描述');
      return;
    }

    this.isSubmitting = true;

    // 模拟提交
    setTimeout(() => {
      const feedback: Feedback = {
        id: Date.now().toString(),
        type: this.feedbackType as 'bug' | 'feature' | 'suggestion' | 'complaint',
        title: this.title,
        content: this.content,
        contact: this.contact,
        priority: this.priority as 'low' | 'medium' | 'high',
        status: 'pending',
        createTime: new Date().toISOString(),
        userId: this.currentUser?.id || '',
        userName: this.currentUser?.realName || ''
      };

      // TODO: 实际提交到服务器
      console.log('提交反馈:', feedback);

      this.showSuccess('反馈提交成功，我们会尽快处理您的反馈！');
      
      // 重置表单
      this.title = '';
      this.content = '';
      this.contact = '';
      this.feedbackType = 'suggestion';
      this.priority = 'medium';
      
      this.isSubmitting = false;

      // 返回上一页
      setTimeout(() => {
        router.back();
      }, 2000);
    }, 1000);
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