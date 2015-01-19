The Vegan Option
================

Minecraft mod that seeks to add vegan alternatives to all Minecraft mob/animal products using the following guidelines:

- Nothing added to worldgen
- No new crops added

### Currently Implemented Alternatives

#### Jute Fibre/Burlap (leather/string)

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

#### Straw Bed (bed)

- Crafted using Hay Bales instead of Wool

#### Fossils (bone)

- Bones drop (very) rarely when mining Stone

#### Plant Milk (milk)

- 2xPumpkin Seeds + Sugar + Water can be crafted to create Pumpkin Seed Milk (a 1:1 alternative to Milk)

#### Egg Replacers (egg)

- Apples can be crafted into Apple Sauce (Apple + Bowl)
- Potatoes can be crafted into Potato Starch (Potato + Piston)
- Apple Sauce and Potato Starch are 1:1 alternatives to Eggs

#### Resin (slimeball)

- Resin is a semi-rare drop from harvesting Spruce Wood
- Resin is a 1:1 alternative to Slimeballs

#### Vegetable Oil Ink (ink sac)

- Sunflower Seeds drop from harvesting Sunflowers
- Sunflower Seeds can be crafted into Sunflower Oil (Sunflower Seeds + Weighted Pressure Plate + Bottle)
- Sunflower Oil can be smelted into Vegetable Wax in a furnace
- Resin can be smelted into Rosin in a furnace
- Black Vegetable Oil Ink can be crafted from Sunflower Oil + Rosin + Vegetable Wax + Charcoal
- Black Vegetable Oil Ink is a 1:1 alternative to Ink Sacs

### Building

1. Clone this repository
2. If you have [Gradle](http://www.gradle.org/) installed, open a command line in the cloned directory and execute: ```gradle build```. To give the build a version number, use ```gradle build -Pversion=<version>``` instead (example: ```gradle build -Pversion=1.0.0```)
 * If you don't have Gradle installed, you can use [ForgeGradle](http://www.minecraftforge.net/forum/index.php?topic=14048.0)'s gradlew/gradlew.bat instead