The Vegan Option
================

A Minecraft mod that seeks to add vegan alternatives to all Minecraft mob/animal products using the following guidelines:

- Nothing added to worldgen
- No new crops added

## Building The Mod

1. Clone this repository
2. If you have [Gradle](http://www.gradle.org/) installed, open a command line in the cloned directory and execute: ```gradle build```. To give the build a version number, use ```gradle build -Pversion=<version>``` instead (example: ```gradle build -Pversion=1.0.0```)
 * If you don't have Gradle installed, you can use [ForgeGradle](http://www.minecraftforge.net/forum/index.php?topic=14048.0)'s gradlew/gradlew.bat instead

## What's Included

### Alternatives by Item

Item | Alternative
-----|------------
Bone | [Fossils](#fossils)
Bone Meal (as a fertilizer) | [Fertilizer](#fertilizer)
Bone Meal (as a white dye) | [White Vegetable Oil Ink](#vegetable-oil-ink)
Milk (as a food) | [Plant Milk](#plant-milk)
Milk (as a status effect curative) | [Soap](#soap)
Ink Sac | [Black Vegetable Oil Ink](#vegetable-oil-ink)
Slimeball | [Resin](#resin)
Wool | [Block of Kapok](#kapok)
Feather | [Faux Feather](#faux-feather)
String | [Jute Fiber](#jute) or [Kapok Tufts](#kapok)
Leather | [Burlap](#burlap)
Leather Armor | [Burlap Armor](#burlap)
Raw/Cooked Meat/Fish (as a food) | No alternative yet (HarvestCraft's tofu is recommended for this purpose)
Egg (as a food) | No alternative yet (HarvestCraft's tofu is recommended for this purpose)
Egg (as a baking agent) | [Potato Starch](#egg-replacers) or [Apple Sauce](#egg-replacers)
Egg (as an object) | [Plastic Egg](#bioplastic) 
Gunpowder | Charcoal + [Sulfur](#sulfur) + [Saltpeter](#saltpeter)
Rotten Flesh | [Rotten Plants](#composting)
Blaze Rod | [Plastic Rod](#bioplastic) + [Rosin](#resin) + [Vegetable Wax](#vegetable-oil) + Flint & Steel
Pufferfish | [Frozen Bubble](#frozen-bubble)
Ender Pearl | [Frozen Bubble](#frozen-bubble) + [Raw Ender](#raw-ender)
Ghast Tear | [Proof of Suffering](#proof-of-suffering)
Spider Eye | [False Morel](#false-morel) or [Doll's Eye](#dolls-eye)
Fermented Spider Eye | [Fermented False Morel](#false-morel)
Mob Heads | [Blank Mob Head](#blank-mob-head) + Various Dyes
Nether Star | No alternative yet
Dragon Egg | No alternative yet

### Content

#### Bioplastic
- [Potato Starch](#potato-starch) in a furnace creates Bioplastic
- Bioplastic crafted in a 1x2 shape creates a Plastic Rod
- Bioplastic crafted in a diamond shape creates a Plastic Egg (Egg [as an object] alternative)

> *References: [Make Your Own Bioplastic](http://green-plastics.net/posts/10/video-brandon121233/)*

#### Burlap
- [Jute Fiber](#jute) crafted in a 2x2 creates Burlap (Leather alternative)
- Burlap crafted in the standard armor patterns creates Burlap Armor (equivalent to Leather Armor)

> *References: [Hessian (cloth)](http://en.wikipedia.org/wiki/Hessian_%28cloth%29)*

#### Composting
- Chest + Sticks crafted together creates a Composter
- "Green" materials alone in a Composter creates Rotten Plants (Rotten Flesh alternative)
- "Green" + "brown" materials together in a Composter creates Compost
- Compost provides passive improvement to adjacent Farmland
- Compost + [Saltpeter](#saltpeter) crafted together creates Fertilizer (Bone Meal fertilizer alternative)

> *References: [The Carbon:Nitrogen Ratio (C:N)](http://www.homecompostingmadeeasy.com/carbonnitrogenratio.html)*

#### Doll's Eye
- Doll's Eye (Spider Eye alternative) occasionally drop from harvesting Grass in a temperate forest biome

> *References: [Actaea pachypoda (doll's-eyes, white baneberry)](http://en.wikipedia.org/wiki/Actaea_pachypoda)*

#### Egg Replacers
- Apple + Wooden Bowl crafted together creates Apple Sauce (Egg [as a baking agent] alternative)
- Piston + Potato crafted together creates Potato Starch (Egg [as a baking agent] alternative)
- If a potato is crushed by a piston in the world, it will also create Potato Starch

> *References: [Egg Substitutions in Baking](http://chefinyou.com/egg-substitutes-cooking/), [Ener-G Egg Replacer](http://www.ener-g.com/egg-replacer.html)*

#### Raw Ender
- 2 Obsidian + Diamond + Emerald creates Encrusted Diamond
- 4 Encrusted Diamond blocks placed in a diamond shape creates an Ender Rift in the center of it
- Water flowing through an Ender Rift at night has a chance to be converted into Raw Ender. However, if this is done during the day, a random block around the Ender Rift will be consumed by it and lost forever.

#### Faux Feather
- [Plastic Rod](#bioplastic) + [Kapok Tuft](#kapok) creates a Faux Feather (Feather alternative)

#### Fossils
- Bones occasionally drop when mining Stone

#### Frozen Bubble
- [Soap](#soap) + Water + Sugar + Glass Bottle crafted together creates Soap Solution
- Soap Solution + 8 Ice (or 1 Packed Ice) crafted together creates a Frozen Bubble (Pufferfish alternative)
- Soap Solution used by a player or a dispenser creates a Bubble in the world
- A Bubble will freeze if it is surrounded by enough Ice/Snow/Packed Ice
- Frozen Bubble + Raw Ender Bucket crafted together creates an Ender Pearl
- Frozen Bubble placed in Raw Ender (in the world) will soak up the Raw Ender and become an Ender Pearl once it is fully filled

> *References: [Frozen Bubbles Freeze at -40c](https://www.youtube.com/watch?v=b0eCAL_t7pg)*

#### Saltpeter
- Saltpeter occasionally drops when mining Sandstone

> *References: [Sodium nitrate (Chile saltpeter)](http://en.wikipedia.org/wiki/Sodium_nitrate), [Chilean caliche](http://en.wikipedia.org/wiki/Caliche#Chilean_caliche)*

#### Sulfur
- Sulfur occasionally drops when mining Netherrack

#### Vegetable Oil Ink
- [Vegetable Oil](#vegetable-oil) + [Vegetable Wax](#vegetable-oil) + [Rosin](#resin) + Charcoal creates Black Vegetable Oil Ink (Ink Sac alternative)
- [Vegetable Oil](#vegetable-oil) + [Vegetable Wax](#vegetable-oil) + [Rosin](#resin) + Nether Quartz creates White Vegetable Oil Ink (Bone Meal dye alternative)

> *References: [Soy ink](http://en.wikipedia.org/wiki/Soy_ink), [The Printing Ink Manual pg219](https://books.google.com/books?id=2PwKTqO5dioC&lpg=PA218&ots=0uokv2EZl3&pg=PA219)*

#### Jute
- Jute Stalks drop from harvesting Large Ferns
- 3x3 of Jute Stalks crafted together makes a Jute Bundle
- Jute Bundle, when placed next to or under water, will start retting
- Once retted, the Jute Bundle can be broken to get a variable amount of Jute Fiber
- Jute Fiber can be crafted into [Burlap](#burlap) (2x2) or String (1x2)

> *References: [Jute](http://en.wikipedia.org/wiki/Jute), [Jute cultivation](http://en.wikipedia.org/wiki/Jute_cultivation), [Retting](http://en.wikipedia.org/wiki/Retting)*

#### Kapok
- Kapok Tufts occasionally drop from harvesting Jungle Leaves
- Kapok Tufts can be crafted into Block of Kapok (2x2; Wool alternative) or String (1x3)

> *References: [Ceiba pentandra](http://en.wikipedia.org/wiki/Ceiba_pentandra)*

#### Blank Mob Head
- Water + [Potato Starch](#potato-starch) + 4 Paper creates Papier-Mâché
- 8 Papier-Mâché + Melon crafted together creates a Blank Mob Head
- Blank Mob Head + 8 of a certain type of dye crafted together creates each of the mob heads

> *References: [Papier-mâché](http://en.wikipedia.org/wiki/Papier-m%C3%A2ch%C3%A9)*

#### Plant Milk
- Water + 2 Pumpkin Seeds + Sugar crafted together creates Pumpkin Seed Milk (Milk [as a food] alternative)

> *References: [Perfect Pumpkin Seed Milk](http://healthyblenderrecipes.com/recipes/homemade_raw_pumpkin_seed_milk)*

#### Proof of Suffering
- Fragments of Suffering occasionally drop when mining Soul Sand
- 8 Fragments of Suffering + Gold Nugget crafted together create Proof of Suffering (Ghast Tear alternative)

#### Resin
- Resin (Slimeball alternative) occasionally drops when harvesting Spruce Wood
- Resin in a furnace creates Rosin

> *References: [Resin](http://en.wikipedia.org/wiki/Resin), [Rosin](http://en.wikipedia.org/wiki/Rosin)*

#### Soap
- Water + 3 Charcoal crafted together creates Wood Ash Lye
- Wood Ash Lye + [Vegetable Oil](#vegetable-oil) + [Rosin](#rosin) creates Soap (Milk [as a status effect curative] alternative)

> *References: [How to Make Soap from Ashes](http://www.motherearthnews.com/homesteading-and-livestock/how-to-make-soap-from-ashes-zmaz72jfzfre.aspx)*

#### Straw Bed
- 3 Hay Bales + 3 Wood Planks creates a Straw Bed (non-perfect Bed alternative)
- Straw Bed deals a tiny amount of damage to the player each time it is slept in (it's itchy)

> *References: [Palliasse](http://en.wiktionary.org/wiki/palliasse)*

#### False Morel
- False Morels (Spider Eye alternative) occasionally drop when harvesting Mycelium
- False Morel + Brown Mushroom + Sugar crafted together creates Fermented False Morel (Fermented Spider Eye alternative)

> *References: [False morel](http://en.wikipedia.org/wiki/False_morel), [Gyromitra esculenta](http://en.wikipedia.org/wiki/Gyromitra_esculenta)*

#### Vegetable Oil
- Sunflower Seeds drop from harvesting Sunflowers
- Sunflower Seeds + Weighted Pressure Plate + Bottle crafted together creates Vegetable Oil
- Vegetable Oil in a furnace creates Vegetable Wax

> *References: [Vegetable oil](http://en.wikipedia.org/wiki/Vegetable_oil)*

### Integration With Other Mods

- Extensive NEI integration (every single item has both Usage and Crafting of some sort defined for it)
- [Bioplastic](#bioplastic) is added as a tool material to Tinkers' Construct (defers to MFR's plastic if it exists)
- [Frozen Bubbles](#frozen-bubble) can be filled with and drained of their Raw Ender in a Thermal Expansion Fluid Transposer
- [Fertilizer](#fertilizer) is a valid fertilizer for MFR's Fertilizer
- Support for the [Version Checker mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2091981-version-checker)
- Various Waila integration
- Wood Ash from Witchery can be used to create [Wood Ash Lye](#soap)