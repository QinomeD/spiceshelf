package qinomed.spiceshelf.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import qinomed.spiceshelf.SpiceShelf;

public class SpiceShelfTags {
    public static final TagKey<Item> SPICE_BLACKLIST = ItemTags.create(new ResourceLocation(SpiceShelf.MODID, "spice_blacklist"));
}
