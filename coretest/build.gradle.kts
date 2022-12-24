repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.security:spring-security-test")
}
