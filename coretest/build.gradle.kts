repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":quantumleap:core"))
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.security:spring-security-test")
}
