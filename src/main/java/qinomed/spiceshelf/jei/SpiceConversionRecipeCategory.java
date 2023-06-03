package qinomed.spiceshelf.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import qinomed.spiceshelf.SpiceShelf;
import qinomed.spiceshelf.recipe.SpiceConversionRecipe;

public class SpiceConversionRecipeCategory implements IRecipeCategory<SpiceConversionRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(SpiceShelf.MODID, "spice_conversion");
    public static final ResourceLocation TEXTURE = new ResourceLocation(SpiceShelf.MODID, "textures/gui/jei_spice_conversion.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SpiceConversionRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 76, 57);
        this.icon = helper.createDrawableItemStack(new ItemStack(Items.SUGAR));
    }

    @Override
    public RecipeType<SpiceConversionRecipe> getRecipeType() {
        return SpiceShelfJEIPlugin.SPICE_CONVERSION_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.spice_conversion");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpiceConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 40).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 40).addItemStack(recipe.getResultItem());
    }
}
