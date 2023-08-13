package cz.quantumleap.core.business.validation;

import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static java.util.Collections.emptyList;

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
        for (var error : errors.getAllErrors()) {
            throw new IllegalArgumentException(error.getCode());
        }
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return emptyList();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return emptyList();
    }

    @Override
    public Object getFieldValue(String field) {
        throw new UnsupportedOperationException(field);
    }
}
