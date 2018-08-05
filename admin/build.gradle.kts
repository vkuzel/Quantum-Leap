dependencies {
    compile(project(":core"))

    implementation("org.webjars.npm:startbootstrap-sb-admin:4.0.0") {
        exclude(group = "org.webjars.npm", module = "datatables.net-bs4")
    }

    testImplementation(project(path = ":core", configuration = "testFixturesUsageCompile"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
