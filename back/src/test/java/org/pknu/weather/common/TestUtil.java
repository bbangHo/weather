package org.pknu.weather.common;

import java.lang.reflect.Field;

public class TestUtil {

    public static void entitySetFiled(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field value", e);
        }
    }
}
