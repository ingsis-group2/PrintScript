plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "PrintScript"
include("lexer")
include("parser")
include("commons")
include("sca")
