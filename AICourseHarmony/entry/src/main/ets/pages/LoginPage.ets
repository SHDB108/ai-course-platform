import router from '@ohos.router';

@Component
struct LoginPage {
  @State username: string = ''
  @State password: string = ''
  @State errorMsg: string = ''

  build() {
    Column() {
      Text('用户登录')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      TextInput({ placeholder: '用户名', text: this.username })
        .onChange((value: string) => this.username = value)
        .margin({ bottom: 20 })
      TextInput({ placeholder: '密码', text: this.password })
        .onChange((value: string) => this.password = value)
        .margin({ bottom: 20 })
      Button('登录')
        .onClick(() => {
          // 后续实现登录校验
          if (!this.username || !this.password) {
            this.errorMsg = '请输入用户名和密码';
            return;
          }
          // 登录成功后跳转主页面
          router.pushUrl({ url: 'pages/MainPage' })
        })
        .margin({ bottom: 20 })
      if (this.errorMsg !== '') {
        Text(this.errorMsg).fontColor(Color.Red)
      }
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 