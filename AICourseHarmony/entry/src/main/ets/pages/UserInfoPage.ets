import router from '@ohos.router';
import { getCurrentUser, updateUserInfo, UserInfo } from '../common/UserStore';

interface EditForm {
  realName: string;
  email: string;
  phone: string;
  major?: string;
  grade?: string;
  department?: string;
  title?: string;
}

@Entry
@Component
struct UserInfoPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State editForm: EditForm = {
    realName: '',
    email: '',
    phone: '',
    major: '',
    grade: '',
    department: '',
    title: ''
  };
  @State isEditing: boolean = false;
  @State isLoading: boolean = false;
  @State message: string = '';

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }
    this.initEditForm();
  }

  initEditForm() {
    if (this.currentUser) {
      this.editForm = {
        realName: this.currentUser.realName,
        email: this.currentUser.email,
        phone: this.currentUser.phone,
        major: this.currentUser.major || '',
        grade: this.currentUser.grade || '',
        department: this.currentUser.department || '',
        title: this.currentUser.title || ''
      };
    }
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
        
        Text('个人信息')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
        
        Button(this.isEditing ? '取消' : '编辑')
          .fontSize(16)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            if (this.isEditing) {
              this.cancelEdit();
            } else {
              this.startEdit();
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
          // 头像区域
          this.buildAvatarSection()
          
          // 基本信息
          this.buildBasicInfo()
          
          // 角色特有信息
          this.buildRoleSpecificInfo()
          
          // 保存按钮
          if (this.isEditing) {
            this.buildSaveButton()
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
  buildAvatarSection() {
    Column() {
      Stack() {
        Image(this.currentUser?.avatar || $r('app.media.foreground'))
          .width(80)
          .height(80)
          .borderRadius(40)
          .backgroundColor('#f0f0f0')
        
        if (this.isEditing) {
          Button('📷')
            .width(30)
            .height(30)
            .borderRadius(15)
            .backgroundColor('#007AFF')
            .fontColor(Color.White)
            .position({ x: 50, y: 50 })
            .onClick(() => {
              // TODO: 实现头像上传
            })
        }
      }
      
      Text(this.currentUser?.realName || '')
        .fontSize(20)
        .fontWeight(FontWeight.Bold)
        .margin({ top: 15, bottom: 5 })
      
      Text(this.getRoleText(this.currentUser?.role))
        .fontSize(14)
        .fontColor('#666')
        .padding({ left: 10, right: 10, top: 5, bottom: 5 })
        .backgroundColor('#f0f0f0')
        .borderRadius(10)
    }
    .alignItems(HorizontalAlign.Center)
    .padding({ top: 30, bottom: 30 })
  }

  @Builder
  buildBasicInfo() {
    Column() {
      Text('基本信息')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 15 })
      
      this.buildInfoItem('用户名', this.currentUser?.username || '', false)
      this.buildInfoItem('真实姓名', this.editForm.realName, this.isEditing, 'realName')
      this.buildInfoItem('邮箱', this.editForm.email, this.isEditing, 'email')
      this.buildInfoItem('手机号', this.editForm.phone, this.isEditing, 'phone')
    }
    .margin({ bottom: 30 })
  }

  @Builder
  buildRoleSpecificInfo() {
    if (this.currentUser?.role === 'student') {
      Column() {
        Text('学生信息')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .alignSelf(ItemAlign.Start)
          .margin({ bottom: 15 })
        
        this.buildInfoItem('学号', this.currentUser.studentId ?? '', false)
        this.buildInfoItem('专业', this.editForm.major ?? '', this.isEditing, 'major')
        this.buildInfoItem('年级', this.editForm.grade ?? '', this.isEditing, 'grade')
      }
      .margin({ bottom: 30 })
    } else if (this.currentUser?.role === 'teacher') {
      Column() {
        Text('教师信息')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .alignSelf(ItemAlign.Start)
          .margin({ bottom: 15 })
        
        this.buildInfoItem('工号', this.currentUser.teacherId ?? '', false)
        this.buildInfoItem('院系', this.editForm.department ?? '', this.isEditing, 'department')
        this.buildInfoItem('职称', this.editForm.title ?? '', this.isEditing, 'title')
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
              switch (field) {
                case 'realName':
                  this.editForm.realName = text;
                  break;
                case 'email':
                  this.editForm.email = text;
                  break;
                case 'phone':
                  this.editForm.phone = text;
                  break;
                case 'major':
                  this.editForm.major = text;
                  break;
                case 'grade':
                  this.editForm.grade = text;
                  break;
                case 'department':
                  this.editForm.department = text;
                  break;
                case 'title':
                  this.editForm.title = text;
                  break;
              }
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

  @Builder
  buildSaveButton() {
    Button(this.isLoading ? '保存中...' : '保存')
      .width('100%')
      .height(50)
      .backgroundColor(this.isLoading ? '#ccc' : '#007AFF')
      .borderRadius(8)
      .fontSize(16)
      .fontWeight(FontWeight.Medium)
      .fontColor(Color.White)
      .onClick(() => {
        this.saveUserInfo();
      })
      .enabled(!this.isLoading)
      .margin({ top: 20 })
  }

  startEdit() {
    this.isEditing = true;
  }

  cancelEdit() {
    this.isEditing = false;
    this.initEditForm();
  }

  saveUserInfo() {
    this.isLoading = true;
    
    // 验证必填字段
    if (!this.editForm.realName.trim()) {
      this.message = '请输入真实姓名';
      this.isLoading = false;
      return;
    }
    
    if (!this.editForm.email.trim()) {
      this.message = '请输入邮箱';
      this.isLoading = false;
      return;
    }
    
    if (!this.editForm.phone.trim()) {
      this.message = '请输入手机号';
      this.isLoading = false;
      return;
    }

    // 模拟网络请求
    setTimeout(() => {
      const success = updateUserInfo(this.editForm);
      
      if (success) {
        this.message = '保存成功';
        this.isEditing = false;
        this.currentUser = getCurrentUser();
      } else {
        this.message = '保存失败，请重试';
      }
      
      this.isLoading = false;
    }, 1000);
  }

  getRoleText(role?: string): string {
    switch (role) {
      case 'admin':
        return '系统管理员';
      case 'teacher':
        return '教师';
      case 'student':
        return '学生';
      default:
        return '未知';
    }
  }
} 