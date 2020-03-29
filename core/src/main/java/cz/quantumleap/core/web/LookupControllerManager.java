package cz.quantumleap.core.web;

import cz.quantumleap.core.data.entity.EntityIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@ConditionalOnWebApplication
public class LookupControllerManager {

    private final List<LookupController> lookupControllers;

    public LookupControllerManager(@Autowired(required = false) List<LookupController> lookupControllers) {
        this.lookupControllers = lookupControllers;
    }

    public LookupController getControllerForEntityIdentifier(String text) {
        return getControllerForEntityIdentifier(EntityIdentifier.parse(text));
    }

    public LookupController getControllerForEntityIdentifier(EntityIdentifier entityIdentifier) {
        // At this point it would be better to have controllers pre-loaded in
        // a map. But controller can be protected by Spring Security which
        // could prevent us from calling getControllerForEntityIdentifier()
        // method on context init.
        for (LookupController lookupController : lookupControllers) {
            EntityIdentifier lookupEntityIdentifier = lookupController.getEntityIdentifier();
            if (Objects.equals(lookupEntityIdentifier, entityIdentifier)) {
                return lookupController;
            }
        }
        throw new IllegalArgumentException("No accessible lookup controller with support of " + entityIdentifier + " was not found!");
    }
}

