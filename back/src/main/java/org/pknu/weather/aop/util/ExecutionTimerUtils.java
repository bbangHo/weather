package org.pknu.weather.aop.util;


public class ExecutionTimerUtils {
    public static long start() {
        return System.currentTimeMillis();
    }

    public static long end(long startTime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
