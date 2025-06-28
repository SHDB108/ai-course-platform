# AI课程平台后端测试说明

## 测试概述

本项目包含完整的单元测试和集成测试，确保代码质量和功能正确性。

## 测试结构

```
src/test/java/com/example/aicourse/
├── AICourseApplicationTest.java          # Spring Boot应用集成测试
├── service/
│   ├── AuthServiceTest.java              # 用户认证服务测试
│   ├── StudentServiceTest.java           # 学生管理服务测试
│   ├── CourseServiceTest.java            # 课程管理服务测试
│   └── TaskServiceTest.java              # 任务管理服务测试
└── utils/
    └── ResultTest.java                   # 工具类测试
```

## 运行测试

### 1. 运行所有测试

```bash
# 在backend目录下执行
mvn test
```

### 2. 运行特定测试类

```bash
# 运行AuthService测试
mvn test -Dtest=AuthServiceTest

# 运行StudentService测试
mvn test -Dtest=StudentServiceTest

# 运行CourseService测试
mvn test -Dtest=CourseServiceTest

# 运行TaskService测试
mvn test -Dtest=TaskServiceTest
```

### 3. 运行特定测试方法

```bash
# 运行特定的测试方法
mvn test -Dtest=AuthServiceTest#testLoginSuccess
```

### 4. 在IDE中运行

- **IntelliJ IDEA**: 右键测试类 → Run Tests
- **Eclipse**: 右键测试类 → Run As → JUnit Test
- **VS Code**: 点击测试方法旁边的运行按钮

## 测试覆盖率

### 1. 生成覆盖率报告

```bash
# 运行测试并生成JaCoCo覆盖率报告
mvn clean test jacoco:report
```

### 2. 查看覆盖率报告

覆盖率报告位置：`target/site/jacoco/index.html`

### 3. 覆盖率要求

- **核心功能**（用户注册、登录、数据查询等）：100% 覆盖
- **辅助功能**（工具类方法）：至少 80% 覆盖
- **整体覆盖率**：至少 85%

## 测试类型说明

### 1. 单元测试 (Unit Tests)

使用Mockito框架进行单元测试，测试各个服务类的独立功能：

- **正常情况测试**：验证方法在正确输入下的输出
- **异常情况测试**：验证方法对非法输入的处理
- **边界条件测试**：如空字符串、极值等

### 2. 集成测试 (Integration Tests)

测试Spring Boot应用的启动和基本配置：

- **上下文加载测试**：验证Spring上下文正常加载
- **应用启动测试**：验证应用能够正常启动

## 测试数据

### 1. 测试数据库

- 使用H2内存数据库进行测试
- 配置文件：`src/test/resources/application-test.yml`
- 每次测试后自动清理数据

### 2. Mock数据

- 使用Mockito创建模拟对象
- 避免对外部依赖的依赖
- 提高测试执行速度

## 测试最佳实践

### 1. 测试命名规范

- 测试类命名：`{ClassName}Test`
- 测试方法命名：`test{MethodName}_{Scenario}`

### 2. 测试结构

```java
@Test
void testMethodName_Scenario() {
    // 1. 准备测试数据 (Arrange)
    // 2. 执行被测试方法 (Act)
    // 3. 验证结果 (Assert)
}
```

### 3. 断言使用

```java
// 常用断言方法
assertEquals(expected, actual);           // 验证期望值等于实际值
assertTrue(condition);                    // 验证条件为 true
assertFalse(condition);                   // 验证条件为 false
assertNull(object);                       // 验证对象为 null
assertNotNull(object);                    // 验证对象不为 null
assertThrows(Exception.class, executable); // 验证代码抛出指定异常
```

## 常见问题

### 1. 测试失败

- 检查测试数据是否正确
- 确认Mock对象配置是否正确
- 查看测试日志获取详细错误信息

### 2. 覆盖率不达标

- 检查是否有未测试的分支
- 确认所有公共方法都有对应的测试
- 添加边界条件测试

### 3. 测试执行缓慢

- 使用内存数据库
- 减少不必要的Mock对象
- 优化测试数据准备

## 持续集成

### 1. 自动化测试

- 每次代码提交自动运行测试
- 测试失败阻止代码合并
- 覆盖率报告自动生成

### 2. 测试环境

- 使用独立的测试数据库
- 避免影响生产环境
- 测试数据隔离

## 扩展测试

### 1. 添加新测试

1. 在对应包下创建测试类
2. 遵循命名规范
3. 编写完整的测试用例
4. 确保覆盖率达标

### 2. 测试新功能

1. 先编写测试用例
2. 实现功能代码
3. 运行测试验证
4. 检查覆盖率

## 联系方式

如有测试相关问题，请联系开发团队。 