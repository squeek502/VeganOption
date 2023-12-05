package squeek.veganoption.loot;

import static squeek.veganoption.VeganOption.REGISTER_LOOTMODIFIERS;

public class LootRegistration
{
	/**
	 * Register our loot table serializers.
	 */
	public static void init()
	{
		REGISTER_LOOTMODIFIERS.register("simple_block_drop", SimpleBlockDropLootModifier.CODEC);
		REGISTER_LOOTMODIFIERS.register("replace_with", ReplaceLootModifier.CODEC);
	}
}
