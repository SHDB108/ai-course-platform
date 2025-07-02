import router from '@ohos.router';
import { getCurrentUser } from '../common/UserStore';
import { UserInfo } from '../common/UserTypes';

interface SystemLog {
  id: string;
  timestamp: string;
  level: 'info' | 'warning' | 'error';
  module: string;
  action: string;
  description: string;
  userId?: string;
  userName?: string;
}

// 系统日志页面，展示平台操作日志与系统事件

/**
 * 系统日志页面组件
 * 展示平台操作日志、系统事件等信息
 */
@Entry
@Component
struct SystemLogPage {
  @State currentUser: UserInfo | null = getCurrentUser();
  @State logs: SystemLog[] = [];
  @State isLoading: boolean = true;
  @State selectedLevel: string = 'all';
  @State selectedModule: string = 'all';
  @State showBack: boolean = false;

  aboutToAppear() {
    if (!this.currentUser) {
      router.replaceUrl({ url: 'pages/LoginPage' });
      return;
    }

    this.loadLogs();
  }

  loadLogs() {
    this.isLoading = true;
    // 模拟网络延迟
    setTimeout(() => {
      this.logs = this.getMockLogs();
      this.isLoading = false;
    }, 500);
  }

  getMockLogs(): SystemLog[] {
    return [
      {
        id: '1',
        timestamp: '2024-01-15 10:30:25',
        level: 'info',
        module: '用户管理',
        action: '用户登录',
        description: '用户张三成功登录系统',
        userId: 'user001',
        userName: '张三'
      },
      {
        id: '2',
        timestamp: '2024-01-15 10:25:18',
        level: 'warning',
        module: '课程管理',
        action: '课程创建',
        description: '课程"人工智能基础"创建成功，但缺少课程描述',
        userId: 'teacher001',
        userName: '李老师'
      },
      {
        id: '3',
        timestamp: '2024-01-15 10:20:45',
        level: 'error',
        module: '测验管理',
        action: '测验提交',
        description: '测验提交失败：网络连接超时',
        userId: 'student001',
        userName: '王同学'
      },
      {
        id: '4',
        timestamp: '2024-01-15 10:15:32',
        level: 'info',
        module: '系统管理',
        action: '系统备份',
        description: '系统数据备份完成',
        userId: 'admin001',
        userName: '管理员'
      },
      {
        id: '5',
        timestamp: '2024-01-15 10:10:15',
        level: 'info',
        module: '学习管理',
        action: '课程选修',
        description: '学生王同学选修了课程"机器学习入门"',
        userId: 'student001',
        userName: '王同学'
      }
    ];
  }

  getFilteredLogs(): SystemLog[] {
    return this.logs.filter(log => {
      const levelMatch = this.selectedLevel === 'all' || log.level === this.selectedLevel;
      const moduleMatch = this.selectedModule === 'all' || log.module === this.selectedModule;
      return levelMatch && moduleMatch;
    });
  }

  getLevelColor(level: string): string {
    switch (level) {
      case 'error': return '#ff4757';
      case 'warning': return '#ffa502';
      case 'info': return '#2ed573';
      default: return '#747d8c';
    }
  }

  getLevelText(level: string): string {
    switch (level) {
      case 'error': return '错误';
      case 'warning': return '警告';
      case 'info': return '信息';
      default: return '未知';
    }
  }

  build() {
    Column() {
      this.buildHeader()
      this.buildFilters()
      this.buildLogList()
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#f5f5f5')
  }

  @Builder
  buildHeader() {
    Row() {
      if (this.showBack) {
        Button('\u2190')
          .fontSize(20)
          .backgroundColor(Color.Transparent)
          .fontColor('#007AFF')
          .onClick(() => {
            router.back();
          })
      } else {
        Button('')
          .fontSize(20)
          .backgroundColor(Color.Transparent)
          .fontColor(Color.Transparent)
      }
      Text('\u7cfb\u7edf\u65e5\u5fd7')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)
        .flexGrow(1)
        .textAlign(TextAlign.Center)
      Button('\u5237\u65b0')
        .fontSize(16)
        .backgroundColor(Color.Transparent)
        .fontColor('#007AFF')
        .onClick(() => {
          this.loadLogs();
        })
    }
    .width('100%')
    .height(60)
    .backgroundColor(Color.White)
    .padding({ left: 20, right: 20 })
    .border({ width: { bottom: 1 }, color: '#e0e0e0' })
  }

