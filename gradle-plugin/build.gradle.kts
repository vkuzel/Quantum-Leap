plugins {
    java
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

val springBootVersion = "2.7.7"
val dependencyManagementVersion = "1.0.15.RELEASE"
val jooqVersion = "3.14.16"
val postgreSqlVersion = "42.3.8"
val junitVersion = "5.8.2"
val mockitoVersion = "4.5.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-loader-tools:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$dependencyManagementVersion")
    implementation(":gradle-project-dependency")
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.jooq:jooq-codegen:$jooqVersion")
    implementation("org.postgresql:postgresql:$postgreSqlVersion")
    implementation("javax.xml.bind:jaxb-api:2.2.12")
    implementation("com.sun.xml.bind:jaxb-core:2.2.11")
    implementation("com.sun.xml.bind:jaxb-impl:2.2.11")
    implementation("javax.activation:javax.activation-api:1.2.0")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
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
