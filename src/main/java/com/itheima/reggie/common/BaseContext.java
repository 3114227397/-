package com.itheima.reggie.common;

public class BaseContext{
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();//这个线程对象的id。只有这么一个
    }

}
