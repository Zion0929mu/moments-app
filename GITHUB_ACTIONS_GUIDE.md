# GitHub Actions自动编译配置指南

## 为什么使用GitHub Actions?

如果你的电脑配置较差,本地编译Android项目会很慢甚至卡死。GitHub Actions提供**免费的云端编译服务**,可以在GitHub的服务器上自动编译你的项目,生成APK文件供下载。

## 配置步骤

### 第一步: 上传项目到GitHub

1. **创建GitHub账号**
   - 访问 https://github.com
   - 点击"Sign up"注册账号

2. **创建新仓库**
   - 登录后点击右上角"+"按钮
   - 选择"New repository"
   - 输入仓库名称(如: moments-app)
   - 选择"Public"(公开)或"Private"(私有)
   - 点击"Create repository"

3. **上传代码**
   
   **方法一: 使用Git命令行**
   ```bash
   # 在项目目录打开命令行
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/你的用户名/moments-app.git
   git push -u origin main
   ```
   
   **方法二: 使用GitHub Desktop**
   - 下载并安装GitHub Desktop
   - 点击"Add" -> "Add existing repository"
   - 选择项目文件夹
   - 点击"Publish repository"

### 第二步: 配置Google登录密钥(可选但推荐)

为了让应用能正常使用Google登录,需要配置`google-services.json`文件。

1. **获取google-services.json**
   - 按照README.md中的说明创建Firebase项目
   - 下载`google-services.json`文件

2. **转换为Base64编码**
   
   **Windows系统:**
   ```powershell
   # 在PowerShell中运行
   [Convert]::ToBase64String([System.IO.File]::ReadAllBytes("app\google-services.json")) | Set-Clipboard
   ```
   
   **Linux/Mac系统:**
   ```bash
   base64 app/google-services.json | pbcopy  # Mac
   base64 app/google-services.json | xclip   # Linux
   ```
   
   或使用在线工具: https://www.base64encode.org/

3. **添加到GitHub Secrets**
   - 访问你的GitHub仓库
   - 点击"Settings" -> "Secrets and variables" -> "Actions"
   - 点击"New repository secret"
   - Name填写: `GOOGLE_SERVICES_JSON`
   - Value粘贴刚才复制的Base64编码内容
   - 点击"Add secret"

**注意:** 如果不配置此步骤,编译也能成功,但Google登录功能将无法使用。

### 第三步: 触发自动编译

GitHub Actions已配置为自动触发,有以下几种方式:

1. **推送代码时自动编译**
   - 每次push代码到main/master分支
   - 自动开始编译

2. **手动触发编译**
   - 访问仓库的"Actions"标签
   - 选择"Android CI"工作流
   - 点击"Run workflow"按钮
   - 选择分支,点击"Run workflow"

### 第四步: 下载编译好的APK

1. **查看编译进度**
   - 访问仓库的"Actions"标签
   - 点击最新的工作流运行
   - 等待编译完成(通常5-10分钟)

2. **下载APK**
   - 编译成功后,向下滚动到"Artifacts"区域
   - 会看到两个文件:
     - `app-debug.apk` - Debug版本(用于测试)
     - `app-release-unsigned.apk` - Release版本(未签名)
   - 点击下载

3. **安装APK**
   - 将下载的APK传输到手机
   - 在手机上打开文件管理器
   - 点击APK文件安装
   - 如果提示"未知来源",需要在设置中允许安装

## 编译状态说明

在GitHub Actions页面可以看到编译状态:

- 🟡 **黄色圆点** - 正在编译
- ✅ **绿色对勾** - 编译成功
- ❌ **红色叉号** - 编译失败

## 常见问题

### Q1: 编译失败怎么办?

**A:** 点击失败的工作流,查看错误日志:
- 如果是"google-services.json"相关错误,检查是否正确配置了Secret
- 如果是依赖下载失败,可能是网络问题,重新运行即可
- 如果是代码错误,需要修复代码后重新推送

### Q2: 编译时间太长?

**A:** GitHub免费账户编译时间限制:
- 公开仓库: 无限制
- 私有仓库: 每月2000分钟
- 通常单次编译需要5-10分钟

### Q3: 如何签名Release版本?

**A:** Release版本需要签名才能发布到应用商店:

1. 创建签名密钥:
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. 将密钥文件转为Base64并添加到GitHub Secrets:
   - `KEYSTORE_FILE` - 密钥文件的Base64编码
   - `KEYSTORE_PASSWORD` - 密钥库密码
   - `KEY_ALIAS` - 密钥别名
   - `KEY_PASSWORD` - 密钥密码

3. 修改GitHub Actions工作流添加签名步骤

### Q4: 不想用GitHub Actions怎么办?

**A:** 如果你有Android Studio,可以本地编译:
1. 打开Android Studio
2. 打开项目
3. Build -> Build Bundle(s) / APK(s) -> Build APK(s)
4. 等待编译完成
5. APK位置: `app/build/outputs/apk/debug/app-debug.apk`

## 高级技巧

### 自动发布Release

创建Git标签可以自动创建GitHub Release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions会自动将APK附加到Release中。

### 优化编译速度

1. 启用Gradle缓存(已配置)
2. 使用GitHub Actions的缓存功能
3. 减少不必要的依赖

## 相关链接

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Android Gradle插件](https://developer.android.com/studio/build)
- [Firebase控制台](https://console.firebase.google.com/)

---

如有问题,请在GitHub Issues中提出。
