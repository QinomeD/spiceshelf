package qinomed.spiceshelf;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import qinomed.spiceshelf.recipe.SpiceShelfRecipes;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpiceShelf.MODID)
public class SpiceShelf {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "spiceshelf";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpiceShelf() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SpiceShelfRecipes.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
