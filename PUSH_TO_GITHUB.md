# 🚀 推送项目到GitHub并开始编译

## 步骤1: 初始化Git仓库

在项目目录打开命令行(PowerShell或CMD),运行:

```bash
# 初始化Git仓库
git init

# 添加所有文件
git add .

# 提交代码
git commit -m "Initial commit: 朋友圈Android应用"
```

## 步骤2: 在GitHub创建仓库

1. 访问 https://github.com
2. 点击右上角 "+" -> "New repository"
3. 填写信息:
   - **Repository name**: moments-app (或你喜欢的名字)
   - **Description**: 仿微信朋友圈Android应用
   - **Public** 或 **Private**: 选择Public(可以使用免费的GitHub Actions)
   - **不要勾选** "Initialize this repository with a README"
4. 点击 "Create repository"

## 步骤3: 连接远程仓库并推送

在GitHub创建仓库后,会看到推送命令,复制运行:

```bash
# 添加远程仓库(替换YOUR_USERNAME为你的GitHub用户名)
git remote add origin https://github.com/YOUR_USERNAME/moments-app.git

# 重命名分支为main
git branch -M main

# 推送代码
git push -u origin main
```

**示例**:
如果你的GitHub用户名是 `john`,仓库名是 `moments-app`:
```bash
git remote add origin https://github.com/john/moments-app.git
git branch -M main
git push -u origin main
```

## 步骤4: 配置GitHub Actions(可选但推荐)

### 4.1 配置google-services.json

如果你已经有`google-services.json`文件:

#### Windows PowerShell:
```powershell
# 转换为Base64
[Convert]::ToBase64String([System.IO.File]::ReadAllBytes("app\google-services.json")) | Set-Clipboard
```

#### Linux/Mac:
```bash
# 转换为Base64
base64 app/google-services.json | pbcopy  # Mac
base64 app/google-services.json | xclip   # Linux
```

### 4.2 添加Secret

1. 访问你的GitHub仓库
2. 点击 "Settings"
3. 左侧菜单点击 "Secrets and variables" -> "Actions"
4. 点击 "New repository secret"
5. Name: `GOOGLE_SERVICES_JSON`
6. Value: 粘贴刚才复制的Base64内容
7. 点击 "Add secret"

**注意**: 如果不配置这一步,编译也能成功,但Google登录功能将无法使用。

## 步骤5: 查看编译进度

1. 访问你的GitHub仓库
2. 点击顶部 "Actions" 标签
3. 会看到一个正在运行的工作流(黄色圆点)
4. 点击进入查看详细进度
5. 等待5-10分钟,编译完成(绿色对勾)

## 步骤6: 下载APK

编译成功后:

1. 在Actions页面,点击最新的成功构建
2. 向下滚动到 "Artifacts" 区域
3. 下载:
   - `app-debug.apk` - 调试版本(推荐用于测试)
   - `app-release-unsigned.apk` - 发布版本(未签名)
4. 将APK传输到手机并安装

## 常见问题

### Q: 推送时要求输入用户名和密码?
**A**: GitHub已不支持密码认证,需要使用Personal Access Token:

1. 访问 GitHub Settings -> Developer settings -> Personal access tokens -> Tokens (classic)
2. 点击 "Generate new token (classic)"
3. 勾选 "repo" 权限
4. 生成token并复制
5. 推送时,用户名输入你的GitHub用户名,密码输入token

**或者使用GitHub Desktop**:
1. 下载 GitHub Desktop
2. 登录你的GitHub账号
3. 添加本地仓库
4. 点击 "Publish repository"

### Q: 编译失败怎么办?
**A**: 

1. 点击失败的工作流查看错误日志
2. 如果是google-services.json相关错误:
   - 检查Secret是否正确配置
   - 或者忽略此错误,应用仍可编译
3. 如果是其他错误,查看错误信息或提交Issue

### Q: 编译太慢?
**A**: 

- 首次编译约8-10分钟(需要下载依赖)
- 后续编译5-7分钟(有缓存)
- GitHub Free账户有足够的免费时间

## 快速命令汇总

```bash
# 1. 初始化并提交
git init
git add .
git commit -m "Initial commit: 朋友圈Android应用"

# 2. 连接GitHub(替换YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/moments-app.git
git branch -M main
git push -u origin main

# 3. 后续更新代码
git add .
git commit -m "更新描述"
git push
```

## 下一步

✅ 代码已推送到GitHub  
✅ GitHub Actions自动编译  
✅ 等待编译完成(5-10分钟)  
✅ 下载APK并安装到手机  
✅ 享受你的朋友圈应用!  

---

**需要帮助?** 查看 [GITHUB_ACTIONS_GUIDE.md](GITHUB_ACTIONS_GUIDE.md) 获取更详细的说明。
