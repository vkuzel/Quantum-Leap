package cz.quantumleap.server.admin.menu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AdminMenuItemDefinition {

    String title();

    String fontAwesomeIcon() default "";

}
