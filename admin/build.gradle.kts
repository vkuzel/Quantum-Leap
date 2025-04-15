repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":quantumleap:core"))

    implementation("org.webjars:bootstrap:5.3.5")
    implementation("org.webjars:font-awesome:5.15.4")
    implementation("org.webjars.bower:chart.js:2.9.4")

    testImplementation(project(":quantumleap:coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
