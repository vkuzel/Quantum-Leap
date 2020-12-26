package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionUtilsTest {

    @Test
    void getClassFieldValueShouldReturnValueOfPrivateField() {
        TestClass testClass = new TestClass("test-value");

        Object value = ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void getClassFieldShouldReturnValueOfChildClassPrivateField() {
        ParentTestClass parentTestClass = new ParentTestClass("test-value");

        Object value = ReflectionUtils.getClassFieldValue(ParentTestClass.class, parentTestClass, "value");

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
    void invokeClassMethodShouldInvokeChildClassPrivateMethod() {
        ParentTestClass parentTestClass = new ParentTestClass("test-value");

        Object value = ReflectionUtils.invokeClassMethod(ParentTestClass.class, parentTestClass, "getValue");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldThrowExceptionForNonExistingMethod() {
        TestClass testClass = new TestClass("test-value");

        assertThrows(IllegalStateException.class, () ->
                ReflectionUtils.invokeClassMethod(TestClass.class, testClass, "nonExisting"));
    }

    private static class ParentTestClass extends TestClass {

        public ParentTestClass(String value) {
            super(value);
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
