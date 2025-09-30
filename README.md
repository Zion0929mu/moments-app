# 朋友圈 - 仿微信朋友圈Android应用

> 🎉 **第一次使用?** 请先阅读 → [**START_HERE.md**](START_HERE.md)  
> 📖 **查找文档?** 请查看 → [**文档索引**](DOCS_INDEX.md)

## 项目简介
这是一个完全模拟微信朋友圈界面和功能的原生Android应用,使用Kotlin开发。

## 核心功能
- ✅ **Google账号登录** - 使用Google账号快速登录
- ✅ **发布动态** - 支持发布文字和图片(最多9张)
- ✅ **查看朋友圈** - 时间线展示所有动态
- ✅ **点赞功能** - 对动态点赞,查看点赞列表
- ✅ **评论功能** - 发表评论,查看评论列表
- ✅ **图片浏览** - 全屏查看图片,支持缩放和滑动
- ✅ **本地存储** - 所有数据保存在本地设备

## 技术栈
- **开发语言**: Kotlin
- **最低版本**: Android 7.0 (API 24)
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room Database
- **登录**: Google Sign-In
- **图片加载**: Glide
- **依赖注入**: Hilt (可选)

## 项目结构
```
app/
├── src/main/
│   ├── java/com/moments/app/
│   │   ├── data/          # 数据层
│   │   │   ├── model/     # 数据模型
│   │   │   ├── local/     # 本地数据库
│   │   │   └── repository/# 数据仓库
│   │   ├── ui/            # UI层
│   │   │   ├── login/     # 登录界面
│   │   │   ├── moments/   # 朋友圈界面
│   │   │   ├── post/      # 发布动态界面
│   │   │   └── photo/     # 图片查看界面
│   │   ├── viewmodel/     # ViewModel层
│   │   └── utils/         # 工具类
│   ├── res/               # 资源文件
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 如何使用

### 本地开发(需要Android Studio)
1. 克隆项目到本地
2. 用Android Studio打开项目
3. 配置Google登录(见下方说明)
4. 连接Android设备或启动模拟器
5. 点击运行按钮

### 云端编译(GitHub Actions - 推荐)
**适合电脑配置较差的情况**

1. **Fork或上传项目到GitHub**
   - 将项目代码上传到你的GitHub仓库

2. **配置Google登录密钥**
   - 在GitHub仓库的Settings -> Secrets中添加`GOOGLE_SERVICES_JSON`(google-services.json的base64编码)

3. **自动编译**
   - 每次push代码到main分支,GitHub Actions会自动编译
   - 编译成功后,在Actions标签页下载生成的APK文件

4. **下载安装**
   - 下载APK文件到手机
   - 安装并运行

## Google登录配置步骤

### 1. 创建Firebase项目
1. 访问 [Firebase Console](https://console.firebase.google.com/)
2. 点击"添加项目"
3. 输入项目名称,按提示完成创建

### 2. 添加Android应用
1. 在Firebase项目中点击"添加应用" -> "Android"
2. 输入包名: `com.moments.app`
3. 下载 `google-services.json` 文件
4. 将文件放到 `app/` 目录下

### 3. 启用Google登录
1. 在Firebase控制台左侧菜单选择"Authentication"
2. 点击"开始使用"
3. 在"登录方法"中启用"Google"

### 4. 配置SHA证书指纹
#### 调试版本(开发用)
```bash
# Windows
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android

