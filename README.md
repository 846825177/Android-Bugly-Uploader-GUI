# 🐞 Android Bugly Uploader GUI 工具

一个用于上传符号表（mapping.txt）到 [Bugly](https://bugly.qq.com/) 的轻量图形界面工具，支持一键保存配置并自动读取。

---

## ✨ 功能简介

- 💾 **配置持久化**：首次输入配置信息后会自动保存到 `config.txt`，下次打开自动加载。
- 🧩 **参数包含**：
  - `appId`
  - `appKey`
  - `bundleId`
  - `version`
  - `versionCode`
  - `mapping.txt` 路径
  - `bugly jar` 包路径
- 🔄 **每次只需修改 version/versionCode 后即可上传**
- 🖱️ **双击运行 `.jar` 即可启动 GUI**（前提是已配置 Java 环境）

---

## 📂 文件结构

| 文件名                        | 描述 |
|-----------------------------|------|
| `BuglyUploaderGui.jar`      | 可执行的 GUI 工具，配置好 Java 环境后双击即可运行 |
| `BuglyUploaderGui.java`     | 工具的源码文件（Java Swing 编写） |
| `build.sh`                  | macOS 构建脚本，将 `.java` 编译为 `.jar` |
| `config.txt`                | 用户保存的配置文件，首次填写后自动生成 |

---

## 🛠️ 使用方法

### ✅ 第一次使用

1. 双击运行 `BuglyUploaderGui.jar`（确保 Java 环境已安装）
2. 填写各项配置信息
3. 点击上传，程序将在当前目录下生成一个 `config.txt` 文件

### 📝 后续使用

- 只需修改版本号（`version` 和 `versionCode`）即可点击上传，无需重复填写其他字段。

---

## 💻 手动构建（适用于开发者）

如需自行编译源码，请使用 macOS 终端运行以下命令：

```bash
chmod +x build.sh
./build.sh
