repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":quantumleap:core"))

    implementation("org.webjars:bootstrap:5.3.3")
    implementation("org.webjars:font-awesome:5.8.1")
    implementation("org.webjars.bower:chart.js:2.7.3")

    testImplementation(project(":quantumleap:coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
