package squeek.veganoption.mixins;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin
{
	@Shadow private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
	private void veganoption$convertRecipes(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller, CallbackInfo ci)
	{
		// todo. look at GT community edition (screret).
		// because of the annoying way recipes are stored at runtime now (recipes and byName), it may be easier to just duplicate the
		// recipes with our item tags. there shouldn't be any worry of "conflicts" because the recipes are the same (ie, if you are using a
		// spider eye, whether it gets the recipe for the spider eye ingredient or the reagent poisonous tag, is irrelevant, so long as all
		// recipes are still valid.
	}
}
