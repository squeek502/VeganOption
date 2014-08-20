The Vegan Option
================

Minecraft mod that seeks to add vegan alternatives to all Minecraft animal products using the following guidelines:

 - Nothing added to worldgen
 - No new crops added

### Currently Implemented Alternatives

#### Jute fibre/Burlap (leather/string alternative)

 - Random amount of 'Jute stalks' drop from harvesting large ferns
 - 3x3 of Jute stalks crafted together makes a 'Jute bundle'
 - Jute bundle when placed next to a water block will start retting
 - Once retted, the jute bundle can be broken to get a variable amount of 'Jute fibre'
 - Jute fibre can be crafted into burlap (2x2) or string (1x2)

###Building

1. Clone this repository
2. If you have [Gradle](http://www.gradle.org/) installed, open a command line in the cloned directory and execute: ```gradle build```. To give the build a version number, use ```gradle build -Pversion=<version>``` instead (example: ```gradle build -Pversion=1.0.0```)
 * If you don't have Gradle installed, you can use [ForgeGradle](http://www.minecraftforge.net/forum/index.php?topic=14048.0)'s gradlew/gradlew.bat instead