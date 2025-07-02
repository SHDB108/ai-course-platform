import router from '@ohos.router';
import { getCurrentUser, users } from '../common/UserStore';
import { UserInfo, UserRole } from '../common/UserTypes';

interface EditUserPageParams {
  userId?: string;
}

interface EditForm {
  id?: string;
  username: string;
  realName: string;
  email: string;
  phone: string;
  role: UserRole;
  status: 'active' | 'inactive';
  studentId: string;
  major: string;
  grade: string;
  teacherId: string;
  department: string;
  title: string;
}

// 编辑用户页面，支持用户信息的查看、编辑与保存

/**
 * 编辑用户页面组件
 * 用于展示和编辑指定用户的详细信息
 */
@Entry
@Component
struct EditUserPage {
  @State currentUser: UserInfo | null = null;
  @State userId: string = '';
  @State isEditing: boolean = false;
  @State isLoading: boolean = false;
  @State message: string = '';
  @State editForm: EditForm = {
    username: '',
    realName: '',
    email: '',
    phone: '',
    role: 'student',
    status: 'active',
    studentId: '',
    major: '',
    grade: '',
    teacherId: '',
    department: '',
    title: ''
  };

  aboutToAppear() {
    // 用 getCurrentUser() 初始化 currentUser，避免误判未登录
    this.currentUser = getCurrentUser();
    // 检查用户权限
    if (!this.currentUser || this.currentUser.role !== 'admin') {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }

    // 获取传递的用户ID
    const params = router.getParams() as EditUserPageParams;
    if (params && params.userId) {
      this.userId = params.userId as string;
      this.loadUserData();
    } else {
      // 新建用户模式
      this.isEditing = true;
      this.initNewUserForm();
    }
  }

  loadUserData() {
    const user = users.find(u => u.id === this.userId);
    if (user) {
      this.editForm = {
        id: user.id,
        username: user.username,
        realName: user.realName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        status: user.status,
        studentId: user.studentId || '',
        major: user.major || '',
        grade: user.grade || '',
        teacherId: user.teacherId || '',
        department: user.department || '',
        title: user.title || ''
      };
    } else {
      router.back();
    }
  }

