package brilliant_text.config;

import brilliant_text.BrilliantText;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = BrilliantText.MODID)
public class ForgeConfigHandler {

    @Config.Comment("Options")
    @Config.Name("Options")
    public static final ClientConfig client = new ClientConfig();

    public static class ClientConfig {
        @Config.Name("Debug Mode")
        @Config.Comment("Will add debug items with all the shader effects applied to them")
        public boolean DEBUG = false;

        @Config.Name("Change Vanilla item formatting")
        @Config.Comment("Should certain vanilla items receive the shader effect?")
        public boolean CHANGE_VANILLA_ITEM_FORMATTING = false;

        @Config.Name("Vanilla item formatting mapping")
        @Config.Comment("The list that decides which vanilla items are mapped to which shader")
        public String[] VANILLA_ITEM_FORMATTING_BINDING = new String[]{
                "item.diamond.name=h",
                "item.swordDiamond.name=h",
                "item.shovelDiamond.name=h",
                "item.pickaxeDiamond.name=h",
                "item.hatchetDiamond.name=h",
                "item.hoeDiamond.name=h",
                "item.helmetDiamond.name=h",
                "item.chestplateDiamond.name=h",
                "item.leggingsDiamond.name=h",
                "item.bootsDiamond.name=h",
                "item.horsearmordiamond.name=h",
                "tile.oreDiamond.name=h",
                "tile.blockDiamond.name=h",

                "item.appleGold.name=g",
                "item.carrotGolden.name=g",
                "tile.beacon.name=g",
                "item.netherStar.name=g",

                "item.blazeRod.name=v",
                "item.blazePowder.name=v",
        };
    }

    @Mod.EventBusSubscriber(modid = BrilliantText.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(BrilliantText.MODID)) {
                ConfigManager.sync(BrilliantText.MODID, Config.Type.INSTANCE);
            }
        }
    }
}