ext {
    set("mainClass", "cz.quantumleap.QuantumLeapApplication")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-jooq")
    api("org.springframework.data:spring-data-commons")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.security:spring-security-oauth2-client")
    api("org.springframework.security:spring-security-oauth2-jose")
    api("org.springframework.session:spring-session-core")
    api("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    api("org.thymeleaf.extras:thymeleaf-extras-java8time")

    api("com.google.guava:guava:29.0-jre")
    api("org.apache.commons:commons-lang3:3.11")
    api("commons-io:commons-io:2.8.0")
    api("com.ibm.icu:icu4j:67.1")
    api("org.jetbrains:annotations:20.1.0")
    api("eu.bitwalker:UserAgentUtils:1.21")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation(project(":coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.postgresql:postgresql")
}
