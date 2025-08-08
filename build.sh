#!/bin/bash

# 清理旧文件
rm -rf out
mkdir out

# 编译 Java 源文件
javac -d out BuglyUploaderGui.java

# 创建 MANIFEST 文件
echo "Main-Class: BuglyUploaderGui" > out/MANIFEST.MF

# 打包为 .jar 文件
cd out
jar cfm ../BuglyUploaderGui.jar MANIFEST.MF BuglyUploaderGui.class
cd ..
