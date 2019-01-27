ext {
    set("mainClassName", "cz.quantumleap.QuantumLeapApplication")
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-jooq")
    compile("org.springframework.data:spring-data-commons")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-thymeleaf")
    compile("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.security:spring-security-oauth2-client")
    compile("org.springframework.security:spring-security-oauth2-jose")
    compile("org.springframework.session:spring-session-core")
    compile("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    compile("org.thymeleaf.extras:thymeleaf-extras-java8time")

    compile("com.google.guava:guava:24.1-jre")
    compile("org.apache.commons:commons-lang3:3.7")
    compile("commons-io:commons-io:2.6")
    compile("com.ibm.icu:icu4j:57.1")
    compile("org.jetbrains:annotations:15.0")
    compile("eu.bitwalker:UserAgentUtils:1.21")

    runtimeOnly("org.postgresql:postgresql")

    testFixturesCompile("org.springframework.boot:spring-boot-starter-test")
    testFixturesCompile("org.springframework.security:spring-security-test")
}
