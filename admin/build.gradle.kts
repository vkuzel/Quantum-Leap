dependencies {
    compile(project(":core"))

    compile("org.webjars.npm:startbootstrap-sb-admin:4.0.0") {
        exclude(group = "org.webjars.npm", module = "datatables.net-bs4")
    }

    testCompile(project(path = ":core", configuration = "testFixturesUsageCompile"))
    testCompile("org.springframework.boot:spring-boot-starter-test")
}
