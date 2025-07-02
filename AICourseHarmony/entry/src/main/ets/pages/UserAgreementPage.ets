import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';

// 用户协议页面，展示平台用户协议内容

/**
 * 用户协议页面组件
 * 展示平台用户协议的详细内容
 */
@Entry
@Component
struct UserAgreementPage {
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
      Button('\u2190')
        .fontSize(20)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          router.back();
        })
      Text('\u7528\u6237\u534f\u8bae')
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
        Text('AI课程平台用户协议')
          .fontSize(24)
          .fontWeight(FontWeight.Bold)
          .textAlign(TextAlign.Center)
          .width('100%')
          .margin({ top: 30, bottom: 30 })

        // 协议内容
        this.buildSection('第一条 总则', [
          '1.1 本协议是您与AI课程平台（以下简称"平台"）之间关于使用平台服务所订立的协议。',
          '1.2 您在使用平台服务前，应当仔细阅读本协议，确保对本协议各条款的含义及相应的法律后果已全部知晓并充分理解。',
          '1.3 您点击"同意"或实际使用平台服务，即表示您已充分阅读、理解并接受本协议的全部内容。'
        ])

        this.buildSection('第二条 服务内容', [
          '2.1 平台为用户提供在线课程学习、测验评估、学习进度跟踪等服务。',
          '2.2 平台服务包括但不限于：课程浏览、课程选修、在线学习、测验参与、成绩查询等。',
          '2.3 平台保留随时修改或中断服务而不需通知用户的权利。'
        ])

        this.buildSection('第三条 用户权利和义务', [
          '3.1 用户有权在平台注册账户，享受平台提供的各项服务。',
          '3.2 用户应当遵守国家法律法规，不得利用平台从事违法违规活动。',
          '3.3 用户应当保护好自己的账户信息，不得将账户借给他人使用。',
          '3.4 用户应当尊重知识产权，不得未经授权传播平台内容。'
        ])

        this.buildSection('第四条 平台权利和义务', [
          '4.1 平台有权对用户的使用行为进行监督和管理。',
          '4.2 平台应当保护用户隐私，不得泄露用户个人信息。',
          '4.3 平台应当保证服务的稳定性和安全性。',
          '4.4 平台有权对违规用户采取限制或终止服务的措施。'
        ])

        this.buildSection('第五条 知识产权', [
          '5.1 平台及其内容的知识产权归平台所有。',
          '5.2 用户不得复制、传播、修改平台内容。',
          '5.3 用户在学习过程中产生的原创内容，知识产权归用户所有。'
        ])

        this.buildSection('第六条 隐私保护', [
          '6.1 平台收集的用户信息仅用于提供服务。',
          '6.2 平台不会向第三方出售、出租或分享用户个人信息。',
          '6.3 用户有权查询、更正自己的个人信息。'
        ])

        this.buildSection('第七条 免责声明', [
          '7.1 平台不对因网络故障、系统维护等原因造成的服务中断承担责任。',
          '7.2 平台不对用户因使用服务而产生的任何直接或间接损失承担责任。',
          '7.3 用户应当自行承担使用平台服务的风险。'
        ])

        this.buildSection('第八条 协议修改', [
          '8.1 平台有权根据需要修改本协议。',
          '8.2 协议修改后，平台将在显著位置公布修改内容。',
          '8.3 用户继续使用服务即表示接受修改后的协议。'
        ])

        this.buildSection('第九条 争议解决', [
          '9.1 本协议的订立、执行和解释及争议的解决均应适用中华人民共和国法律。',
          '9.2 如双方就本协议内容或其执行发生任何争议，双方应尽量友好协商解决。',
          '9.3 协商不成时，任何一方均可向平台所在地的人民法院提起诉讼。'
        ])

        this.buildSection('第十条 其他', [
          '10.1 本协议自用户同意之日起生效。',
          '10.2 本协议的最终解释权归平台所有。',
          '10.3 本协议未尽事宜，按照国家相关法律法规执行。'
        ])

        // 底部信息
        Column() {
          Text('版本：1.0')
            .fontSize(12)
            .fontColor('#999')
          Text('更新日期：2024年1月15日')
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