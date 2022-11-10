# MyRobotLab Android
Android app to connect to a MyRobotLab instance. This app can
control a connected instance via the WebGui service. It can
also expose new services to the connected instance that can take advantage
of the unique capabilities of mobile devices.

## Architecture
MyRobotLab Android is written in Kotlin to allow easy porting to an iOS app
in the future. It was also split up into multiple subprojects to allow
the underlying Kotlin bindings (mrlkt) to be used in a standalone program.

### Mrlkt
Mrlkt is the Kotlin version of [mrlpy](https://github.com/AutonomicPerfectionist/mrlpy).
It provides a framework for communicating with an MRL instance using Kotlin syntax, as
well as a higher level API to create services written entirely in Kotlin.

Mrlkt lives in the `org.myrobotlab.kotlin` package under the `mrlkt` module.
Services common to Kotlin, mainly the Runtime implementation, are housed
in the `org.myrobotlab.kotlin.service` package.


### Mrlkt Annotations
This is a small module meant to contain only annotation definitions. This enables 
KSP plugins to depend on only this module and not the entirety of mrlkt.
These annotations live under the `org.myrobotlab.kotlin.annotations` package in the `mrlkt-annotations` module.

### Mrlkt KSP
This is a KSP plugin that performs compile-time processing of annotations. Its primary
purpose is to generate the list of mappings from mrlkt classes to Java mrl classes.
In the future it will also perform compile-time scanning for Service classes
to build a registry of available services.

It lives in the `org.myrobotlab.kotlin.ksp` package under the `mrlkt-ksp` module.

### MyRobotLab Android
This module contains the Android app source code. It uses Jetpack Compose as its UI framework.
All Android-specific classes are under the `org.myrobotlab.android` package, with
Android-only services living under the `org.myrobotlab.android.service` package.