# 复制SHA-1和SHA-256指纹,添加到Firebase项目设置中
```

#### 正式版本(发布用)
```bash
keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
```

## 构建APK

### Debug版本
```bash
./gradlew assembleDebug
# APK位置: app/build/outputs/apk/debug/app-debug.apk
```

### Release版本
```bash
./gradlew assembleRelease
# APK位置: app/build/outputs/apk/release/app-release.apk
```

## GitHub Actions自动编译说明

项目已配置GitHub Actions工作流,每次推送代码会自动:
1. 检查代码
2. 配置编译环境
3. 编译生成APK
4. 上传APK作为构建产物

**如何下载编译好的APK:**
1. 访问你的GitHub仓库
2. 点击顶部"Actions"标签
3. 选择最新的成功构建
4. 在"Artifacts"区域下载APK

## 常见问题

### Q: Google登录失败
**A:** 检查以下几点:
- 确认google-services.json文件已正确放置
- 确认SHA-1指纹已添加到Firebase
- 确认手机上已安装Google Play服务

### Q: 编译失败
**A:** 常见原因:
- Gradle版本不匹配 - 使用Android Studio推荐版本
- SDK版本未安装 - 在SDK Manager中安装对应版本
- 依赖下载失败 - 检查网络连接

### Q: GitHub Actions编译超时
**A:** 免费账户有时间限制,可以:
- 优化构建配置
- 使用缓存加速
- 减少不必要的依赖

## 开发计划

### 当前版本 (v1.0)
- [x] 项目基础架构
- [x] Google登录集成
- [x] 朋友圈UI设计
- [x] 发布动态功能
- [x] 点赞和评论功能
- [x] 本地数据存储
- [x] GitHub Actions配置

### 未来计划 (v2.0)
- [ ] 图片编辑功能
- [ ] 视频发布支持
- [ ] 定位功能
- [ ] 提醒谁看
- [ ] 朋友圈背景
- [ ] 主题切换

## 文档导航

- 📖 **[快速开始](QUICKSTART.md)** - 适合初学者的简化指南
- 🚀 **[GitHub Actions指南](GITHUB_ACTIONS_GUIDE.md)** - 云端编译详细教程
- 📱 **应用截图** - (待添加)

## 项目亮点与技术实现

### 架构设计
本项目采用清晰的**MVVM架构**,严格分离数据层、业务层和UI层:
- **数据层**: Room数据库 + Repository模式实现数据持久化
- **业务层**: ViewModel处理业务逻辑,使用LiveData实现数据响应式更新
- **UI层**: Activity/Fragment + RecyclerView实现界面展示

### 核心技术点
1. **Google登录集成** - Firebase Authentication + Google Sign-In SDK
2. **图片网格布局** - 根据图片数量动态计算列数(1/2/3列)
3. **图片缩放查看** - PhotoView库实现双指缩放、拖拽
4. **复杂列表实现** - RecyclerView嵌套、ViewHolder复用优化
5. **数据类型转换** - Room TypeConverter处理复杂数据结构
6. **权限适配** - Android 6-13权限动态申请适配

### 性能优化
- Glide图片加载缓存策略
- RecyclerView ViewHolder复用
- 协程处理异步操作,避免主线程阻塞
- Room数据库索引优化查询速度

## 项目反思与改进方向

### 已完成的核心功能
✅ 完整的MVVM架构实现  
✅ 美观的微信风格UI设计  
✅ 流畅的用户交互体验  
✅ Google登录集成  
✅ 本地数据持久化  
✅ GitHub Actions自动化编译  

### 潜在问题与解决方案

#### 1. 数据存储局限性
**问题**: 当前使用本地Room数据库,数据无法跨设备同步  
**改进方案**:
- 集成Firebase Firestore实现云端存储
- 添加数据同步机制
- 实现离线缓存策略

#### 2. 图片存储方式
**问题**: 图片以URI形式存储,可能因权限变化失效  
**改进方案**:
- 将图片复制到应用私有目录
- 实现图片压缩减少存储空间
- 支持图片云端上传(Firebase Storage)

#### 3. 用户系统简化
**问题**: 只支持单用户,没有好友关系  
**改进方案**:
- 添加用户列表和好友关系
- 实现好友动态过滤
- 支持用户搜索和添加好友

#### 4. 性能优化空间
**问题**: 大量图片加载可能导致内存占用过高  
**改进方案**:
- 实现图片预加载和分页加载
- 优化RecyclerView滚动性能
- 添加图片内存缓存LRU策略

#### 5. 功能扩展建议
- [ ] 支持视频发布和播放
- [ ] 添加图片滤镜和编辑功能
- [ ] 实现消息推送通知
- [ ] 支持暗黑模式主题
- [ ] 添加数据导出功能
- [ ] 实现多语言支持

### 代码质量改进
- 添加单元测试覆盖核心逻辑
- 使用Hilt依赖注入简化代码
- 实现更完善的错误处理机制
- 添加日志系统便于调试

## 学习价值

本项目适合以下学习场景:
- 🎓 **Android开发入门** - 完整的项目结构和代码注释
- 💼 **MVVM架构实践** - 清晰的分层设计
- 🔥 **Firebase集成** - Google登录、数据库使用
- 🛠️ **CI/CD实践** - GitHub Actions自动化构建
- 📱 **真实项目经验** - 模拟真实社交应用开发

## 许可证
MIT License - 可自由使用、修改和分发

## 贡献指南
欢迎提交Issue和Pull Request!

## 致谢
- Android官方文档和示例
- Firebase平台支持
- 开源社区的优秀库

## 联系方式
如有问题,请在GitHub Issues中提出。

---
**开发时间**: 2025年9月30日  
**开发者**: Cascade AI  
**版本**: v1.0.0  
**最后更新**: 2025-09-30
