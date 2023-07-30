repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":quantumleap:core"))

    implementation(files("lib/bootstrap-5.3.0-alpha3.jar"))
    implementation("org.webjars:font-awesome:5.8.1")
    implementation("org.webjars.bower:chart.js:2.7.3")

    testImplementation(project(":quantumleap:coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
