package qinomed.spiceshelf.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import qinomed.spiceshelf.SpiceShelf;

public class EffectStringUtil {
    public static MobEffectInstance getEffect(String s) {
        try {
            var effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(s.substring(0, s.indexOf(" "))));
            var length = Integer.parseInt(s.substring(s.indexOf(" ")+1, StringUtils.ordinalIndexOf(s, " ", 2)));
            var amplifier = Integer.parseInt(s.substring(StringUtils.ordinalIndexOf(s, " ", 2)+1, StringUtils.ordinalIndexOf(s, " ", 3)));
            var hideParticles = Boolean.parseBoolean(s.substring(StringUtils.ordinalIndexOf(s, " ", 3)));
            return new MobEffectInstance(effect, length*20, amplifier, false, hideParticles);
        } catch (NullPointerException e) {
            SpiceShelf.LOGGER.error("Invalid mob effect");
        }
        return null;
    }

    public static MobEffectInstance[] getAllEffects(String[] arr) {
        MobEffectInstance[] ret = new MobEffectInstance[arr.length];
        for (int i = 0; i < arr.length; i++) {
            var s = arr[i];
            ret[i] = getEffect(s);
        }
        return ret;
    }

    public static String effectToString(MobEffectInstance effect) {
        return String.join(" ", ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString(), Integer.toString(effect.getDuration()/20), Integer.toString(effect.getAmplifier()), Boolean.toString(effect.isVisible()));
    }

    public static String effectToCoolerString(MobEffectInstance effect) {
        var s = effect.getEffect().getDisplayName().getString();
        if (effect.getAmplifier() > 0)
            s = s.concat(" " + Component.translatable("potion.potency." + effect.getAmplifier()).getString());
        if (effect.getDuration() > 20)
            s = s.concat(" (" + MobEffectUtil.formatDuration(effect, 1) + ")");

        return s;
    }
}
