package com.uroad.dubai.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类转换初始化
 */
public class ClassUtil {
    public static <T> T getT(Object o, int i) {
        try {
            @SuppressWarnings("rawtypes")
            Class clazz = o.getClass();
            //getSuperclass()获得该类的父类
//            System.out.println(clazz.getSuperclass());
            //getGenericSuperclass()获得带有泛型的父类
            //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
            Type type = clazz.getGenericSuperclass();
//            System.out.println(type);
            //ParameterizedType参数化类型，即泛型
            ParameterizedType p = (ParameterizedType) type;
            //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
            Class<T> c = (Class<T>) p.getActualTypeArguments()[i];
            return c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
