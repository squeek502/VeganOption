package squeek.veganoption.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Replaces the old DropSpecifier/BlockSpecifier system. Adds a given drop, with a certain % chance and quantity.
 * <br/>
 * "chance" and "num" are NumberProviders so technically can be any valid NumberProvider, but you probably want to use either
 * ConstantValue or UniformGenerator (min/max, for num).
 * <br/>
 * Provides a helper constructor which takes a Block rather than an array of LootItemConditions, which will automatically create
 * the LootItemCondition (LootItemBlockStatePropertyCondition) for the provided block. If you need more specificity (for example, dolls eye
 * only drops in certain biomes), you will have to use the regular constructor and specify the blockstate property condition.
 */
public class SimpleBlockDropLootModifier extends LootModifier
{
	public static final Supplier<Codec<SimpleBlockDropLootModifier>> CODEC = Suppliers.memoize(() -> {
		return RecordCodecBuilder.create(instance -> codecStart(instance).and(
			instance.group(
				ForgeRegistries.ITEMS.getCodec().fieldOf("drop").forGetter(m -> m.drop),
				NumberProviders.CODEC.fieldOf("chance").forGetter(m -> m.chance),
				NumberProviders.CODEC.fieldOf("num").forGetter(m -> m.num)
			)).apply(instance, SimpleBlockDropLootModifier::new)
		);
	});

	private final Item drop;
	private final NumberProvider chance;
	private final NumberProvider num;

	public SimpleBlockDropLootModifier(LootItemCondition[] conditionsIn, Item drop, NumberProvider chance, NumberProvider num)
	{
		super(conditionsIn);
		this.drop = drop;
		this.chance = chance;
		this.num = num;
	}

	public SimpleBlockDropLootModifier(Block block, Item drop, NumberProvider chance, NumberProvider num)
	{
		this(new LootItemCondition[] { new LootItemBlockStatePropertyCondition.Builder(block).build() }, drop, chance, num);
	}

	private boolean shouldDrop(LootContext context)
	{
		return context.getRandom().nextFloat() < chance.getFloat(context); // todo: test silk touch
	}

	private int amountToDrop(LootContext context)
	{
		return num.getInt(context);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		if (shouldDrop(context))
			generatedLoot.add(new ItemStack(drop, amountToDrop(context)));
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC.get();
	}
}
