# power-detect

设备功率监测上位机程序。

1. 执行 `mvn clean package -Dmaven.test.skip=true` 将程序打为可执行jar包 `power-detect.jar`。
2. 执行 `java -jar power-detect.jar` 运行程序。

## 测试类
客户端测试入口：com.yhwt.pd.test.TestClientMain

服务端测试入口：com.yhwt.pd.test.TestServerMain