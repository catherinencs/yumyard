buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("com.google.gms:google-services:4.3.15") // This should be the only place to define this classpath
    }
}


