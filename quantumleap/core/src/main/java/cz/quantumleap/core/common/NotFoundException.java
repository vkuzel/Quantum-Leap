package cz.quantumleap.core.common;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Object id) {
        super("Entity " + id + " not found!");
    }

}
