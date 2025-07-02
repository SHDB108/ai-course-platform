import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';

@Entry
@Component
struct PrivacyPolicyPage {
  @State currentUser: UserInfo | null = getCurrentUser();

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
  }

  build() {
    Column() {
      this.buildHeader()
      this.buildContent()
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
      
      Text('隐私政策')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      
      Button('')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor(Color.Transparent)
    }
    .width('100%')
    .height(60)
    .backgroundColor(Color.White)
    .padding({ left: 20, right: 20 })
    .border({ width: { bottom: 1 }, color: '#e0e0e0' })
  }

  @Builder
  buildContent() {
    Scroll() {
      Column() {
        // 标题
        Text('AI课程平台隐私政策')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .textAlign(TextAlign.Center)
          .width('100%')
          .margin({ top: 30, bottom: 30 })

        // 政策内容
        this.buildSection('引言', [
          '我们非常重视您的隐私保护。本隐私政策说明了我们如何收集、使用、存储和保护您的个人信息。',
          '请您仔细阅读本政策，了解我们对您个人信息的处理方式。如果您不同意本政策的任何内容，请停止使用我们的服务。'
        ])

        this.buildSection('信息收集', [
          '我们收集的信息包括：',
          '• 账户信息：用户名、邮箱、手机号等注册信息',
          '• 学习数据：课程学习进度、测验成绩、学习时长等',
          '• 设备信息：设备型号、操作系统、IP地址等',
          '• 使用记录：登录时间、操作日志、浏览记录等'
        ])

        this.buildSection('信息使用', [
          '我们使用收集的信息用于：',
          '• 提供和改进服务：根据您的学习情况推荐课程',
          '• 个性化体验：为您提供定制化的学习内容',
          '• 安全保障：检测和防止欺诈、滥用等行为',
          '• 客户支持：响应您的咨询和反馈',
          '• 统计分析：分析平台使用情况，优化服务'
        ])

        this.buildSection('信息共享', [
          '我们不会出售、出租或交易您的个人信息。在以下情况下，我们可能会共享您的信息：',
          '• 获得您的明确同意',
          '• 法律法规要求',
          '• 保护我们的合法权益',
          '• 与授权合作伙伴共享（如云服务提供商）'
        ])

        this.buildSection('信息存储', [
          '• 我们采用行业标准的安全措施保护您的信息',
          '• 信息存储在中国境内的服务器上',
          '• 我们会在必要期间内保留您的信息',
          '• 当您删除账户时，我们会删除相关个人信息'
        ])

        this.buildSection('信息安全', [
          '我们采取多种安全措施保护您的信息：',
          '• 数据加密：传输和存储时使用加密技术',
          '• 访问控制：限制对个人信息的访问权限',
          '• 安全审计：定期进行安全评估和漏洞扫描',
          '• 员工培训：对员工进行隐私保护培训'
        ])

        this.buildSection('您的权利', [
          '根据相关法律法规，您享有以下权利：',
          '• 访问权：查询、复制您的个人信息',
          '• 更正权：更正或补充不准确的个人信息',
          '• 删除权：在特定情况下删除您的个人信息',
          '• 撤回同意：撤回您之前给予的同意',
          '• 投诉举报：向监管部门投诉我们的处理行为'
        ])

        this.buildSection('未成年人保护', [
          '我们非常重视未成年人的隐私保护：',
          '• 我们不会故意收集14岁以下儿童的个人信息',
          '• 如果发现收集了儿童信息，我们会立即删除',
          '• 建议未成年人在监护人指导下使用我们的服务'
        ])

        this.buildSection('Cookie使用', [
          '我们使用Cookie和类似技术：',
          '• 记住您的登录状态和偏好设置',
          '• 分析网站使用情况，改进用户体验',
          '• 提供个性化内容和广告',
          '• 您可以通过浏览器设置管理Cookie'
        ])

        this.buildSection('政策更新', [
          '我们可能会不时更新本隐私政策：',
          '• 更新后的政策将在平台上公布',
          '• 重大变更会通过邮件或站内信通知您',
          '• 继续使用服务即表示接受更新后的政策'
        ])

        this.buildSection('联系我们', [
          '如果您对本隐私政策有任何疑问，请联系我们：',
          '• 邮箱：privacy@aicourse.com',
          '• 电话：400-123-4567',
          '• 地址：北京市海淀区中关村大街1号'
        ])

        // 底部信息
        Column() {
          Text('版本：1.0')
            .fontSize(12)
            .fontColor('#999')
          Text('生效日期：2024年1月15日')
            .fontSize(12)
            .fontColor('#999')
            .margin({ top: 8 })
        }
        .width('100%')
        .alignItems(HorizontalAlign.Center)
        .margin({ top: 40, bottom: 40 })
      }
      .padding(20)
    }
    .flexGrow(1)
  }

  @Builder
  buildSection(title: string, items: string[]) {
    Column() {
      Text(title)
        .fontSize(16)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 16 })
        .fontColor('#333')
      
      ForEach(items, (item: string) => {
        Text(item)
          .fontSize(14)
          .fontColor('#666')
          .alignSelf(ItemAlign.Start)
          .margin({ bottom: 12 })
          .lineHeight(20)
      })
    }
    .width('100%')
    .alignItems(HorizontalAlign.Start)
    .margin({ bottom: 30 })
  }
} 