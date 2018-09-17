参考资料： https://blog.csdn.net/u014652699/article/details/73639563  

1. 安装gradle3.5，配置PATH  
    参考资料： https://www.w3cschool.cn/gradle/
2. 下载源码  
    从https://github.com/elastic/elasticsearch下载 elasticsearch-5.6.4-src.zip 并解压
3. 在elasticsearch-5.6.4目录下执行 gradle idea 命令。  
    如果觉得下载速度慢，可以使用国内的镜像。在build.gradle文件中allprojects下配置
    ```
    // injecting groovy property variables into all projects
    allprojects {
      repositories {  
        maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
      }
      // injecting groovy property variables into all projects
      project.ext {
        // for ide hacks...
        isEclipse = System.getProperty("eclipse.launcher") != null || gradle.startParameter.taskNames.contains('eclipse') || gradle.startParameter.taskNames.contains('cleanEclipse')
        isIdea = System.getProperty("idea.active") != null || gradle.startParameter.taskNames.contains('idea') || gradle.startParameter.taskNames.contains('cleanIdea')
        // for backcompat testing
        indexCompatVersions = versions
        wireCompatVersions = versions.subList(firstMajorIndex, versions.size())
        buildMetadata = buildMetadataMap
      }
    }
    ```
4. 导入idea 
5. 启动ES  
    入口类：core/src/main/java/org/elasticsearch/bootstrap/Elasticsearch.java 
6. 报错  **java.lang.IllegalStateException: path.home is not configured**   
   需要加上path.home，可以直接修改虚拟机的运行参数，在idea的Run菜单下点击Edit Configuration选项，配置VM参数为： 
   -Des.path.home=D:\dev\elasticsearch-5.6.4    //es发行版的根目录 
7. 报错  Could not register mbeans java.security.AccessControlException: access denied ("javax.management.MBeanTrustPermission" "register")  
   解决方法，增加VM options： -Dlog4j2.disable.jmx=true
8. 报错 java.lang.StringIndexOutOfBoundsException: String index out of range: -1   
   解决方法：core模块中 org/elasticsearch/Build.java 文件 第72行  
   isSnapshot = true;  改为  isSnapshot = **false**; 
9. 报错 
    fatal error in thread [main], exiting java.lang.ExceptionInInitializerError: null
    Caused by: java.security.AccessControlException: access denied ("java.lang.RuntimePermission" "accessDeclaredMembers")   
    解决方法：    
    在jdk的jre下，例如D:\Java\jdk1.8.0_171\jre\lib\security\java.policy   
    在java.policy文件末尾加上下面几行：   
    ```
    permission java.lang.RuntimePermission "createClassLoader"; 
    permission java.lang.RuntimePermission "getClassLoader"; 
    permission java.lang.RuntimePermission "accessDeclaredMembers";
    ```
    
10. 再启动，启动成功。 
11. 浏览器中访问 http://localhost:9200/ ，返回结果
    ```
    {
      "name" : "eDtfxX7",
      "cluster_name" : "elasticsearch",
      "cluster_uuid" : "AYw0Bs4fRamQQa0Lrwz57Q",
      "version" : {
        "number" : "5.6.4",
        "build_hash" : "Unknown",
        "build_date" : "Unknown",
        "build_snapshot" : false,
        "lucene_version" : "6.6.1"
      },
      "tagline" : "You Know, for Search"
    }
    ```
    
    