  initNewUserForm() {
    this.editForm = {
      username: '',
      realName: '',
      email: '',
      phone: '',
      role: 'student',
      status: 'active',
      studentId: '',
      major: '',
      grade: '',
      teacherId: '',
      department: '',
      title: ''
    };
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
        
        Text(this.userId ? '编辑用户' : '新建用户')
          .fontSize(18)
        .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
        
        Button(this.isEditing ? '保存' : '编辑')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            if (this.isEditing) {
              this.saveUser();
            } else {
              this.isEditing = true;
            }
          })
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor(Color.White)
      .border({ width: { bottom: 1 }, color: '#e0e0e0' })

      // 内容区域
      Scroll() {
        Column() {
          // 基本信息
          this.buildBasicInfo()
          
          // 角色特有信息
          this.buildRoleSpecificInfo()
          
          // 消息提示
          if (this.message) {
            Text(this.message)
              .fontSize(14)
              .fontColor(this.message.includes('成功') ? '#28a745' : '#FF3B30')
              .textAlign(TextAlign.Center)
              .margin({ top: 10, bottom: 10 })
          }
        }
        .padding({ left: 20, right: 20, bottom: 100 })
      }
      .flexGrow(1)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildBasicInfo() {
    Column() {
      Text('基本信息')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      this.buildInfoItem('用户名', this.editForm.username || '', this.isEditing, 'username')
      this.buildInfoItem('真实姓名', this.editForm.realName || '', this.isEditing, 'realName')
      this.buildInfoItem('邮箱', this.editForm.email || '', this.isEditing, 'email')
      this.buildInfoItem('手机号', this.editForm.phone || '', this.isEditing, 'phone')
      
      // 角色选择
      if (this.isEditing) {
        this.buildRoleSelector()
      } else {
        this.buildInfoItem('角色', this.getRoleText(this.editForm.role), false)
      }
      
      // 状态选择
      if (this.isEditing) {
        this.buildStatusSelector()
      } else {
        this.buildInfoItem('状态', this.editForm.status === 'active' ? '正常' : '禁用', false)
      }
    }
    .margin({ bottom: 30 })
  }

  @Builder
  buildRoleSpecificInfo() {
    if (this.editForm.role === 'student') {
      Column() {
        Text('学生信息')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .alignSelf(ItemAlign.Start)
          .margin({ bottom: 15 })
        
        this.buildInfoItem('学号', this.editForm.studentId || '', this.isEditing, 'studentId')
        this.buildInfoItem('专业', this.editForm.major || '', this.isEditing, 'major')
        this.buildInfoItem('年级', this.editForm.grade || '', this.isEditing, 'grade')
      }
      .margin({ bottom: 30 })
    } else if (this.editForm.role === 'teacher') {
      Column() {
        Text('教师信息')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .alignSelf(ItemAlign.Start)
          .margin({ bottom: 15 })
        
        this.buildInfoItem('工号', this.editForm.teacherId || '', this.isEditing, 'teacherId')
        this.buildInfoItem('院系', this.editForm.department || '', this.isEditing, 'department')
        this.buildInfoItem('职称', this.editForm.title || '', this.isEditing, 'title')
      }
      .margin({ bottom: 30 })
    }
  }

  @Builder
  buildInfoItem(label: string, value: string, editable: boolean, field?: string) {
    Row() {
      Text(label)
        .fontSize(14)
        .fontColor('#666')
        .width(80)
      
      if (editable) {
        TextInput({ text: value })
          .onChange((text: string) => {
            if (field) {
              this.updateFormField(field, text);
            }
          })
          .flexGrow(1)
          .height(40)
          .backgroundColor('#f8f9fa')
          .borderRadius(8)
          .padding({ left: 10, right: 10 })
      } else {
        Text(value)
          .fontSize(14)
          .flexGrow(1)
      }
    }
    .width('100%')
    .padding({ top: 15, bottom: 15 })
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 10 })
    .padding({ left: 15, right: 15 })
  }

  updateFormField(field: string, value: string) {
    switch (field) {
      case 'username':
        this.editForm.username = value;
        break;
      case 'realName':
        this.editForm.realName = value;
        break;
      case 'email':
        this.editForm.email = value;
        break;
      case 'phone':
        this.editForm.phone = value;
        break;
      case 'studentId':
        this.editForm.studentId = value;
        break;
      case 'major':
        this.editForm.major = value;
        break;
      case 'grade':
        this.editForm.grade = value;
        break;
      case 'teacherId':
        this.editForm.teacherId = value;
        break;
      case 'department':
        this.editForm.department = value;
        break;
      case 'title':
        this.editForm.title = value;
        break;
    }
  }

  @Builder
  buildRoleSelector() {
    Row() {
      Text('角色')
        .fontSize(14)
        .fontColor('#666')
        .width(80)
      
      Row() {
        ForEach(['student', 'teacher', 'admin'], (role: UserRole) => {
          Button(this.getRoleText(role))
            .fontSize(14)
            .height(32)
            .backgroundColor(this.editForm.role === role ? '#007AFF' : '#f0f0f0')
            .fontColor(this.editForm.role === role ? Color.White : '#333')
            .borderRadius(16)
            .margin({ right: 10 })
            .onClick(() => {
              this.editForm.role = role;
              // 清空角色特有字段
              this.editForm.studentId = '';
              this.editForm.major = '';
              this.editForm.grade = '';
              this.editForm.teacherId = '';
              this.editForm.department = '';
              this.editForm.title = '';
            })
        })
      }
      .flexGrow(1)
    }
    .width('100%')
    .padding({ top: 15, bottom: 15 })
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 10 })
    .padding({ left: 15, right: 15 })
  }

  @Builder
  buildStatusSelector() {
    Row() {
      Text('状态')
        .fontSize(14)
        .fontColor('#666')
        .width(80)
      
      Row() {
        ForEach(['active', 'inactive'], (status: string) => {
          Button(status === 'active' ? '正常' : '禁用')
            .fontSize(14)
            .height(32)
            .backgroundColor(this.editForm.status === status ? '#007AFF' : '#f0f0f0')
            .fontColor(this.editForm.status === status ? Color.White : '#333')
            .borderRadius(16)
            .margin({ right: 10 })
            .onClick(() => {
              this.editForm.status = status as 'active' | 'inactive';
            })
        })
      }
      .flexGrow(1)
    }
    .width('100%')
    .padding({ top: 15, bottom: 15 })
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 10 })
    .padding({ left: 15, right: 15 })
  }

  saveUser() {
    this.isLoading = true;
    this.message = '';
    
    // 验证必填字段
    if (!this.editForm.username?.trim()) {
      this.message = '请输入用户名';
      this.isLoading = false;
      return;
    }
    
    if (!this.editForm.realName?.trim()) {
      this.message = '请输入真实姓名';
      this.isLoading = false;
      return;
    }
    
    if (!this.editForm.email?.trim()) {
      this.message = '请输入邮箱';
      this.isLoading = false;
      return;
    }
    
    if (!this.editForm.phone?.trim()) {
      this.message = '请输入手机号';
      this.isLoading = false;
      return;
    }

    // 验证用户名唯一性
    const existingUser = users.find(u => u.username === this.editForm.username && u.id !== this.userId);
    if (existingUser) {
      this.message = '用户名已存在';
      this.isLoading = false;
      return;
    }

    // 模拟网络请求
    setTimeout(() => {
      if (this.userId) {
        // 编辑现有用户
        const success = this.updateExistingUser();
        if (success) {
          this.message = '保存成功';
          setTimeout(() => {
            router.back();
          }, 1000);
        } else {
          this.message = '保存失败，请重试';
        }
      } else {
        // 创建新用户
        const success = this.createNewUser();
        if (success) {
          this.message = '创建成功';
          setTimeout(() => {
            router.back();
          }, 1000);
        } else {
          this.message = '创建失败，请重试';
        }
      }
      
      this.isLoading = false;
    }, 1000);
  }

  updateExistingUser(): boolean {
    const userIndex = users.findIndex(u => u.id === this.userId);
    if (userIndex === -1) return false;
    
    const originalUser = users[userIndex];
    const updatedUser: UserInfo = {
      id: originalUser.id,
      username: this.editForm.username!,
      password: originalUser.password,
      realName: this.editForm.realName!,
      email: this.editForm.email!,
      phone: this.editForm.phone!,
      role: this.editForm.role!,
      status: this.editForm.status!,
      createTime: originalUser.createTime,
      lastLoginTime: originalUser.lastLoginTime,
      avatar: originalUser.avatar,
      studentId: this.editForm.studentId,
      major: this.editForm.major,
      grade: this.editForm.grade,
      teacherId: this.editForm.teacherId,
      department: this.editForm.department,
      title: this.editForm.title
    };
    
    users[userIndex] = updatedUser;
    return true;
  }

  createNewUser(): boolean {
    const newUser: UserInfo = {
      id: Date.now().toString(),
      username: this.editForm.username!,
      password: '123456', // 默认密码
      realName: this.editForm.realName!,
      email: this.editForm.email!,
      phone: this.editForm.phone!,
      role: this.editForm.role!,
      status: this.editForm.status!,
      createTime: new Date().toISOString(),
      studentId: this.editForm.studentId,
      major: this.editForm.major,
      grade: this.editForm.grade,
      teacherId: this.editForm.teacherId,
      department: this.editForm.department,
      title: this.editForm.title
    };
    
    users.push(newUser);
    return true;
  }

  getRoleText(role?: UserRole): string {
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
} 