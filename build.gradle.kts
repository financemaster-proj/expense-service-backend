plugins {
	java
	id("org.springframework.boot") version "4.1.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.financemaster"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.flywaydb:flyway-database-postgresql")
	runtimeOnly("org.postgresql:postgresql")

	// `test` is deliberately minimal. Anything that needs a real database
	// belongs in the integrationTest suite below - see FM-002.
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
	suites {
		// Fast feedback: unit and slice tests only, no Docker, runs on every save.
		named<JvmTestSuite>("test") {
			useJUnitJupiter()
		}

		// Slow path: real Postgres via Testcontainers.
		// Deliberately NOT wired into `check`, so `./gradlew build` stays
		// container-free. FM-007's CI calls this task explicitly instead.
		register<JvmTestSuite>("integrationTest") {
			useJUnitJupiter()

			dependencies {
				// A new suite starts with an empty classpath: without this it
				// cannot see the main classes or application.yaml at all.
				implementation(project())

				implementation("org.springframework.boot:spring-boot-starter-actuator-test")
				implementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
				implementation("org.springframework.boot:spring-boot-starter-flyway-test")
				implementation("org.springframework.boot:spring-boot-starter-webmvc-test")

				// Testcontainers lives here and ONLY here. If these were on the
				// `test` classpath, a stray @Testcontainers in src/test would
				// quietly reintroduce Docker into the fast path.
				implementation("org.springframework.boot:spring-boot-testcontainers")
				implementation("org.testcontainers:testcontainers-junit-jupiter")
				implementation("org.testcontainers:testcontainers-postgresql")

				runtimeOnly("org.junit.platform:junit-platform-launcher")
			}

			targets {
				all {
					testTask.configure {
						// Fail on a broken unit test before paying for a container.
						shouldRunAfter(tasks.named("test"))
					}
				}
			}
		}
	}
}
