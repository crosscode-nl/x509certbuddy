plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.0"
}



group = "nl.crosscode"
version = "0.8.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<Test>{
        useJUnitPlatform()
        // Work around. Gradle does not include enough information to disambiguate
        // between different examples and scenarios.
        systemProperty("cucumber.junit-platform.naming-strategy", "long")
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("233.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("CROSSCODE_INTELLIJ_PUBLISH_TOKEN"))
    }
}

dependencies {

    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation(platform("io.cucumber:cucumber-bom:7.9.0"))

    testImplementation("org.picocontainer:picocontainer:2.15")
    testImplementation("io.cucumber:cucumber-picocontainer:7.9.0");

    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.junit.jupiter:junit-jupiter")

}