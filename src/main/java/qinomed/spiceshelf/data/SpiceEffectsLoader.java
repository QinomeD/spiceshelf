package qinomed.spiceshelf.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import qinomed.spiceshelf.util.EffectStringUtil;

import java.util.HashMap;
import java.util.Map;

public class SpiceEffectsLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
    private Map<ResourceLocation, MobEffectInstance> spiceEffects = new HashMap<>();

    public static class SpiceEffect {
        public ResourceLocation item;
        public String effect;

        public SpiceEffect(ResourceLocation item, String effect) {
            this.item = item;
            this.effect = effect;
        }
    }

    public SpiceEffectsLoader() {
        super(GSON, "spice_effects");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.spiceEffects.clear();
        jsonMap.forEach((location, spiceEffect) -> {
            var effect = GSON.fromJson(spiceEffect, SpiceEffect.class);
            spiceEffects.put(effect.item, EffectStringUtil.getEffect(effect.effect));
        });
    }

    public Map<ResourceLocation, MobEffectInstance> getSpiceEffects() {
        return spiceEffects;
    }

    @Override
    public String getName() {
        return "SpiceEffectsLoader";
    }
}
