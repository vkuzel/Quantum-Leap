dependencies {
    compile(project(":core"))

    implementation("org.webjars:bootstrap:4.3.1") {
        exclude(group = "org.webjars", module = "jquery")
    }
    implementation("org.webjars:font-awesome:5.8.1")
    implementation("org.webjars:jquery:3.3.1-2")
    implementation("org.webjars.bower:chart.js:2.7.3")

    testImplementation(project(path = ":core", configuration = "testFixturesUsageCompile"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
