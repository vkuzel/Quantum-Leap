dependencies {
    implementation(project(":core"))

    implementation("org.webjars:bootstrap:5.1.3")
    implementation("org.webjars:font-awesome:5.8.1")
    implementation("org.webjars.bower:chart.js:2.7.3")

    testImplementation(project(":coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
