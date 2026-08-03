package brilliant_text.handlers;

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
        public boolean DEBUG = false;

        @Config.Name("Change Vanilla item formatting")
        @Config.Comment({"Should certain vanilla items receive the shader effect?"})
        public boolean CHANGE_VANILLA_ITEM_FORMATTING = true;

        @Config.Name("Vanilla item formatting mapping")
        @Config.Comment({"The list which decides which vanilla items are mapped to which shader"})
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

        @Config.Comment({
                "Character to Shader Mappings.",
                "Data Types: (hex) A color code in the ARGB format; (number-number) A random value between the first and second number; (ResourceLocation) A string pointing to a texture; (boolean) true or false",
                "Format: char=textColor (hex)|outlineColor (hex)|glowColor (hex)|particleTexture (ResourceLocation)|particleColor (hex)|particleRarity (number)|particleLifetime (number)|particleDimensions (number-number)|particleRotation (number-number)|particleRotationPerFrame (number-number)|shouldShrink (boolean)",
                "Example: g=FF986B31|FFFCE670|FFFCE670|brilliant_text:textures/particles/glow.png|FFFCE670|100|200|2-4|1-360|0-1|true"
        })
        @Config.Name("Shader Bindings")
        public String[] CHARACTER_BINDINGS = new String[]{
                "g=FF986B31|FFFCE670|FFFCE670|brilliant_text:textures/particles/glow.png|FFFCE670|100|200|2-4|1-360|0-1|true",
                "s=FF4C5E6F|FFD5EAF8|00000000|brilliant_text:textures/particles/glow.png|FFD5EAF8|100|200|2-4|1-360|0-1|true",
                "h=FF0C3730|FF8CF4E2|FF8CF4E2|minecraft:textures/items/diamond.png|AA8CF4E2|150|400|3-4|0-45|0-0|false",
                "q=FF60241E|FFE77B49|00000000",
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