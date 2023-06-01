package qinomed.spiceshelf.event;

import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import qinomed.spiceshelf.SpiceShelf;
import qinomed.spiceshelf.data.SpiceConversionLoader;
import qinomed.spiceshelf.data.SpiceEffectsLoader;
import qinomed.spiceshelf.util.EffectStringUtil;
import qinomed.spiceshelf.util.SpiceShelfTags;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SpiceShelf.MODID)
public class SpiceShelfEvents {
    public static final SpiceEffectsLoader SPICE_EFFECTS_LOADER = new SpiceEffectsLoader();
    public static final SpiceConversionLoader SPICE_CONVERSIONS_LOADER = new SpiceConversionLoader();

    @SubscribeEvent
    public static void applySpice(ItemStackedOnOtherEvent event) {
        if (event.getClickAction() == ClickAction.SECONDARY && !event.getPlayer().getLevel().isClientSide()) {
            Map<ResourceLocation, MobEffectInstance> spiceEffects = SPICE_EFFECTS_LOADER.getSpiceEffects();
            Multimap<ResourceLocation, ResourceLocation> spiceConversions = SPICE_CONVERSIONS_LOADER.getSpiceConversions();
            ItemStack stackedOn = event.getStackedOnItem();
            ItemStack carriedItem = event.getCarriedItem();
            ResourceLocation spiceID = ForgeRegistries.ITEMS.getKey(carriedItem.getItem());
            ResourceLocation itemID = ForgeRegistries.ITEMS.getKey(stackedOn.getItem());
            Player player = event.getPlayer();

            var value = spiceConversions.get(itemID).stream().toList();
            if (value != null) {
                if (spiceConversions.containsKey(itemID) && ForgeRegistries.ITEMS.getValue(value.get(0)) == carriedItem.getItem()) {
                    event.setCanceled(true);
                    var spice = ForgeRegistries.ITEMS.getValue(value.get(0));
                    var result = ForgeRegistries.ITEMS.getValue(value.get(1));
                    if (carriedItem.getItem() == spice) {
                        var resultStack = new ItemStack(result);
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
                    }
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
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        var tag = stack.getOrCreateTag();
        if (tag.contains("spices")) {
            var spices = tag.getCompound("spices").getAllKeys().stream().toList();
            spices.forEach(s -> event.getToolTip().add(spices.indexOf(s)+1, Component.translatable("spice." + StringUtils.replace(s, ":", ".")).withStyle(ChatFormatting.GRAY)));
        }
        if (SPICE_EFFECTS_LOADER.getSpiceEffects().containsKey(ForgeRegistries.ITEMS.getKey(stack.getItem()))) {
            event.getToolTip().add(1, Component.translatable("spiceshelf.applicable_to_food").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(SPICE_EFFECTS_LOADER);
        event.addListener(SPICE_CONVERSIONS_LOADER);
    }
}
