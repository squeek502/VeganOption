# About the Crafting and Usage handler syntax
To define crafting or usage text for an item or block, create a string with the following name:

 	<registry_name>.vowiki.crafting
 	<registry_name>.vowiki.usage

Where <registry_name> is the registry name of the item or block (usually item.veganoption.item_name or block.veganoption.block_name)

To insert the name of the corresponding item or block, use %1$s
To insert another localized string inside a usage/crafting string, use the format {unlocalized.string.name}
To reference another block or item use the syntax [[modid:item_name]] (when specifying metadata, use [[modid:item_name:meta]] instead)
To insert a line break, use \n

Example:

    example.string=Nested string example
 	item.VeganOption.exampleItem.name=Example
 	item.VeganOption.exampleItem.vowiki.usage=%1$s is nothing like [[minecraft:wheat]] or [[minecraft:golden_apple:1]], but it can do a {example.string}.\n\nLines broken.

would give exampleItem a Usage tab that looks like:

 	Example is nothing like Wheat or Golden Apple, but it can do a Nested string example.
	
	Lines broken.
