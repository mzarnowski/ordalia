plugins {
    id("java")
}

group = "dev.mzarnowski.wikimedia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // https://mvnrepository.com/artifact/com.launchdarkly/okhttp-eventsource
    implementation("com.launchdarkly:okhttp-eventsource:4.1.0")
}

tasks.test {
    useJUnitPlatform()
}