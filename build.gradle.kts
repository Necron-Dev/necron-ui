val group = "moe.nec"
val modId = "necron-ui"
val version = "0.0.1"

val lombokVersion = "1.18.46"
val enchantedJavaLibraryVersion = "0.3.0"
val minecraftVersion = "1.21.11"
val parchmentVersion = "2025.12.20"
val fabricLoaderVersion = "0.19.1"
val fabricApiVersion = "0.141.3+1.21.11"

plugins {
  val loomVersion = "1.15-SNAPSHOT"
  val enchantedJavaPluginVersion = "0.3.1"
  id("net.fabricmc.fabric-loom-remap") version loomVersion
  id("net.yqloss.enchanted-java-plugin") version enchantedJavaPluginVersion
}

val replacements = listOf(
  ::modId,
  ::version,
).associate { it.name to it() }

base {
  archivesName = "$modId-$version-fabric-$minecraftVersion"
}

repositories {
  mavenCentral()
  maven {
    name = "ParchmentMC"
    url = uri("https://maven.parchmentmc.org")
  }
  maven("https://maven.yqloss.net")
}

dependencies {
  compileOnly("org.projectlombok:lombok:$lombokVersion")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
  compileOnly("net.yqloss:enchanted-java-library:$enchantedJavaLibraryVersion")
  minecraft("com.mojang:minecraft:$minecraftVersion")
  mappings(
    @Suppress("UnstableApiUsage")
    loom.layered {
      officialMojangMappings()
      parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    },
  )
  modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

tasks.processResources {
  inputs.properties(replacements)
  filesMatching("fabric.mod.json") {
    expand(replacements)
  }
  rename("\\$\\{modId}\\.mixins\\.json", "$modId.mixins.json")
}

tasks.withType<JavaCompile>().configureEach {
  options.release = 25
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}
