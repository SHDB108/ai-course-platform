@Component
struct EditUserPage {
  build() {
    Column() {
      Text('编辑信息')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      Text('这里是编辑用户信息的页面')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 