package cz.quantumleap.core.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final public class ReflectionUtils {

    /**
     * Returns a value of a field which may be private or declared on a
     * superclass.
     */
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
        } catch (NoSuchFieldException e) {
            if (type.getSuperclass() != null) {
                return getClassFieldValue(type.getSuperclass(), instance, fieldName);
            }
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            String format = "Cannot get class field value from %s.%s";
            String msg = String.format(format, type.getName(), fieldName);
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Invokes a method, which may be private or declared on a superclass.
     */
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
        } catch (NoSuchMethodException e) {
            if (type.getSuperclass() != null) {
                return invokeClassMethod(type.getSuperclass(), instance, methodName);
            }
            throw new IllegalStateException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String format = "Cannot invoke class method %s.%s";
            String msg = String.format(format, type.getName(), methodName);
            throw new IllegalStateException(msg, e);
        }
    }
}
