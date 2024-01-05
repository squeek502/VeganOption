# About the Crafting and Usage handler syntax
To define crafting or usage text for an item or block, create a string with the following name:

 	<registry_name>.vowiki.crafting
 	<registry_name>.vowiki.usage

Where <registry_name> is the registry name of the item or block (usually item.veganoption.item_name or block.veganoption.block_name)

To insert the name of the corresponding item or block, use %1$s

To insert another localized string inside a usage/crafting string, use the format {unlocalized.string.name}

To reference another block, item or fluid use the syntax [[modid:item_name]]. If an item and fluid share a modid:item_name description ID, it will default to the item.

To insert a line break, use \n

Example:

    example.string=Nested string example
 	item.VeganOption.exampleItem.name=Example
 	item.VeganOption.exampleItem.vowiki.usage=%1$s is nothing like [[minecraft:wheat]], but it can do a {example.string}.\n\nLines broken.

would give exampleItem a Usage tab that looks like:

 	Example is nothing like Wheat, but it can do a Nested string example.
	
	Lines broken.
