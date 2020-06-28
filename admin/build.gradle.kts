dependencies {
    implementation(project(":core"))

    implementation("org.webjars:bootstrap:4.5.0") {
        exclude(group = "org.webjars", module = "jquery")
    }
    implementation("org.webjars:font-awesome:5.8.1")
    implementation("org.webjars:jquery:3.5.1")
    implementation("org.webjars.bower:chart.js:2.7.3")

    testImplementation(project(":coretest"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
