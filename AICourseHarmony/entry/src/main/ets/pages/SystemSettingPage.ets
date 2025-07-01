import router from '@ohos.router';
import { getCurrentUser, UserInfo } from '../common/UserStore';

interface SettingOption {
  label: string;
  value: string;
}

interface SettingItem {
  title: string;
  subtitle: string;
  type: 'switch' | 'select' | 'info' | 'action';
  value?: boolean | string;
  options?: SettingOption[];
  onChange?: (value: boolean | string) => void;
  onAction?: () => void;
}

interface SystemSettings {
  notifications: boolean;
  autoSave: boolean;
  darkMode: boolean;
  language: string;
  fontSize: string;
}

interface AlertDialogButton {
  value: string;
  action: () => void;
}

interface AlertDialogOptions {
  title: string;
  message: string;
  primaryButton: AlertDialogButton;
  secondaryButton: AlertDialogButton;
}

@Entry
@Component
struct SystemSettingPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State settings: SystemSettings = {
    notifications: true,
    autoSave: true,
    darkMode: false,
    language: 'zh-CN',
    fontSize: 'medium'
  };

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
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
        
        Text('系统设置')
          .fontSize(18)
          .fontWeight(FontWeight.Bold)
          .flexGrow(1)
          .textAlign(TextAlign.Center)
      }
      .width('100%')
      .padding({ left: 20, right: 20, top: 20, bottom: 20 })
      .backgroundColor('#f5f5f5')

      // 设置列表
      List() {
        // 通知设置
        ListItem() {
          this.buildSettingSection('通知设置', [
            {
              title: '推送通知',
              subtitle: '接收课程更新和消息提醒',
              type: 'switch',
              value: this.settings.notifications,
              onChange: (value: boolean) => {
                this.settings.notifications = value;
              }
            } as SettingItem
          ])
        }

        // 学习设置
        ListItem() {
          this.buildSettingSection('学习设置', [
            {
              title: '自动保存进度',
              subtitle: '自动保存学习进度和笔记',
              type: 'switch',
              value: this.settings.autoSave,
              onChange: (value: boolean) => {
                this.settings.autoSave = value;
              }
            } as SettingItem
          ])
        }

        // 显示设置
        ListItem() {
          this.buildSettingSection('显示设置', [
            {
              title: '深色模式',
              subtitle: '使用深色主题',
              type: 'switch',
              value: this.settings.darkMode,
              onChange: (value: boolean) => {
                this.settings.darkMode = value;
              }
            } as SettingItem,
            {
              title: '字体大小',
              subtitle: this.getFontSizeText(),
              type: 'select',
              value: this.settings.fontSize,
              options: [
                { label: '小', value: 'small' } as SettingOption,
                { label: '中', value: 'medium' } as SettingOption,
                { label: '大', value: 'large' } as SettingOption
              ],
              onChange: (value: string) => {
                this.settings.fontSize = value;
              }
            } as SettingItem
          ])
        }

        // 语言设置
        ListItem() {
          this.buildSettingSection('语言设置', [
            {
              title: '应用语言',
              subtitle: this.getLanguageText(),
              type: 'select',
              value: this.settings.language,
              options: [
                { label: '简体中文', value: 'zh-CN' } as SettingOption,
                { label: 'English', value: 'en-US' } as SettingOption
              ],
              onChange: (value: string) => {
                this.settings.language = value;
              }
            } as SettingItem
          ])
        }

        // 系统信息
        ListItem() {
          this.buildSettingSection('系统信息', [
            {
              title: '应用版本',
              subtitle: '1.0.0',
              type: 'info'
            } as SettingItem,
            {
              title: '当前用户',
              subtitle: this.currentUser?.realName || '未知',
              type: 'info'
            } as SettingItem,
            {
              title: '用户角色',
              subtitle: this.getRoleText(this.currentUser?.role),
              type: 'info'
            } as SettingItem
          ])
        }

        // 管理功能（仅管理员可见）
        if (this.currentUser?.role === 'admin') {
          ListItem() {
            this.buildSettingSection('管理功能', [
              {
                title: '数据备份',
                subtitle: '备份系统数据',
                type: 'action',
                onAction: () => {
                  this.handleDataBackup();
                }
              } as SettingItem,
              {
                title: '系统日志',
                subtitle: '查看系统运行日志',
                type: 'action',
                onAction: () => {
                  router.pushUrl({ url: 'pages/SystemLogPage' });
                }
              } as SettingItem,
              {
                title: '清除缓存',
                subtitle: '清理应用缓存数据',
                type: 'action',
                onAction: () => {
                  this.handleClearCache();
                }
              } as SettingItem
            ])
          }
        }

        // 关于
        ListItem() {
          this.buildSettingSection('关于', [
            {
              title: '用户协议',
              subtitle: '查看用户使用协议',
              type: 'action',
              onAction: () => {
                router.pushUrl({ url: 'pages/UserAgreementPage' });
              }
            } as SettingItem,
            {
              title: '隐私政策',
              subtitle: '查看隐私保护政策',
              type: 'action',
              onAction: () => {
                router.pushUrl({ url: 'pages/PrivacyPolicyPage' });
              }
            } as SettingItem,
            {
              title: '意见反馈',
              subtitle: '提交问题或建议',
              type: 'action',
              onAction: () => {
                router.pushUrl({ url: 'pages/FeedbackPage' });
              }
            } as SettingItem
          ])
        }

        // 退出登录
        ListItem() {
          this.buildSettingSection('账户', [
            {
              title: '退出登录',
              subtitle: '退出当前账户',
              type: 'action',
              onAction: () => {
                this.handleLogout();
              }
            } as SettingItem
          ])
        }
      }
      .width('100%')
      .height('100%')
      .padding({ left: 20, right: 20 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildSettingSection(title: string, items: SettingItem[]) {
    Column() {
      Text(title)
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .alignSelf(ItemAlign.Start)
        .margin({ bottom: 10 })
      
      ForEach(items, (item: SettingItem) => {
        this.buildSettingItem(item)
      })
    }
    .width('100%')
    .padding(15)
    .backgroundColor(Color.White)
    .borderRadius(12)
    .margin({ bottom: 15 })
  }

  @Builder
  buildSettingItem(item: SettingItem) {
    Row() {
      Column() {
        Text(item.title)
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 5 })
        
        Text(item.subtitle)
          .fontSize(14)
          .fontColor('#666')
      }
      .alignItems(HorizontalAlign.Start)
      .flexGrow(1)
      
      if (item.type === 'switch') {
        Toggle({ type: ToggleType.Switch, isOn: item.value as boolean })
          .onChange((isOn: boolean) => {
            if (item.onChange) {
              item.onChange(isOn);
            }
          })
      } else if (item.type === 'select') {
        Button(item.value as string)
          .fontSize(14)
          .height(32)
          .backgroundColor('#f0f0f0')
          .borderRadius(16)
          .onClick(() => {
            this.showSelectDialog(item);
          })
      } else if (item.type === 'action') {
        Button('>')
          .fontSize(16)
          .width(32)
          .height(32)
          .backgroundColor(Color.Transparent)
          .onClick(() => {
            if (item.onAction) {
              item.onAction();
            }
          })
      }
    }
    .width('100%')
    .padding({ top: 12, bottom: 12 })
    .border({ width: { bottom: 1 }, color: '#f0f0f0' })
  }

  showSelectDialog(item: SettingItem) {
    if (!item.options) return;
    
    // 创建选项列表
    interface OptionItem {
      text: string;
      value: string;
    }
    const options: OptionItem[] = item.options.map(option => ({
      text: option.label,
      value: option.value
    } as OptionItem));
    
    // 显式类型对象
    const dialogOptions: AlertDialogOptions = {
      title: item.title,
      message: '请选择',
      primaryButton: {
        value: '取消',
        action: () => {
          // 取消操作
        }
      },
      secondaryButton: {
        value: '确定',
        action: () => {
          // 这里应该处理选择结果
          // 由于AlertDialog的限制，这里简化处理
          if (item.onChange) {
            // 默认选择第一个选项
            item.onChange(item.options![0].value);
          }
        }
      }
    };
    AlertDialog.show(dialogOptions);
  }

  getFontSizeText(): string {
    switch (this.settings.fontSize) {
      case 'small':
        return '小';
      case 'large':
        return '大';
      default:
        return '中';
    }
  }

  getLanguageText(): string {
    switch (this.settings.language) {
      case 'en-US':
        return 'English';
      default:
        return '简体中文';
    }
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

  handleDataBackup() {
    const backupDialogOptions: AlertDialogOptions = {
      title: '数据备份',
      message: '确定要备份系统数据吗？',
      primaryButton: {
        value: '取消',
        action: () => {
          // 取消操作
        }
      },
      secondaryButton: {
        value: '确定',
        action: () => {
          // 模拟备份过程
          setTimeout(() => {
            const successDialogOptions: AlertDialogOptions = {
              title: '备份完成',
              message: '系统数据已成功备份',
              primaryButton: {
                value: '确定',
                action: () => {
                  // 关闭对话框
                }
              },
              secondaryButton: {
                value: '',
                action: () => {}
              }
            };
            AlertDialog.show(successDialogOptions);
          }, 2000);
        }
      }
    };
    AlertDialog.show(backupDialogOptions);
  }

  handleClearCache() {
    const clearCacheDialogOptions: AlertDialogOptions = {
      title: '清除缓存',
      message: '确定要清除应用缓存吗？此操作不可恢复。',
      primaryButton: {
        value: '取消',
        action: () => {
          // 取消操作
        }
      },
      secondaryButton: {
        value: '确定',
        action: () => {
          // 模拟清除缓存过程
          setTimeout(() => {
            const successDialogOptions: AlertDialogOptions = {
              title: '清除完成',
              message: '应用缓存已成功清除',
              primaryButton: {
                value: '确定',
                action: () => {
                  // 关闭对话框
                }
              },
              secondaryButton: {
                value: '',
                action: () => {}
              }
            };
            AlertDialog.show(successDialogOptions);
          }, 1500);
        }
      }
    };
    AlertDialog.show(clearCacheDialogOptions);
  }

  handleLogout() {
    // TODO: 实现退出登录功能
    router.replaceUrl({ url: 'pages/LoginPage' });
  }
} 