![](https://raw.githubusercontent.com/ssiyy/tenseiga/main/imgs/Sesshomaru_and_Tenseiga.webp)

[![](https://jitpack.io/v/ssiyy/tenseiga.svg)](https://jitpack.io/#ssiyy/tenseiga)
# Tenseiga(天生牙)
一个用来支持Android AOP开发的工具

Tenseiga采用ASM 7.0开发，具体ASM和java版本支持如下
| ASM Release | Release Date | Java Support |
|:--:|:--:|:--:|
|2.0	|2005-05-17	|Java 5 language support|
|3.2	|2009-06-11	|support for the new invokedynamic code.|
|4.0	|2011-10-29	|Java 7 language support|
|5.0	|2014-03-16	|Java 8 language support|
|6.0	|2017-09-23	|Java 9 language support|
|6.1	|2018-03-11	|Java 10 language support|
|7.0	|2018-10-27	|Java 11 language support|
|7.1	|2019-03-03	|Java 13 language support|
|8.0	|2020-03-28	|Java 14 language support|
|9.0	|2020-09-22	|Java 16 language support|
|9.1	|2021-02-06	|JDK 17 support|
|9.2	|2021-06-26	|JDK 18 support|

- JDK (`1.8` is recommended)
- Gradle version `4.1+`
- Android Gradle Plugin version `3.0+`

| Android Gradle Plugin |  Gradle  |
|:---------------------:|:--------:|
| 3.0.0+                | 4.1+     |
| 3.1.0+                | 4.4+     |
| 3.2.0 - 3.2.1         | 4.6+     |
| 3.3.0 - 3.3.3         | 4.10.1+  |
| 3.4.0 - 3.4.3         | 5.1.1+   |
| 3.5.0 - 3.5.4         | 5.4.1+   |
| 3.6.0 - 3.6.4         | 5.6.4+   |
| 4.0.0+                | 6.1.1+   |
| 4.1.0+                | 6.5+     |
| 4.2.0+                | 6.7.1+   |
| 7.0                   | 7.0+     |
| 7.1                   | 7.1+     |
| 7.2                   | 7.3.3+   |

是的，支持最新的AGP 7.0+

## 导入方式
### 将JitPack存储库添加到您的构建文件中(项目根目录下build.gradle文件)
```
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### 添加依赖
```
buildscript {
    dependencies {
        classpath "com.github.ssiyy.tenseiga:tenseiga-plugin:lastVersion"
    }
}
```
在工程Module的构建文件中(build.gradle)
```
apply plugin: "com.siy.tenseiga"
```
### 使用
#### 使用方式1：gradle脚本配置
```
TExtension {
    proxys {
        //代理用户方法
        proxy {
            targetClass = "com.siy.tenseiga.test.OriginJava"
            hookMethod = "hookProxy"
            hookClass = "com.siy.tenseiga.test.HookJava"
            filter "com.siy.tenseiga.*"
        }

        //代理系统方法
        getString {
            targetClass = "android.content.Context"
            hookMethod = "hookProxySys"
            hookClass = "com.siy.tenseiga.test.HookJava"
            filter "com.siy.tenseiga.test.*", "com.siy.tenseiga.MainActivity"
        }

    }

    //修改用户方法
    replaces {
        replace {
            targetClass = "com.siy.tenseiga.test.OriginJava"
            hookMethod = "hookReplace"
            hookClass = "com.siy.tenseiga.test.HookJava"
        }
    }
}
```
以
```
proxys {
   proxy {
      targetClass = "com.siy.tenseiga.test.OriginJava"
      hookMethod = "hookProxy"
      hookClass = "com.siy.tenseiga.test.HookJava"
      filter "com.siy.tenseiga.*"
    }
}
```
为例做简单解释

proxys 下面的配置对代理织入方法生效

proxy 想要hook的目标方法

targetClass 想要hook的目标方法所在的类

hookMethod 自己编写的hook方法

hookClass 自己编写的hook方法所在的类

ps:

replaces 配置对替换织入方法生效

### 使用方式2：注解方式
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
```
dependencies {
  implementation "com.github.ssiyy.tenseiga:tenseiga:lastVersion"
}
```
以
```
  @Replace(value = "annoReplace")
  @TargetClass(value = "com.siy.tenseiga.test.OriginJava")
  public String hookAnnoReplace(int a, Integer b) {
      Log.e("siy", "OriginJava-hookAnnoReplace-");
      return a + b + "hook";
  }
```
为例子做简单解释
@Replace 表示当前方法是以替换的方法织入,值是织入的方法名字

@TargetClass 表示织入方法所在的类

ps:
类似的还有

@Proxy 表示当前方法以代理的方式织入，值是织入的方法名字

@Filter 用来过滤代理织入方法的范围

###  提供额外操作方式
想要使用提供的额外操作方式同样需要引入
```
implementation "com.github.ssiyy.tenseiga:tenseiga:lastVersion"
```
如：
```
   public int hookProxy(int a, Integer b, String str, View view, Context context, byte bbb, short sh) {
        Log.e("siy", "HookJava-hookProxy-");

        //获取实例方法所在的对象
        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

        int total = (int) Invoker.invoke(1, 3, "hah", new View(App.INSTANCE), null, 1, 2);
        return total - b;
    }
```
Invoker.invoke(...)   用来调用待用返回值的原方法

Invoker.invokeVoid(...) 用来调用不带返回值的原方法

Self.get()  获取hook的实例方法所在对象，注意hook的静态方法获取不到对象

Self.getField(fieldName) 获取hook的实例对象中的字段

Self.putField(fieldValue,fieldName) 向hook的实例对象添加新的字段

###### 如果对上述解释有疑问，各位同学可以下载代码进一步了解

## 鸣谢
- [lancet](https://github.com/eleme/lancet) 
- [booster](https://github.com/didi/booster)
- [DoKit](https://github.com/didi/DoKit)


