import router from '@ohos.router';
import { login, UserInfo } from '../common/UserStore';

@Entry
@Component
struct LoginPage {
  @State username: string = ''
  @State password: string = ''
  @State errorMsg: string = ''
  @State isLoading: boolean = false
  @State showPassword: boolean = false

  build() {
    Column() {
      // 顶部返回按钮
      Row() {
        Button('← 返回')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            router.back();
          })
      }
      .width('100%')
      .padding({ left: 20, top: 20, bottom: 20 })
      .justifyContent(FlexAlign.Start)

      // 登录表单
      Column() {
        // Logo和标题
        Column() {
          Image($r('app.media.foreground'))
            .width(80)
            .height(80)
            .margin({ bottom: 20 })
          
          Text('AI课程平台')
            .fontSize(28)
            .fontWeight(FontWeight.Bold)
            .margin({ bottom: 8 })
          
          Text('智能化生产 · 结构化管理 · 个性化学习')
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 40 })
        }
        .alignItems(HorizontalAlign.Center)

        // 登录表单
        Column() {
          // 用户名输入框
          Column() {
            Text('用户名')
              .fontSize(14)
              .fontWeight(FontWeight.Medium)
              .alignSelf(ItemAlign.Start)
              .margin({ bottom: 8 })
            
            TextInput({ 
              placeholder: '请输入用户名', 
              text: this.username 
            })
              .onChange((value: string) => this.username = value)
              .height(50)
              .backgroundColor('#f8f9fa')
              .borderRadius(8)
              .padding({ left: 15, right: 15 })
              .placeholderColor('#999')
          }
          .margin({ bottom: 20 })

          // 密码输入框
          Column() {
            Text('密码')
              .fontSize(14)
              .fontWeight(FontWeight.Medium)
              .alignSelf(ItemAlign.Start)
              .margin({ bottom: 8 })
            
            Row() {
              TextInput({ 
                placeholder: '请输入密码', 
                text: this.password 
              })
                .onChange((value: string) => this.password = value)
                .type(this.showPassword ? InputType.Normal : InputType.Password)
                .height(50)
                .backgroundColor('#f8f9fa')
                .borderRadius(8)
                .padding({ left: 15, right: 15 })
                .placeholderColor('#999')
                .flexGrow(1)
              
              Button(this.showPassword ? '👁️' : '🙈')
                .width(50)
                .height(50)
                .backgroundColor('#f8f9fa')
                .borderRadius(8)
                .margin({ left: 10 })
                .onClick(() => {
                  this.showPassword = !this.showPassword;
                })
            }
          }
          .margin({ bottom: 20 })

          // 错误信息显示
          if (this.errorMsg !== '') {
            Text(this.errorMsg)
              .fontSize(14)
              .fontColor(Color.Red)
              .margin({ bottom: 20 })
          }

          // 登录按钮
          Button(this.isLoading ? '登录中...' : '登录')
            .width('100%')
            .height(50)
            .backgroundColor(this.isLoading ? '#ccc' : '#007AFF')
            .borderRadius(8)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .fontColor(Color.White)
            .onClick(() => {
              this.handleLogin();
            })
            .enabled(!this.isLoading)

          // 快速登录提示
          Column() {
            Text('测试账号')
              .fontSize(14)
              .fontWeight(FontWeight.Medium)
              .margin({ top: 30, bottom: 15 })
            
            this.buildTestAccount('管理员', 'admin', 'admin123')
            this.buildTestAccount('教师', 'teacher1', 'teacher123')
            this.buildTestAccount('学生', 'student1', 'student123')
          }
          .alignItems(HorizontalAlign.Start)
        }
        .width('100%')
        .padding({ left: 30, right: 30 })
      }
      .flexGrow(1)
      .justifyContent(FlexAlign.Center)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }

  @Builder
  buildTestAccount(role: string, username: string, password: string) {
    Row() {
      Text(`${role}: ${username}`)
        .fontSize(12)
        .fontColor('#666')
        .flexGrow(1)
      
      Button('使用')
        .fontSize(12)
        .height(30)
        .backgroundColor('#f0f0f0')
        .borderRadius(15)
        .onClick(() => {
          this.username = username;
          this.password = password;
        })
    }
    .width('100%')
    .justifyContent(FlexAlign.SpaceBetween)
    .margin({ bottom: 8 })
  }

  handleLogin() {
    // 清除之前的错误信息
    this.errorMsg = '';
    
    // 验证输入
    if (!this.username.trim()) {
      this.errorMsg = '请输入用户名';
      return;
    }
    
    if (!this.password.trim()) {
      this.errorMsg = '请输入密码';
      return;
    }

    // 显示加载状态
    this.isLoading = true;

    // 模拟网络延迟
    setTimeout(() => {
      try {
        const user = login(this.username.trim(), this.password.trim());
        
        if (user) {
          // 登录成功，跳转到主页面
          router.replaceUrl({ url: 'pages/MainPage' });
        } else {
          // 登录失败
          this.errorMsg = '用户名或密码错误';
        }
      } catch (error) {
        this.errorMsg = '登录失败，请稍后重试';
      } finally {
        this.isLoading = false;
      }
    }, 1000);
  }
} 