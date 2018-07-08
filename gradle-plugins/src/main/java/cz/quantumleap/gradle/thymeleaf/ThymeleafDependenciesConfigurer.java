package cz.quantumleap.gradle.thymeleaf;

import cz.quantumleap.gradle.project.RootProject;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.Project;

public class ThymeleafDependenciesConfigurer {

    private static final String THYMELEAF_VERSION_PROPERTY_NAME = "thymeleaf.version";
    private static final String THYMELEAF_VERSION = "3.0.8.RELEASE";
    private static final String THYMELEAF_LAYOUT_DIALECT_VERSION_PROPERTY_NAME = "thymeleaf-layout-dialect.version";
    private static final String THYMELEAF_LAYOUT_DIALECT_VERSION = "2.2.2";
    private static final String THYMELEAF_EXTRAS_SPRINGSECURITY4_VERSION_PROPERTY_NAME = "thymeleaf-extras-springsecurity4.version";
    private static final String THYMELEAF_EXTRAS_SPRINGSECURITY4_VERSION = "3.0.2.RELEASE";
    private static final String THYMELEAF_EXTRAS_JAVA8TIME_VERSION_PROPERTY_NAME = "thymeleaf-extras-java8time.version";
    private static final String THYMELEAF_EXTRAS_JAVA8TIME_VERSION = "3.0.1.RELEASE";

    public void configure(RootProject rootProject) {
        Project project = rootProject.getProject();
        ProjectUtils.setExtraProperty(project, THYMELEAF_VERSION_PROPERTY_NAME, THYMELEAF_VERSION);
        ProjectUtils.setExtraProperty(project, THYMELEAF_LAYOUT_DIALECT_VERSION_PROPERTY_NAME, THYMELEAF_LAYOUT_DIALECT_VERSION);
        ProjectUtils.setExtraProperty(project, THYMELEAF_EXTRAS_SPRINGSECURITY4_VERSION_PROPERTY_NAME, THYMELEAF_EXTRAS_SPRINGSECURITY4_VERSION);
        ProjectUtils.setExtraProperty(project, THYMELEAF_EXTRAS_JAVA8TIME_VERSION_PROPERTY_NAME, THYMELEAF_EXTRAS_JAVA8TIME_VERSION);
    }

}
