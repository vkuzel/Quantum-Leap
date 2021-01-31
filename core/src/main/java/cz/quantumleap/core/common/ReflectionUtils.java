package cz.quantumleap.core.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

final public class ReflectionUtils {

    /**
     * Returns declared fields on a class or its superclasses. Does not return
     * static fields.
     */
    public static List<Field> getClassFields(Class<?> type) {
        try {
            Field[] fields = type.getDeclaredFields();
            return Arrays.asList(fields);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns a value of a field which may be private or declared on a
     * superclass.
     */
    @SuppressWarnings("unused")
    public static Object getClassFieldValue(Class<?> type, Object instance, String fieldName) {
        try {
            Field field = type.getDeclaredField(fieldName);
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
