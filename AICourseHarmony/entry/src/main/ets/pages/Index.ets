import router from '@ohos.router';

@Entry
@Component
struct Index {
  @State message: string = 'Hello World';

  build() {
    Column() {
      Text('智慧课程系统')
        .fontSize(26)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      Button('进入系统')
        .onClick(() => {
          // 跳转到登录页面，后续实现
          router.pushUrl({ url: 'pages/LoginPage' })
        })
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
}