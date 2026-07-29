package brilliant_text.handlers;

import brilliant_text.BrilliantText;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BrilliantText.MODID)
public class ModRegistry {
    public static TestItem GOLD_TEST = new TestItem("gold_item");
    public static TestItem SILVER_TEST = new TestItem("silver_item");
    public static TestItem COPPER_TEST = new TestItem("copper_test");
    public static TestItem BURNING_TEST = new TestItem("burning_test");

    public static void init() {

    }

    @SubscribeEvent
    public static void registerItemEvent(RegistryEvent.Register<Item> event) {
        if (ForgeConfigHandler.client.DEBUG) {
            event.getRegistry().registerAll(
                    GOLD_TEST,
                    SILVER_TEST,
                    COPPER_TEST,
                    BURNING_TEST
            );
        }
    }
}