package cz.quantumleap.server.dashboard.menu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DashboardMenuItemDefinition {

    String title();

    String fontAwesomeIcon() default "";

}
