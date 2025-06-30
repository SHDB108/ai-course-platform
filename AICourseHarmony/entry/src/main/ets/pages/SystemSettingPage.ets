@Component
struct SystemSettingPage {
  build() {
    Column() {
      Text('系统设置')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      Text('这里是管理员可见的系统设置页面')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 