package cz.quantumleap.core.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final public class ReflectionUtils {

    /**
     * Returns declared fields on a class or its superclasses. Does not return
     * static fields.
     */
    public static List<Field> getClassFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        try {
            var fieldArray = type.getDeclaredFields();
            fields.addAll(Arrays.asList(fieldArray));
            if (type.getSuperclass() != null) {
                var superClassFields = getClassFields(type.getSuperclass());
                fields.addAll(superClassFields);
            }
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        }
        return fields;
    }

    /**
     * Returns a value of a field which may be private or declared on a
     * superclass.
     */
    public static Object getClassFieldValue(Class<?> type, Object instance, String fieldName) {
        try {
            var field = type.getDeclaredField(fieldName);
            var accessible = field.canAccess(instance);
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
            throw createInaccessibleMemberException(type, "field", fieldName, e);
        } catch (IllegalAccessException e) {
            throw createInaccessibleMemberException(type, "field", fieldName, e);
        }
    }

    /**
     * Invokes a method, which may be private or declared on a superclass.
     */
    public static Object invokeClassMethod(Class<?> type, Object instance, String methodName) {
        try {
            var method = type.getDeclaredMethod(methodName);
            var accessible = method.canAccess(instance);
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
            throw createInaccessibleMemberException(type, "method", methodName, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw createInaccessibleMemberException(type, "method", methodName, e);
        }
    }

    private static IllegalStateException createInaccessibleMemberException(
            Class<?> type,
            String memberType,
            String memberName,
            Throwable e
    ) throws IllegalStateException {
        var format = "Cannot get class %s value from %s.%s";
        var msg = String.format(format, type.getName(), memberType, memberName);
        return new IllegalStateException(msg, e);
    }
}
