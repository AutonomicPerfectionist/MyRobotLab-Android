# MyRobotLab Android
Android app to connect to a MyRobotLab instance. This app can
control a connected instance via the WebGui service. It can
also expose new services to the connected instance that can take advantage
of the unique capabilities of mobile devices.

## Architecture
MyRobotLab Android is written in Kotlin to allow easy porting to an iOS app
in the future. It was also split up into multiple subprojects to allow
the underlying Kotlin bindings (mrlkt) to be used in a standalone program.

## Mrlkt
Mrlkt is the Kotlin version of [mrlpy](https://github.com/AutonomicPerfectionist/mrlpy).
It provides a framework for communicating with an MRL instance using Kotlin syntax, as
well as a higher level API to create services written entirely in Kotlin.

Mrlkt lives in the `org.myrobotlab.kotlin` package
