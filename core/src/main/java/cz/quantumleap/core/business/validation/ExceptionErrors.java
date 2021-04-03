package cz.quantumleap.core.business.validation;

import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ExceptionErrors extends AbstractErrors {

    @Override
    public String getObjectName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
        throw new IllegalArgumentException(errorCode);
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
        throw new IllegalArgumentException(field + ": " + errorCode);
    }

    @Override
    public void addAllErrors(Errors errors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getFieldValue(String field) {
        throw new UnsupportedOperationException();
    }
}
