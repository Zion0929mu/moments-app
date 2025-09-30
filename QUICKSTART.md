# 快速开始指南

这是一份针对初学者的简化版使用指南。

## 🎯 第一步: 选择编译方式

根据你的电脑配置,选择合适的编译方式:

### 方式A: GitHub Actions云端编译 (推荐 - 适合配置差的电脑)
- ✅ 不需要安装Android Studio
- ✅ 不占用本地资源
- ✅ 编译速度快
- ❌ 需要GitHub账号
- ❌ 需要上传代码到GitHub

👉 **使用这种方式,请查看 [GITHUB_ACTIONS_GUIDE.md](GITHUB_ACTIONS_GUIDE.md)**

### 方式B: 本地编译 (适合有Android Studio的用户)
- ✅ 可以即时调试
- ✅ 数据保密性好
- ❌ 需要安装Android Studio (约4GB)
- ❌ 首次编译较慢(15-30分钟)

👉 **使用这种方式,继续往下看**

---

## 📱 方式B: 本地编译步骤

### 1. 安装Android Studio

1. 访问 https://developer.android.com/studio
2. 下载Android Studio最新版本
3. 安装(选择默认选项即可)
4. 首次启动会下载SDK,需要等待一段时间

### 2. 配置Google登录

#### 2.1 创建Firebase项目

1. 访问 https://console.firebase.google.com/
2. 点击"添加项目"
3. 输入项目名称(如: 朋友圈App)
4. 按照提示完成创建

#### 2.2 添加Android应用

1. 在Firebase项目页面,点击Android图标
2. **包名**填写: `com.moments.app`
3. 点击"注册应用"
4. **重要**: 下载 `google-services.json` 文件
5. 将下载的文件复制到项目的 `app/` 文件夹中

#### 2.3 启用Google登录

1. 在Firebase控制台,点击左侧"Authentication"
2. 点击"Get started"
3. 在"登录方法"标签中,点击"Google"
4. 打开开关,点击"保存"

#### 2.4 配置SHA证书指纹

在命令行运行以下命令:

**Windows:**
```cmd
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Mac/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

复制输出中的 `SHA-1` 和 `SHA-256` 指纹,添加到Firebase项目设置中:
1. Firebase项目 -> 项目设置
2. 找到你的Android应用
3. 添加指纹

### 3. 打开项目并编译

1. 启动Android Studio
2. 选择"Open"打开项目文件夹
3. 等待Gradle同步完成(首次较慢,请耐心等待)
4. 点击顶部工具栏的绿色三角形▶️按钮(或按Shift+F10)
5. 选择连接的设备或模拟器
6. 等待编译和安装完成

### 4. 如何生成APK文件

如果你想生成APK文件分享给别人:

1. 点击菜单 Build -> Build Bundle(s) / APK(s) -> Build APK(s)
2. 等待编译完成
3. 点击弹出的通知中的"locate"链接
4. 找到 `app-debug.apk` 文件
5. 将APK传输到手机安装

---

## 🔧 常见问题解决

### Q: Gradle同步失败
**A:** 
1. 检查网络连接
2. 如果在中国大陆,可能需要配置镜像:
   - 打开 `build.gradle.kts` 文件
   - 将 `google()` 和 `mavenCentral()` 替换为国内镜像

### Q: 编译错误 "google-services.json not found"
**A:** 
1. 确认 `google-services.json` 文件在 `app/` 文件夹中
2. 点击 File -> Sync Project with Gradle Files

### Q: 模拟器太慢
**A:** 
1. 推荐使用真机测试(用USB连接手机)
2. 或者创建x86架构的模拟器(更快)

### Q: Google登录失败
**A:** 检查:
1. `google-services.json` 文件是否正确
2. SHA-1指纹是否添加到Firebase
3. 是否启用了Google登录方法
4. 手机上是否安装了Google Play服务

---

## 📖 功能使用说明

### 登录
- 打开应用,点击"Google登录"按钮
- 选择你的Google账号
- 首次登录需要授权

### 查看朋友圈
- 登录后自动进入朋友圈界面
- 下拉刷新(暂时没有网络数据)
- 向下滚动查看更多动态

### 发布动态
1. 点击右下角绿色"+"按钮
2. 输入文字内容
3. 点击添加图片按钮选择图片(最多9张)
4. 点击右上角"发表"按钮

### 点赞和评论
1. 点击动态右下角的三点按钮
2. 选择"赞"或"评论"
3. 输入评论内容后点击"发送"

### 查看图片
- 点击动态中的图片可以全屏查看
- 左右滑动切换图片
- 双指缩放图片
- 点击图片关闭

### 删除动态
- 只能删除自己发布的动态
- 点击动态右上角的三点按钮
- 选择"删除"

---

## 💡 小提示

1. **数据存储在本地** - 所有数据保存在手机上,卸载应用会丢失
2. **Google登录必须** - 首次使用必须登录Google账号
3. **不支持多设备同步** - 这是单机版应用
4. **图片存储** - 图片以URI形式存储,不会占用太多空间

---

## 📞 需要帮助?

1. 查看完整文档: [README.md](README.md)
2. GitHub Actions指南: [GITHUB_ACTIONS_GUIDE.md](GITHUB_ACTIONS_GUIDE.md)
3. 提交问题: 在GitHub仓库创建Issue

---

**祝使用愉快!** 🎉
