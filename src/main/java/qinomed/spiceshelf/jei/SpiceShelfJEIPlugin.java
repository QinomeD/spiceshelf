package qinomed.spiceshelf.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import qinomed.spiceshelf.SpiceShelf;
import qinomed.spiceshelf.recipe.SpiceConversionRecipe;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class SpiceShelfJEIPlugin implements IModPlugin {
    public static RecipeType<SpiceConversionRecipe> SPICE_CONVERSION_TYPE =
            new RecipeType<>(SpiceConversionRecipeCategory.UID, SpiceConversionRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SpiceShelf.MODID, "jei_integration");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SpiceConversionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<SpiceConversionRecipe> recipes = manager.getAllRecipesFor(SpiceConversionRecipe.Type.INSTANCE);
        registration.addRecipes(SPICE_CONVERSION_TYPE, recipes);
    }
}
