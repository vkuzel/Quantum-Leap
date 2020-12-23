package cz.quantumleap.core.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final public class ReflectionUtils {

    @SuppressWarnings("unused")
    public static Object getClassFieldValue(Class<?> type, Object instance, String fieldName) {
        try {
            var field = type.getDeclaredField(fieldName);
            boolean accessible = field.canAccess(instance);
            try {
                if (!accessible) {
                    field.setAccessible(true);
                }
                return field.get(instance);
            } finally {
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            String format = "Cannot get class field value from %s.%s";
            String msg = String.format(format, type.getName(), fieldName);
            throw new IllegalStateException(msg, e);
        }
    }

    @SuppressWarnings("unused")
    public static Object invokeClassMethod(Class<?> type, Object instance, String methodName) {
        try {
            Method method = type.getDeclaredMethod(methodName);
            boolean accessible = method.canAccess(instance);
            try {
                if (!accessible) {
                    method.setAccessible(true);
                }
                return method.invoke(instance);
            } finally {
                if (!accessible) {
                    method.setAccessible(false);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String format = "Cannot invoke class method %s.%s";
            String msg = String.format(format, type.getName(), methodName);
            throw new IllegalStateException(msg, e);
        }
    }
}
