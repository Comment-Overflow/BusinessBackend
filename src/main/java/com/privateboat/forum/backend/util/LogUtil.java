package com.privateboat.forum.backend.util;

public class LogUtil {
    public static void debug(Object e){
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        System.out.println("DEBUG\t" + fullClassName + "." + methodName + "():" + lineNumber + "\t" + e.toString());
    }
}
