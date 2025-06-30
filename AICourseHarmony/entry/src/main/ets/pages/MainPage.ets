import router from '@ohos.router';

@Component
struct MainPage {
  build() {
    Column() {
      Text('主页面')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 60, bottom: 40 })
      // 预留用户信息展示
      Text('欢迎，用户名（角色）')
        .margin({ bottom: 30 })
      // 预留动态菜单
      List() {
        ListItem() {
          Button('菜单项1')
            .onClick(() => {
              // 跳转到对应页面
            })
        }
        ListItem() {
          Button('菜单项2')
            .onClick(() => {
              // 跳转到对应页面
            })
        }
      }
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
} 