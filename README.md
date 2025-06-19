# WebMorph

**WebMorph** is a web-oriented reactive [Spring](https://docs.spring.io/spring-framework/reference/) bootstrap designed
to cover your everyday backend needs — with simplicity, power, and performance in mind.
It provides a unified architecture
with [RSocket](https://docs.spring.io/spring-framework/reference/rsocket.html) + [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html), [JWT-based authentication](https://github.com/auth0/java-jwt),
a reactive EventBus, and fine-grained ACL powered by [LuckPerms](https://github.com/LuckPerms/LuckPerms).
Need deeper control? Modify everything at runtime
via [ClassTransform](https://github.com/Lenni0451/Classtransform) + [Mixin](https://github.com/SpongePowered/Mixin) — no forks, no hacks.
With built-in support for media streaming, geographic data, and auto-generated API docs, WebMorph helps you ship
full-featured backend systems — fast and clean.

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb/WebMorph">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-8.14-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://repo.jyraf.com/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshots&maven.groupId=dev.ckateptb&maven.artifactId=WebMorph&maven.extension=jar" target="_blank"><img alt="Download" src="https://img.shields.io/nexus/s/dev.ckateptb/WebMorph?server=https%3A%2F%2Frepo.jyraf.com"></a>
</p>

## 🚀 Features

- - - 🟢 — Implemented
- - - 🟡 — Partially implemented
- - - 🔴 — Not yet implemented


- 🟢 🧬 **ClassTransform + Mixin's** — Modify internals of your app and its dependencies at runtime — no forks, no
  hacks. You control your project, not the libraries you use.
    - 🟡 🧠 **Reflection** — Fast, streamlined access to jars, objects, classes, methods, fields, and internals — explore
      and manipulate any structure in your runtime, no matter how hidden.
- 🟢 ⚡ **RSocket + WebFlux** — Unified reactive entrypoint supporting both WebSocket and HTTP (XHR) in a single
  context.
- 🔴 🔐 **Spring Security with JWT** — Centralized JWT authentication shared across RSocket and WebFlux — one token, one
  security context.
    - 🟡 🛂 **LuckPerms integration** — Powerful permission and meta system, fully integrated into Spring Security for
      fine-grained access control.
- 🟢 📡 **EventBus** — Incredibly easy to use. Subscribe, transform, or cancel events with full control over the
  reactive flow — all with minimal overhead.
- 🔴 📁 **File Server** — Host photos, videos, audio, or anything else — with full support for streaming, range
  requests, and media delivery.
- 🔴 🌍 **Prebuilt Geo** — Instantly access country and city data via built-in endpoints. No external APIs, no CSV
  imports — just query and get accurate results out of the box.
- 🔴 📚 **Auto-Generated API Docs** — REST and RSocket endpoints are documented automatically — no Swagger, no OpenAPI
  overhead. Pure runtime introspection, ready for frontends or debugging.

## 📦 Installation

⚙️ Gradle (Kotlin DSL – build.gradle.kts)

```kts
plugins {
    id("java")
    id("com.gradleup.shadow").version("8.3.6")
    id("io.spring.dependency-management").version("1.1.7")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(group = "ch.qos.logback")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.jyraf.com/repository/maven-snapshots/")
}

dependencies {
    implementation("dev.ckateptb:WebMorph:<version>-SNAPSHOT")

    implementation(
        "org.bytedeco:ffmpeg:7.1-1.5.11:${
            System.getProperty("os.name").lowercase().split(" ")[0]
        }-x86_64"
    )

    // ❌ No need to include — already provided by WebMorph
    // implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    // implementation("org.springframework.boot:spring-boot-starter-rsocket:3.5.0")
    // implementation("org.springframework.boot:spring-boot-starter-webflux:3.5.0")
    // implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
    // implementation("org.springframework.security:spring-security-messaging:6.5.0")
    // implementation("org.springframework.security:spring-security-rsocket:6.5.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes(
                "Main-Class" to "com.example.project.Application" // << Main class here
            )
        }
        mergeServiceFiles()
    }
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
}
```

⚙️ Gradle (Groovy DSL – build.gradle)

```groovy
plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '8.3.6'
    id 'io.spring.dependency-management' version '1.1.7'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
    all {
        exclude module: 'spring-boot-starter-logging'
        exclude group: 'ch.qos.logback'
    }
}

repositories {
    mavenCentral()
    maven {
        url 'https://repo.jyraf.com/repository/maven-snapshots/'
    }
}


dependencies {
    implementation "dev.ckateptb:WebMorph:<version>-SNAPSHOT"

    def osName = System.getProperty("os.name").toLowerCase().split(" ")[0]
    implementation "org.bytedeco:ffmpeg:7.1-1.5.11:${osName}-x86_64"

    // ❌ No need to include — already provided by WebMorph
    // implementation 'org.springframework.boot:spring-boot-starter-validation:3.5.0'
    // implementation 'org.springframework.boot:spring-boot-starter-rsocket:3.5.0'
    // implementation 'org.springframework.boot:spring-boot-starter-webflux:3.5.0'
    // implementation 'org.springframework.boot:spring-boot-starter-security:3.5.0'
    // implementation 'org.springframework.security:spring-security-messaging:6.5.0'
    // implementation 'org.springframework.security:spring-security-rsocket:6.5.0'
}

tasks.named('shadowJar') {
    archiveClassifier.set('')
    manifest {
        attributes 'Main-Class': 'com.example.project.Application' // << Main class here
    }
    mergeServiceFiles()
}

tasks.named('build') {
    dependsOn tasks.named('shadowJar')
}

tasks.named('jar') {
    enabled = false
}
```

### ⚠️ Critical Note

It is strongly discouraged to use the spring-boot-gradle-plugin with WebMorph.
This plugin packages your application using a nested JAR format (BOOT-INF/...), which breaks classpath visibility and
makes runtime transformation via Mixin/ClassTransform impossible.

Instead, you must use the Shadow Gradle plugin, as shown in the example below. It produces a flat, fully compatible fat
JAR.

## 🧰 Usage

```java
import dev.ckateptb.webmorph.events.MixinTransformerRegistrationEvent;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // If you're using mixins, register your mixin package before the bootstrap
        MixinTransformerRegistrationEvent.<MixinTransformerRegistrationEvent>on(event ->
                event.addTransformer("org.example.package.mixins.**")
        );

        // This will bootstrap WebMorph (including Spring context and mixin system)
        WebMorph.bootstrap(args);
    }
}
```

### ⚠️ Critical Note

Start with VM options: `--add-opens java.base/java.lang=ALL-UNNAMED`

### ⚠️ Note

There is currently no official Wiki or documentation available for WebMorph.
However, you can explore the available functionality via JavaDocs in your IDE or simply browse the source code — it’s
clean, readable, and designed to be self-explanatory.

## License

### This project is licensed under the LGPL-3.0-only License.

See the [LICENSE.md](LICENSE.md) file for details.

## Author

### [CKATEPTb](https://github.com/CKATEPTb), [fakeivchenko](https://github.com/fakeivchenko)

Feel free to open issues and submit pull requests to improve the library!