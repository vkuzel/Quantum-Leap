package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionUtilsTest {

    @Test
    void getClassFieldValueShouldReturnValueOfPrivateField() {
        TestClass testClass = new TestClass("test-value");

        Object value = ReflectionUtils.getClassFieldValue(TestClass.class, testClass, "value");

        assertEquals("test-value", value);
    }

    @Test
    void invokeClassMethodShouldInvokePrivateMethod() {
        TestClass testClass = new TestClass("test-value");

        Object value = ReflectionUtils.invokeClassMethod(TestClass.class, testClass, "getValue");

        assertEquals("test-value", value);
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
