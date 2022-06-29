![](https://raw.githubusercontent.com/ssiyy/tenseiga/main/imgs/Sesshomaru_and_Tenseiga.webp)

[![](https://jitpack.io/v/ssiyy/tenseiga.svg)](https://jitpack.io/#ssiyy/tenseiga)
# Tenseiga(天生牙)
一个用来支持Android AOP开发的工具

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
        classpath "com.github.ssiyy.tenseiga:tenseiga-plugin:v1.0.0"
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
  implementation "com.github.ssiyy.tenseiga:tenseiga:v1.0.0"
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
implementation "com.github.ssiyy.tenseiga:tenseiga:v1.0.0"
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

## 鸣谢
- [lancet](https://github.com/eleme/lancet) 
- [booster](https://github.com/didi/booster)
- [DoKit](https://github.com/didi/DoKit)


