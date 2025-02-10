import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import java.util.regex.Pattern.compile


plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("io.spring.dependency-management") version "1.0.1.RELEASE"
  id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "com.octavio"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.12"
val vertxWebVersion = "4.5.12"
val vertxWebVersionClient = "4.5.12"
val junitJupiterVersion = "5.9.1"
var log4jVersion = "1.7.30"
val jacksonVersion = "2.17.1"
var projectlombok = "1.18.36"

val mainVerticleName = "com.octavio.starter_broker.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencyManagement {
  imports {
    mavenBom("org.apache.logging.log4j:log4j-bom:2.23.1")
  }
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web:$vertxWebVersion")
  implementation("io.vertx:vertx-web-client:$vertxWebVersionClient")
  implementation("io.vertx:vertx-config:4.5.12")
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl")
  implementation("org.slf4j:slf4j-api:$log4jVersion")
  compileOnly("org.projectlombok:lombok:$projectlombok")
  annotationProcessor("org.projectlombok:lombok:$projectlombok")


  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testCompileOnly("org.projectlombok:lombok:$projectlombok")
  testAnnotationProcessor("org.projectlombok:lombok:$projectlombok")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

jib {
  from {
    image = "amazoncorretto:21"
  }
  to {
    image = "example/jib/vertx4-start"
  }
  container {
    mainClass = launcherClassName
    args = listOf("run", mainVerticleName)
    ports = listOf("8888")
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
