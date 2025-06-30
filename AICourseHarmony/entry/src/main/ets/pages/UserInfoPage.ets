@Component
struct UserInfoPage {
  build() {
    Column() {
      Text('个人信息')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      Text('这里是普通用户可见的个人信息页面')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 