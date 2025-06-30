@Component
struct UserManagePage {
  build() {
    Column() {
      Text('用户管理')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      Text('这里是管理员可见的用户管理页面')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 