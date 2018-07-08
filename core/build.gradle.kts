ext {
    set("mainClassName", "cz.quantumleap.QuantumLeapApplication")
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-jooq")
    compile("org.springframework.data:spring-data-commons")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-thymeleaf")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.security.oauth:spring-security-oauth2")
    compile("org.thymeleaf.extras:thymeleaf-extras-springsecurity4")
    compile("org.thymeleaf.extras:thymeleaf-extras-java8time")

    compile("com.google.guava:guava:24.1-jre")
    compile("org.apache.commons:commons-lang3:3.7")
    compile("commons-io:commons-io:2.6")
    compile("com.ibm.icu:icu4j:57.1")
    compile("org.jetbrains:annotations:15.0")

    runtime("org.postgresql:postgresql")

    testFixturesCompile("org.springframework.boot:spring-boot-starter-test")
    testFixturesCompile("org.springframework.security:spring-security-test")
}
