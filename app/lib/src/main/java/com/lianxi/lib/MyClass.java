package com.lianxi.lib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class MyClass {
    @MyAnnotation(address = "南京")
    void Test() {

    }

    @MyAnnotation2("q")
    void Test2(){

    }
}

//表示使用的区域
@Target(value = {ElementType.METHOD, ElementType.TYPE})
//表示在什么地方有效
@Retention(value = RetentionPolicy.RUNTIME)
//JAVAdom中有没有效果
//@Documented
//子类是否可以继承父类注解
//@Inherited
@interface MyAnnotation {
    String name() default "wang";
    int age() default 23;
    String address();
}

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@interface MyAnnotation2{
    String value();
}