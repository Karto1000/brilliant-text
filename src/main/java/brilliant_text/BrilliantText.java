package brilliant_text;

import brilliant_text.handlers.ModRegistry;
import brilliant_text.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = BrilliantText.MODID, version = BrilliantText.VERSION, name = BrilliantText.NAME, dependencies = "required-after:fermiumbooter")
public class BrilliantText {
    public static final String MODID = "brilliant_text";
    public static final String VERSION = "1.0.1";
    public static final String NAME = "Brilliant Text";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;

    @SidedProxy(clientSide = "brilliant_text.proxy.ClientProxy", serverSide = "brilliant_text.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Instance(MODID)
    public static BrilliantText instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModRegistry.init();
        BrilliantText.PROXY.preInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        completedLoading = true;
        BrilliantText.PROXY.postInit();
    }
}