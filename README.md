<img alt="Octodroid" align="right" src="https://raw.githubusercontent.com/slapperwan/gh4a/master/app/src/main/res/drawable-xxhdpi/octodroid.png">

OctoDroid
=========
This application provides access to [GitHub](https://github.com/) and lets you stay connected with your network

Download
--------
[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80px">](https://f-droid.org/packages/com.gh4a/)

Main features
-------------

### Repository
* List repositories
* Watch/unwatch repository
* View branches/tags
* View pull requests
* View contributors
* View watchers/networks
* View issues

### User
* View basic information
* Activity feeds
* Follow/unfollow user
* View public/watched repositories
* View followers/following
* View organizations (if type is user)
* View members (if type is organization)

### Issue
* List issues
* Filter by label, assignee or milestone
* Create/edit/close/reopen issue
* Comment on issue
* Manage labels
* Manage milestones

### Commit
* View commit (shows files changed/added/deleted)
* Diff viewer with colorized HTML
* View commit history on each file

### Tree/File browser
* Browse source code
* View code with syntax highlighting

### Gist
* List public gists
* View gist content

### Explore Github
* Public timeline
* Trending repos (today, week, month, forever)
* GitHub blog

*..and many more*

How to Build Octodroid
----------------------

### 登录方式

App 支持两种登录方式，推荐用 **Token 登录**（不需要配置任何东西）：

#### 方式一：Token 登录（推荐 ✅）

1. 打开 https://github.com/settings/tokens
2. 点 "Generate new token (classic)"
3. 勾选权限：`repo`、`user`、`gist`、`read:org`、`notifications`
4. 生成 Token，复制
5. 打开 App → 选 "Login using access token" → 粘贴 → 登录成功

**不需要 `client.properties`，不需要注册 OAuth App。**

#### 方式二：OAuth 浏览器登录

1. 在 https://github.com/settings/developers 注册一个 OAuth App
   - Callback URL **必须填** `gh4a://oauth`
2. 在源码根目录创建 `client.properties`：
   ```
   ClientId=你的client_id
   ClientSecret=你的client_secret
   ```
3. 构建 APK（见下方）

### 构建 APK

- 确保安装了 Android SDK platform 和 build-tools

```bash
# 用 GitHub Actions 自动构建（推荐）
# Push 到 master 分支后会自动触发，APK 在 workflow artifacts 中

# 或者本地构建
./gradlew assembleDebug
```

APK 产物：`app/build/outputs/apk/debug/app-debug.apk`
./gradlew tasks
```

Open Source Libraries
---------------------
* [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
* [AndroidSVG](https://github.com/BigBadaboom/androidsvg)
* [AndroidX](https://github.com/androidx/androidx)
* [emoji-java](https://github.com/vdurmont/emoji-java)
* [GitHubSdk](https://github.com/maniac103/GitHubSdk)
* [HoloColorPicker](https://github.com/LarsWerkman/HoloColorPicker)
* [MarkdownEdit](https://github.com/Tunous/MarkdownEdit)
* [Material Design Icons](https://github.com/google/material-design-icons)
* [PrettyTime](https://github.com/ocpsoft/prettytime)
* [Recycler Fast Scroll](https://github.com/pluscubed/recycler-fast-scroll)
* [Retrofit](https://github.com/square/retrofit)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxLoader](https://github.com/maniac103/RxLoader)
* [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)

Contributions
-------------
* [kageiit](https://github.com/kageiit) - Improvements and bug fixes
* [maniac103](https://github.com/maniac103) - Improvements, bug fixes and new features
* [ARoiD](https://github.com/ARoiD) - Testing
* [extremis (Steven Mautone)](https://github.com/extremis) - OctoDroid name and the new icon
* [zquestz](https://github.com/zquestz) - Thanks for the application icon
* [cketti](https://github.com/cketti)
* [Tunous](https://github.com/Tunous) - Improvements, bug fixes and new features
