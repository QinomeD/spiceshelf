package qinomed.spiceshelf.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import qinomed.spiceshelf.SpiceShelf;

public class SpiceShelfRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SpiceShelf.MODID);

    public static final RegistryObject<RecipeSerializer<SpiceConversionRecipe>> SPICE_CONVERSION_SERIALIZER =
            SERIALIZERS.register("spice_conversion", () -> SpiceConversionRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
