The Vegan Option
================

Minecraft mod that seeks to add vegan alternatives to all Minecraft animal products using the following guidelines:

- Nothing added to worldgen
- No new crops added

### Currently Implemented Alternatives

#### Jute fibre/Burlap (leather/string)

- Jute Stalks drop from harvesting Large Ferns
- 3x3 of Jute Stalks crafted together makes a Jute Bundle
- Jute Bundle, when placed next to or under water, will start retting
- Once retted, the Jute Bundle can be broken to get a variable amount of Jute Fibre
- Jute Fibre can be crafted into Burlap (2x2) or String (1x2)
- Burlap is a 1:1 alternative to Leather

#### Kapok (wool/string/feather)

- Kapok Tufts are a semi-rare drop from Jungle Leaves
- Kapok Tufts can be crafted into Block of Kapok (2x2) or String (1x3)
- Block of Kapok is a 1:1 alternative to Wool
- Kapok Tufts can be crafted with a Stick to make a Faux Feather
- Faux Feather is a 1:1 alternative to Feather

### Building

1. Clone this repository
2. If you have [Gradle](http://www.gradle.org/) installed, open a command line in the cloned directory and execute: ```gradle build```. To give the build a version number, use ```gradle build -Pversion=<version>``` instead (example: ```gradle build -Pversion=1.0.0```)
 * If you don't have Gradle installed, you can use [ForgeGradle](http://www.minecraftforge.net/forum/index.php?topic=14048.0)'s gradlew/gradlew.bat instead