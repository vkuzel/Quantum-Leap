plugins {
    java
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

val springBootVersion = "3.3.3"
val dependencyManagementVersion = "1.1.6"
val jooqVersion = "3.19.11"
val postgreSqlVersion = "42.7.3"
val junitVersion = "5.10.3"
val mockitoVersion = "5.11.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-loader-tools:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$dependencyManagementVersion")
    implementation(":gradle-project-dependency")
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.jooq:jooq-codegen:$jooqVersion")
    implementation("org.postgresql:postgresql:$postgreSqlVersion")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-core:4.0.1")
    implementation("com.sun.xml.bind:jaxb-impl:4.0.1")
    implementation("javax.activation:javax.activation-api:1.2.0")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    plugins {
        create("QuantumLeapPlugin") {
            id = "com.github.vkuzel.Quantum-Leap-Gradle-Plugin"
            implementationClass = "cz.quantumleap.gradle.QuantumLeapPlugin"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
