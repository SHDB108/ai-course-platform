import router from '@ohos.router';

// 应用入口页面，负责路由分发和初始跳转

/**
 * 应用入口页面组件
 * 负责应用启动时的路由分发和初始页面跳转
 */
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