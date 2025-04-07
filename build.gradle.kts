plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "eu.devload"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.github.twitch4j:twitch4j:1.23.0")
    implementation("org.json:json:20250107")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "eu.devload.twitch.TQusic"
    }
}

tasks.test {
    useJUnitPlatform()
}