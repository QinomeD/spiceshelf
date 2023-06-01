package qinomed.spiceshelf.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class SpiceConversionLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();
    private Multimap<ResourceLocation, ResourceLocation> spiceConversions = ArrayListMultimap.create();

    public static class SpiceConversion {
        public ResourceLocation item;
        public ResourceLocation spice;
        public ResourceLocation result;

        public SpiceConversion(ResourceLocation item, ResourceLocation spice, ResourceLocation result) {
            this.item = item;
            this.spice = spice;
            this.result = result;
        }
    }

    public SpiceConversionLoader() {
        super(GSON, "spice_conversions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.spiceConversions.clear();
        jsonMap.forEach((location, spiceConversion) -> {
            var conversion = GSON.fromJson(spiceConversion, SpiceConversion.class);
            spiceConversions.put(conversion.item, conversion.spice);
            spiceConversions.put(conversion.item, conversion.result);
        });
    }

    public Multimap<ResourceLocation, ResourceLocation> getSpiceConversions() {
        return spiceConversions;
    }

    @Override
    public String getName() {
        return "SpiceConversionsLoader";
    }
}
