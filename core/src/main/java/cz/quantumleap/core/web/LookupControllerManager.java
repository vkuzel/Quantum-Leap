package cz.quantumleap.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnWebApplication
public class LookupControllerManager {

    private final List<LookupController> lookupControllers;

    public LookupControllerManager(@Autowired(required = false) List<LookupController> lookupControllers) {
        this.lookupControllers = lookupControllers;
    }

    public LookupController getControllerForTable(String databaseTableNameWithSchema) {
        // At this point it would be better to have controllers pre-loaded in
        // a map. But controller can be protected by Spring Security which
        // could prevent us from calling supportedDatabaseTableNameWithSchema()
        // method on context init.
        return lookupControllers.stream()
                .filter(controller -> hasSupportForTable(controller, databaseTableNameWithSchema))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No accessible lookup controller with support of " + databaseTableNameWithSchema + " was not found!"));
    }

    private boolean hasSupportForTable(LookupController controller, String tableName) {
        return controller.supportedDatabaseTableNameWithSchema().equals(tableName);
    }
}

