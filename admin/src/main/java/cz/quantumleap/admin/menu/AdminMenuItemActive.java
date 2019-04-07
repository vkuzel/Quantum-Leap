package cz.quantumleap.admin.menu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AdminMenuItemActive {

    /**
     * Identify parent menu item by its title. This may change in future.
     */
    String value();
}
