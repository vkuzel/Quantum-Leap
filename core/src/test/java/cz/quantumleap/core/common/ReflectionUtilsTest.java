package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    @Test
    void getClassFieldsShouldReturnNonStaticFieldsOfClassAndItsSuperclasses() {
        List<Field> fields = ReflectionUtils.getClassFields(ChildTestClass.class);

        List<String> fieldNames = fields.stream().map(Field::getName).collect(Collectors.toList());
        assertEquals(2, fieldNames.size());
        assertTrue(fieldNames.contains("value"));
        assertTrue(fieldNames.contains("childValue"));
    }

    @Test
    void getClassFieldValueShouldReturnValueOfPrivateField() {
        TestClass testClass = new TestClass("test-value");

        Object value = ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void getClassFieldShouldReturnValueOfParentClassPrivateField() {
        ChildTestClass childTestClass = new ChildTestClass("test-value", "child-value");

        Object value = ReflectionUtils.getClassFieldValue(ChildTestClass.class, childTestClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void getClassFieldShoutTrowExceptionForNotExistingField() {
        TestClass testClass = new TestClass("test-value");

        assertThrows(IllegalStateException.class, () ->
                ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "nonExisting"));
    }

    @Test
    void invokeClassMethodShouldInvokePrivateMethod() {
        TestClass testClass = new TestClass("test-value");

        Object value = ReflectionUtils.invokeClassMethod(TestClass.class, testClass, "getValue");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldInvokeParentClassPrivateMethod() {
        ChildTestClass childTestClass = new ChildTestClass("test-value", "child-value");

        Object value = ReflectionUtils.invokeClassMethod(ChildTestClass.class, childTestClass, "getValue");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldThrowExceptionForNonExistingMethod() {
        TestClass testClass = new TestClass("test-value");

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
