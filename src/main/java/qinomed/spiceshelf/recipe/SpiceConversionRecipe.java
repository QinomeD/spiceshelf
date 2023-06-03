package qinomed.spiceshelf.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import qinomed.spiceshelf.SpiceShelf;
import qinomed.spiceshelf.util.EmptyContainer;

public class SpiceConversionRecipe implements Recipe<EmptyContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final String sound;

    public SpiceConversionRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, ItemStack output, String sound) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.sound = sound;
    }

    public String getSoundID() {
        return this.sound;
    }

    public boolean matches(ItemStack input, ItemStack spice, Level level) {
        return !level.isClientSide() && inputs.get(0).test(input) && inputs.get(1).test(spice);
    }

    @Override
    public boolean matches(EmptyContainer pContainer, Level pLevel) {
        return !pLevel.isClientSide();
    }

    @Override
    public ItemStack assemble(EmptyContainer pContainer) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputs;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SpiceConversionRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "spice_conversion";
    }

    public static class Serializer implements RecipeSerializer<SpiceConversionRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(SpiceShelf.MODID, "spice_conversion");

        @Override
        public SpiceConversionRecipe fromJson(ResourceLocation id, JsonObject serializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(serializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(2, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            String sound = GsonHelper.getAsString(serializedRecipe, "sound", "");

            return new SpiceConversionRecipe(id, inputs, output, sound);
        }

        @Override
        public @Nullable SpiceConversionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            String sound = buf.readUtf();
            return new SpiceConversionRecipe(id, inputs, output, sound);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SpiceConversionRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeUtf(recipe.getSoundID());
        }
    }
}
