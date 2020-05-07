# Hadoop OSS支持

这个项目是开源Hadoop 3.2.1修改，以支持通过Ram Role直接访问阿里云OSS

## 使用方式

在阿里云的ECS机器安装Hadoop，并给ECS赋予对应OSS的权限RAM Role。

hadoop-tools/hadoop-aliyun 打包，替换HADOOP_HOME/share/hadoop/tools/lib/ 路径下的hadoop-aliyun-x.x.x.jar

修改core-site.xml的配置，添加如下配置

```xml
<configuration>
  <property>
    <name>fs.oss.endpoint</name>
    <value>oss-cn-beijing-internal.aliyuncs.com</value>
  </property>
  <property>
    <name>fs.oss.impl</name>
    <value>org.apache.hadoop.fs.aliyun.oss.AliyunOSSFileSystem</value>
  </property>
  <property>
    <name>fs.oss.credentials.provider</name>
    <value>org.apache.hadoop.fs.aliyun.oss.AssumedRoleCredentialProvider</value>
  </property>
  <property>
    <name>fs.oss.assumed.role.arn</name>
    <value>acs:ram::ACCOUNT:role/ECSROLE</value>
  </property>
  <property>
    <name>fs.oss.assumed.role.name</name>
    <value>ECSROLE</value>
  </property>
  <property>
    <name>fs.oss.assumed.role.sts.endpoint</name>
    <value>sts-vpc.cn-beijing.aliyuncs.com</value>
    <description>
      Role ARN for the role to be assumed.
      Required if the fs.oss.credentials.provider is
      org.apache.hadoop.fs.aliyun.oss.AssumedRoleCredentialProvider.
    </description>
  </property>
<configuration>
```
其中ACCOUNT是对应的阿里云主账号，一般是16位数字。ECSROLE是给ECS机器赋予的Ram Role。如果不是北京区的，需要修改最后一个sts的对应endpoint。
配置完成以后就可以使用hdfs指令访问有权限的OSS桶。