plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

group = "cz.quantumleap"

val springBootVersion: String = "1.5.8.RELEASE"
val gradleProjectDependenciesVersion: String = "3.0.0"
val jooqVersion: String = "3.9.6"
val postgreSqlVersion: String = "9.4.1212.jre7"


dependencies {
    add("compile", gradleApi())

    add("compile", "org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    add("compile", "com.github.vkuzel:Gradle-Project-Dependencies:$gradleProjectDependenciesVersion")
    add("compile", "org.jooq:jooq:$jooqVersion")
    add("compile", "org.jooq:jooq-meta:$jooqVersion")
    add("compile", "org.jooq:jooq-codegen:$jooqVersion")
    add("compile", "org.postgresql:postgresql:$postgreSqlVersion")

    add("testCompile", "junit:junit:4.12")
    add("testCompile", "org.mockito:mockito-core:1.10.19")
}