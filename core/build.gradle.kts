ext {
    set("mainClass", "cz.quantumleap.QuantumLeapApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    api(":gradle-project-dependency")
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
    api("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    api("org.apache.commons:commons-lang3:3.17.0")
    api("commons-io:commons-io:2.19.0")
    api("com.ibm.icu:icu4j:77.1")
    api("org.jetbrains:annotations:26.0.2")
    api("eu.bitwalker:UserAgentUtils:1.21")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation(project(":quantumleap:coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.postgresql:postgresql")
}
