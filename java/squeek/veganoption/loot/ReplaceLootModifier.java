package squeek.veganoption.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Loot modifier that replaces a given drop with another. Used, for instance, to replace sunflower drops with sunflower seeds.
 */
public class ReplaceLootModifier extends LootModifier
{
	public static final Supplier<Codec<ReplaceLootModifier>> CODEC = Suppliers.memoize(() -> {
		return RecordCodecBuilder.create(instance -> codecStart(instance).and(
			instance.group(
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("replace").forGetter(m -> m.replace),
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("with").forGetter(m -> m.with)
			)).apply(instance, ReplaceLootModifier::new)
		);
	});

	private final Item replace;
	private final Item with;

	public ReplaceLootModifier(LootItemCondition[] conditions, Item replace, Item with)
	{
		super(conditions);
		this.replace = replace;
		this.with = with;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		generatedLoot.removeIf(stack -> stack.getItem() == replace);
		generatedLoot.add(new ItemStack(with));
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC.get();
	}
}
