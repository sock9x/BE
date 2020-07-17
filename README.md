1. Install JDK8 from oracle site.
2. 
check java version: 
```java -version```
the output will be like this:

```$xslt
openjdk version "1.8.0_222"
OpenJDK Runtime Environment (build 1.8.0_222-8u222-b10-1ubuntu1~16.04.1-b10)
OpenJDK 64-Bit Server VM (build 25.222-b10, mixed mode)
```

build jar file: `./gradlew build -x test`

run jar file:
 
```java -jar /build/libs/*.jar```

Access to swagger: http://localhost:8080/swagger-ui.html