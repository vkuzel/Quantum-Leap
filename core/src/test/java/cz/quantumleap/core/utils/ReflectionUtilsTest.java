package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    @Test
    void getClassFieldsShouldReturnNonStaticFieldsOfClassAndItsSuperclasses() {
        var fields = ReflectionUtils.getClassFields(ChildTestClass.class);

        var fieldNames = fields.stream().map(Field::getName).collect(Collectors.toList());
        assertEquals(2, fieldNames.size());
        assertTrue(fieldNames.contains("value"));
        assertTrue(fieldNames.contains("childValue"));
    }

    @Test
    void getClassFieldValueShouldReturnValueOfPrivateField() {
        var testClass = new TestClass("test-value");

        var value = ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void getClassFieldShouldReturnValueOfParentClassPrivateField() {
        var childTestClass = new ChildTestClass("test-value", "child-value");

        var value = ReflectionUtils.getClassFieldValue(ChildTestClass.class, childTestClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void getClassFieldShouldTrowExceptionForNotExistingField() {
        var testClass = new TestClass("test-value");

        assertThrows(IllegalStateException.class, () ->
                ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "nonExisting"));
    }

    @Test
    void invokeClassMethodShouldInvokePrivateMethod() {
        var testClass = new TestClass("test-value");

        var value = ReflectionUtils.invokeClassMethod(TestClass.class, testClass, "getValue");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldInvokeParentClassPrivateMethod() {
        var childTestClass = new ChildTestClass("test-value", "child-value");

        var value = ReflectionUtils.invokeClassMethod(ChildTestClass.class, childTestClass, "getValue");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldThrowExceptionForNonExistingMethod() {
        var testClass = new TestClass("test-value");

        assertThrows(IllegalStateException.class, () ->
                ReflectionUtils.invokeClassMethod(TestClass.class, testClass, "nonExisting"));
    }

    private static class ChildTestClass extends TestClass {

        private final String childValue;

        public ChildTestClass(String value, String childValue) {
            super(value);
            this.childValue = childValue;
        }

        @SuppressWarnings("unused")
        public String getChildValue() {
            return childValue;
        }
    }

    private static class TestClass {

        private final String value;

        public TestClass(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        private String getValue() {
            return value;
        }
    }
}
