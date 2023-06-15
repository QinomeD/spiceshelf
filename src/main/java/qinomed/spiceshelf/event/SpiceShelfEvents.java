package qinomed.spiceshelf.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import qinomed.spiceshelf.SpiceShelf;
import qinomed.spiceshelf.data.SpiceEffectsLoader;
import qinomed.spiceshelf.recipe.SpiceConversionRecipe;
import qinomed.spiceshelf.util.EffectStringUtil;
import qinomed.spiceshelf.util.SpiceConversionRecipeUtil;
import qinomed.spiceshelf.util.SpiceShelfTags;

import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SpiceShelf.MODID)
public class SpiceShelfEvents {
    public static final SpiceEffectsLoader SPICE_EFFECTS_LOADER = new SpiceEffectsLoader();

    @SubscribeEvent
    public static void applySpice(ItemStackedOnOtherEvent event) {
        if (event.getClickAction() == ClickAction.SECONDARY && !event.getPlayer().getLevel().isClientSide()) {
            Map<ResourceLocation, MobEffectInstance> spiceEffects = SPICE_EFFECTS_LOADER.getSpiceEffects();

            ItemStack stackedOn = event.getStackedOnItem();
            ItemStack carriedItem = event.getCarriedItem();
            ResourceLocation spiceID = ForgeRegistries.ITEMS.getKey(carriedItem.getItem());
            Player player = event.getPlayer();
            Level level = player.getLevel();

            SpiceConversionRecipe recipe = SpiceConversionRecipeUtil.getRecipeFor(stackedOn, carriedItem, level);

            if (recipe != null) {
                event.setCanceled(true);
                var resultStack = recipe.getResultItem();
                resultStack.setTag(stackedOn.getTag());
                if (stackedOn.getCount() > 1) {
                    ItemHandlerHelper.giveItemToPlayer(player, resultStack);
                    stackedOn.shrink(1);
                } else {
                    event.getSlot().set(resultStack);
                }

                //Leave container behind
                if (carriedItem.getItem().hasCraftingRemainingItem(carriedItem))
                    if (carriedItem.getCount() > 1) {
                        ItemHandlerHelper.giveItemToPlayer(player, carriedItem.getCraftingRemainingItem());
                        carriedItem.shrink(1);
                    } else
                        event.getCarriedSlotAccess().set(carriedItem.getCraftingRemainingItem());
                else
                    carriedItem.shrink(1);

                playSoundFromID(recipe.getSoundID(), level, player.getOnPos());
            } else if (spiceEffects.containsKey(spiceID) && stackedOn.getItem().isEdible()) {
                if (!stackedOn.is(SpiceShelfTags.SPICE_BLACKLIST) && !stackedOn.getOrCreateTag().getCompound("spices").contains(String.valueOf(spiceID))) {
                    event.setCanceled(true);

                    // Give separate item if stack > 1
                    if (stackedOn.getCount() > 1) {
                        var spices = stackedOn.getOrCreateTag().getCompound("spices");
                        spices.putString(String.valueOf(spiceID), EffectStringUtil.effectToString(spiceEffects.get(spiceID)));

                        var stack = stackedOn.copy();
                        stack.setCount(1);
                        stack.getOrCreateTag().put("spices", spices);
                        player.addItem(stack);

                        stackedOn.shrink(1);
                    } else {
                        // Swap item if stack == 1
                        var spices = stackedOn.getOrCreateTag().getCompound("spices");
                        spices.putString(String.valueOf(spiceID), EffectStringUtil.effectToString(spiceEffects.get(spiceID)));

                        stackedOn.getOrCreateTag().put("spices", spices);
                    }

                    //Leave container behind
                    if (carriedItem.getItem().hasCraftingRemainingItem(carriedItem))
                        event.getCarriedSlotAccess().set(carriedItem.getCraftingRemainingItem());
                    else
                        carriedItem.shrink(1);

                    playSoundFromID("", level, player.getOnPos());
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        var tag = stack.getTag();
        if (tag != null && tag.contains("spices")) {
            var spicesTag = tag.getCompound("spices");
            var spices = spicesTag.getAllKeys().stream().toList();
            spices.forEach(s -> event.getToolTip().add(spices.indexOf(s)+1, Component.translatable("spice." + StringUtils.replace(s, ":", "."))
                    .withStyle(Objects.requireNonNull(EffectStringUtil.getEffect(spicesTag.getString(s))).getEffect().getCategory().getTooltipFormatting())));
        }
        if (SPICE_EFFECTS_LOADER.getSpiceEffects().containsKey(ForgeRegistries.ITEMS.getKey(stack.getItem()))) {
            var spiceID = ForgeRegistries.ITEMS.getKey(stack.getItem());
            var effect = SPICE_EFFECTS_LOADER.getSpiceEffects().get(spiceID);
            event.getToolTip().add(1, Component.translatable("spiceshelf.when_applied").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(2, Component.literal(Component.translatable("spiceshelf.apply_effect").getString() + EffectStringUtil.effectToCoolerString(effect))
                    .withStyle(effect.getEffect().getCategory().getTooltipFormatting()));
        }
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(SPICE_EFFECTS_LOADER);
    }

    private static void playSoundFromID(String id, Level level, BlockPos pos) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(id));

        level.playSound(null, pos, Objects.requireNonNullElse(soundEvent, SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, 1, 1);
    }
}
