#### 1. 新建maven项目, extend_stemmer_override_filter 
#### 2. pom.xml文件中加入build配置：
```
    <build>
        <finalName>extend_stemmer_override_filter</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.3.2</version>
                <configuration>
                    <source>${maven.compiler.target}</source>
                    <target>${maven.compiler.target}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.11</version>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                    </includes>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <outputDirectory>${project.build.directory}/releases/</outputDirectory>
                    <descriptors>
                        <descriptor>${basedir}/src/main/assemblies/plugin.xml</descriptor>
                    </descriptors>
                    <archive>
                        <manifest>
                            <mainClass>fully.qualified.MainClass</mainClass>
                        </manifest>
                    </archive>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

#### 3. src/main/assemblies/plugin.xml 文件 
```
<?xml version="1.0"?>
<assembly>
    <id>-</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <files>
        <file>
            <source>${project.basedir}/src/main/resources/plugin-descriptor.properties</source>
            <outputDirectory></outputDirectory>
            <filtered>true</filtered>
        </file>
    </files>
    <dependencySets>
        <dependencySet>
            <outputDirectory>/</outputDirectory>
            <useProjectArtifact>true</useProjectArtifact>
            <useTransitiveFiltering>true</useTransitiveFiltering>
            <excludes>
                <exclude>org.elasticsearch:elasticsearch</exclude>
            </excludes>
        </dependencySet>
        <dependencySet>
            <outputDirectory>/</outputDirectory>
            <useProjectArtifact>true</useProjectArtifact>
            <useTransitiveFiltering>true</useTransitiveFiltering>
            <includes>
                <include>org.apache.httpcomponents:httpclient</include>
            </includes>
        </dependencySet>
    </dependencySets>
</assembly>
```

#### 4. src/main/resources/plugin-descriptor.properties 文件
```
 description=${project.description}
 version=${project.version}
 name=dynamic_stemmer_override_filter
 site=${elasticsearch.plugin.site}
 jvm=true
 classname=com.globalegrow.esearch.plugin.esof.ExtendStemmerOverrideFilterPlugin
 java.version=${maven.compiler.target}
 elasticsearch.version=${versions.es}
 isolated=${elasticsearch.plugin.isolated}
```

#### 5. 编写代码
ExtendStemmerOverrideFilterPlugin

#### 6. maven打包
```
mvn clean install -Dmaven.test.skip=true  
```

#### 7. 插件打包后在release文件夹下