# MyRobotLab Android
Android app to connect to a MyRobotLab instance. This app can
control a connected instance via the WebGui service. It can
also expose new services to the connected instance that can take advantage
of the unique capabilities of mobile devices.

## Getting Started
MyRobotLab Android is currently in **Alpha**, there will be bugs and problems!

Right now, this app can only be used with a special version of MyRobotLab
that contains modifications made by me. These modifications are in the process
of being merged into MyRobotLab and this README will be updated once they have been merged.

1. Clone [myrobotlab](https://github.com/MyRobotLab/myrobotlab/tree/ap-android-alpha-1)
2. Checkout `ap-android-alpha-1` branch. This branch contains all needed changes
3. Build with `mvn package -DskipTests`
4. Copy `myrobotlab.jar` to cwd with `cp target/myrobotlab.jar ./`
5. Run with `./myrobotlab.sh` on Linux or `./myrobotlab.bat` on Windows
6. Open a new terminal and clone this repo
7. Open the project in Android Studio
8. Compile and run MyRobotLab Android on either an emulator or a physical device
9. Press the `Connect` button on the `Client` tab and enter your computer's
  IP address. If running an emulator on the same machine, use `10.0.2.2` to connect to localhost.
10. Explore the app. There are currently 2 services available, `TestKotlinService` and `Gyro`.

# For Developers

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
It also searches for classes marked with `@MrlService` and adds them to the service registry.

It lives in the `org.myrobotlab.kotlin.ksp` package under the `mrlkt-ksp` module.

### MyRobotLab Android
This module contains the Android app source code. It uses Jetpack Compose as its UI framework.
All Android-specific classes are under the `org.myrobotlab.android` package, with
Android-only services living under the `org.myrobotlab.android.service` package.