  @Builder
  buildFilters() {
    Row() {
      // 日志级别筛选
      Select([
        { value: 'all' },
        { value: 'info' },
        { value: 'warning' },
        { value: 'error' }
      ])
        .selected(['all', 'info', 'warning', 'error'].indexOf(this.selectedLevel))
        .onSelect((index: number, value: string) => {
          this.selectedLevel = value;
        })
        .width('45%')
        .height(36)
        .backgroundColor('#f8f9fa')
        .borderRadius(8)

      // 模块筛选
      Select([
        { value: 'all' },
        { value: '用户管理' },
        { value: '课程管理' },
        { value: '测验管理' },
        { value: '学习管理' },
        { value: '系统管理' }
      ])
        .selected(['all', '用户管理', '课程管理', '测验管理', '学习管理', '系统管理'].indexOf(this.selectedModule))
        .onSelect((index: number, value: string) => {
          this.selectedModule = value;
        })
        .width('45%')
        .height(36)
        .backgroundColor('#f8f9fa')
        .borderRadius(8)
    }
    .width('100%')
    .justifyContent(FlexAlign.SpaceBetween)
    .padding(20)
    .backgroundColor(Color.White)
    .border({ width: { bottom: 1 }, color: '#e0e0e0' })
  }

  @Builder
  buildLogList() {
    if (this.isLoading) {
      Column() {
        LoadingProgress()
          .width(50)
          .height(50)
        Text('加载中...')
          .fontSize(16)
          .margin({ top: 20 })
      }
      .width('100%')
      .height('100%')
      .justifyContent(FlexAlign.Center)
    } else {
      List() {
        ForEach(this.getFilteredLogs(), (log: SystemLog) => {
          ListItem() {
            this.buildLogItem(log)
          }
        })
      }
      .width('100%')
      .height('100%')
      .backgroundColor(Color.Transparent)
    }
  }

  @Builder
  buildLogItem(log: SystemLog) {
    Column() {
      Row() {
        // 日志级别标签
        Text(this.getLevelText(log.level))
          .fontSize(12)
          .fontWeight(FontWeight.Bold)
          .backgroundColor(this.getLevelColor(log.level))
          .fontColor(Color.White)
          .padding({ left: 8, right: 8, top: 4, bottom: 4 })
          .borderRadius(4)

        // 时间戳
        Text(log.timestamp)
          .fontSize(12)
          .fontColor('#666')
          .flexGrow(1)
          .textAlign(TextAlign.End)
      }
      .width('100%')
      .margin({ bottom: 8 })

      // 模块和操作
      Row() {
        Text(log.module)
          .fontSize(14)
          .fontWeight(FontWeight.Medium)
          .fontColor('#333')
        
        Text('•')
          .fontSize(14)
          .fontColor('#999')
          .margin({ left: 8, right: 8 })
        
        Text(log.action)
          .fontSize(14)
          .fontColor('#666')
      }
      .width('100%')
      .margin({ bottom: 8 })

      // 描述
      Text(log.description)
        .fontSize(14)
        .fontColor('#333')
        .width('100%')
        .margin({ bottom: 8 })

      // 用户信息
      if (log.userName) {
        Row() {
          Text('操作人：')
            .fontSize(12)
            .fontColor('#999')
          
          Text(log.userName)
            .fontSize(12)
            .fontColor('#666')
            .fontWeight(FontWeight.Medium)
        }
        .width('100%')
      }
    }
    .width('100%')
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(8)
    .margin({ bottom: 12, left: 20, right: 20 })
    .shadow({
      radius: 4,
      color: '#00000020',
      offsetX: 0,
      offsetY: 2
    })
  }
} 