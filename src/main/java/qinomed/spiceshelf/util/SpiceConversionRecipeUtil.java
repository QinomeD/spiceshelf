package qinomed.spiceshelf.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import qinomed.spiceshelf.recipe.SpiceConversionRecipe;

import java.util.List;

public class SpiceConversionRecipeUtil {

    public static SpiceConversionRecipe getRecipeFor(ItemStack input, ItemStack spice, Level level) {
        List<SpiceConversionRecipe> recipes = level.getRecipeManager().getAllRecipesFor(SpiceConversionRecipe.Type.INSTANCE);

        for (SpiceConversionRecipe recipe : recipes) {
            if (recipe.matches(input, spice, level)) {
                return recipe;
            }
        }

        return null;
    }
}
