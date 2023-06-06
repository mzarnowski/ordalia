plugins {
    java
}

group = "dev.mzarnowski.wikimedia"
version = "1.0-SNAPSHOT"



subprojects {
    apply(plugin = "org.gradle.java")

    repositories {
        mavenCentral()
    }

    dependencies{
        // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
        implementation("org.apache.kafka:kafka-clients:3.4.0")
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("org.slf4j:slf4j-simple:2.0.7")

        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}