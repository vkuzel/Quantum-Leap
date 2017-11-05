package cz.quantumleap.admin.menu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AdminMenuItemDefinition {

    String title();

    /**
     * Identify parent menu item by its title. This may change in future.
     */
    String parentByTitle() default "";

    String fontAwesomeIcon() default "";

    int priority() default 0;
}
