# 100以内加减法练习题系统（Java 控制台）

运行方式（Windows PowerShell）：

```bash
# 进入项目根目录
cd C:\Users\anna\Desktop\demo1

# 编译
javac -encoding UTF-8 -d out src\app\**\*.java src\app\*.java

# 运行
java -cp out app.Main
```

初始用户（位于 `data/users.csv`）：
- 教师：`t1 / 123456`
- 学生：`s1 / 123456`
- 学生：`s2 / 123456`

数据存储：
- `data/exercises.csv`：题库元数据（创建时间、创建人、发布状态、发布时间等）
- `data/questions/<exerciseId>.txt`：题目与标准答案
- `data/scores.csv`：成绩概要
- `data/attempts/<exerciseId>_<student>.csv`：学生作答明细

约束与规则：
- 题库一次生成数量 1-100
- 加减法均保证结果在 0-100（含）内
- 同一题库内题目不重复
- 展示题目时按每行 6 列排版
