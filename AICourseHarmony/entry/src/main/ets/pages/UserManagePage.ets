import router from '@ohos.router';
import { getCurrentUser, users, UserInfo, UserRole } from '../common/UserStore';

interface UserIdParams {
  userId: string;
}

@Entry
@Component
struct UserManagePage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State userList: UserInfo[] = [];
  @State searchText: string = '';
  @State selectedRole: string = 'all';
  @State isLoading: boolean = false;

  aboutToAppear() {
    if (!this.currentUser || this.currentUser.role !== 'admin') {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    
    // 模拟网络延迟
    setTimeout(() => {
      this.userList = users;
      this.isLoading = false;
    }, 500);
  }

  build() {
    Column() {
      // 顶部导航栏
      Row() {
        Button('← 返回')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            router.back();
          })
        
        Text('用户管理')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
        
        Button('+ 添加')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            router.pushUrl({ url: 'pages/EditUserPage' });
          })
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor(Color.White)
      .border({ width: { bottom: 1 }, color: '#e0e0e0' })

      // 搜索和筛选
      this.buildSearchAndFilter()
      
      // 用户列表
      this.buildUserList()
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildSearchAndFilter() {
    Column() {
      // 搜索框
      Row() {
        TextInput({ 
          placeholder: '搜索用户名或真实姓名', 
          text: this.searchText 
        })
          .onChange((value: string) => {
            this.searchText = value;
            this.filterUsers();
          })
          .width('85%')
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(20)
          .padding({ left: 15, right: 15 })
          .placeholderColor('#999')
        Button('🔍')
          .width('15%')
          .height(40)
          .backgroundColor('#007AFF')
          .borderRadius(20)
          .fontColor(Color.White)
          .onClick(() => {
            this.filterUsers();
          })
      }
      .width('100%')
      .alignItems(VerticalAlign.Center)
      .margin({ bottom: 15 })

      // 角色筛选
      Scroll() {
        Row() {
          this.buildFilterChip('全部', 'all')
          this.buildFilterChip('学生', 'student')
          this.buildFilterChip('教师', 'teacher')
          this.buildFilterChip('管理员', 'admin')
        }
        .padding({ left: 20, right: 20 })
      }
      .scrollable(ScrollDirection.Horizontal)
      .margin({ bottom: 15 })
    }
    .padding({ left: 20, right: 20, top: 15 })
    .backgroundColor(Color.White)
  }

  @Builder
  buildFilterChip(label: string, value: string) {
    Button(label)
      .fontSize(14)
      .height(32)
      .backgroundColor(this.selectedRole === value ? '#007AFF' : '#f0f0f0')
      .fontColor(this.selectedRole === value ? Color.White : '#333')
      .borderRadius(16)
      .margin({ right: 10 })
      .onClick(() => {
        this.selectedRole = value;
        this.filterUsers();
      })
  }

  @Builder
  buildUserList() {
    if (this.isLoading) {
      Column() {
        LoadingProgress()
          .width(50)
          .height(50)
          .margin({ top: 100 })
        
        Text('加载中...')
          .fontSize(14)
          .fontColor('#666')
          .margin({ top: 10 })
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)
    } else if (this.userList.length === 0) {
      Column() {
        Text('👥')
          .fontSize(60)
          .margin({ top: 100, bottom: 20 })
        
        Text('暂无用户')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 10 })
        
        Text('系统中还没有任何用户')
          .fontSize(14)
          .fontColor('#666')
      }
      .width('100%')
      .alignItems(HorizontalAlign.Center)
    } else {
      List() {
        ForEach(this.userList, (user: UserInfo) => {
          ListItem() {
            this.buildUserItem(user)
          }
        })
      }
      .width('100%')
      .height('100%')
      .padding({ left: 20, right: 20 })
    }
  }

  @Builder
  buildUserItem(user: UserInfo) {
    Column() {
      Row() {
        // 用户头像
        Image(user.avatar || $r('app.media.foreground'))
          .width(50)
          .height(50)
          .borderRadius(25)
          .backgroundColor('#f0f0f0')
        // 用户信息
        Column() {
          Text(user.realName)
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 5 })
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
          Text(`@${user.username}`)
            .fontSize(14)
            .fontColor('#666')
            .margin({ bottom: 5 })
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
          Row() {
            Text(this.getRoleText(user.role))
              .fontSize(12)
              .padding({ left: 8, right: 8, top: 4, bottom: 4 })
              .backgroundColor(this.getRoleColor(user.role))
              .fontColor(Color.White)
              .borderRadius(10)
            Text('•')
              .fontSize(12)
              .fontColor('#999')
              .margin({ left: 8, right: 8 })
            Text(user.status === 'active' ? '正常' : '禁用')
              .fontSize(12)
              .fontColor(user.status === 'active' ? '#28a745' : '#FF3B30')
          }
        }
        .alignItems(HorizontalAlign.Start)
        .flexGrow(1)
        .margin({ left: 15 })
      }
      .width('100%')
      // 底部操作按钮组横向排列
      Row() {
        Button('编辑')
          .fontSize(11)
          .height(28)
          .width(70)
          .backgroundColor('#007AFF')
          .borderRadius(14)
          .fontColor(Color.White)
          .margin({ right: 10 })
          .onClick(() => {
            this.editUser(user);
          })
        Button(user.status === 'active' ? '禁用' : '启用')
          .fontSize(11)
          .height(28)
          .width(70)
          .backgroundColor(user.status === 'active' ? '#FF3B30' : '#28a745')
          .borderRadius(14)
          .fontColor(Color.White)
          .onClick(() => {
            this.toggleUserStatus(user);
          })
      }
      .margin({ top: 8 })
    }
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 15 })
    .onClick(() => {
      this.viewUserDetail(user);
    })
  }

  filterUsers() {
    this.isLoading = true;
    
    setTimeout(() => {
      let filteredUsers = users;
      
      // 按角色筛选
      if (this.selectedRole !== 'all') {
        filteredUsers = filteredUsers.filter(user => user.role === this.selectedRole);
      }
      
      // 按搜索文本筛选
      if (this.searchText.trim()) {
        const searchLower = this.searchText.toLowerCase();
        filteredUsers = filteredUsers.filter(user => 
          user.realName.toLowerCase().includes(searchLower) ||
          user.username.toLowerCase().includes(searchLower) ||
          user.email.toLowerCase().includes(searchLower)
        );
      }
      
      this.userList = filteredUsers;
      this.isLoading = false;
    }, 300);
  }

  viewUserDetail(user: UserInfo) {
    // TODO: 跳转到用户详情页面
  }

  editUser(user: UserInfo) {
    const userIdParams: UserIdParams = { userId: user.id };
    router.pushUrl({ 
      url: 'pages/EditUserPage',
      params: userIdParams
    });
  }

  toggleUserStatus(user: UserInfo) {
    const userIndex = users.findIndex(u => u.id === user.id);
    if (userIndex === -1) return;
    
    // 切换用户状态
    users[userIndex].status = user.status === 'active' ? 'inactive' : 'active';
    
    // 更新当前用户列表
    this.loadUsers();
    
    // 显示提示信息
    const newStatus = users[userIndex].status === 'active' ? '启用' : '禁用';
    // 这里可以添加Toast提示
  }

  getRoleText(role: UserRole): string {
    switch (role) {
      case 'admin':
        return '管理员';
      case 'teacher':
        return '教师';
      case 'student':
        return '学生';
      default:
        return '未知';
    }
  }

  getRoleColor(role: UserRole): string {
    switch (role) {
      case 'admin':
        return '#FF3B30';
      case 'teacher':
        return '#007AFF';
      case 'student':
        return '#28a745';
      default:
        return '#999';
    }
  }
} 