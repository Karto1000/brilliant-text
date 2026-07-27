package brilliant_text.handlers;

import brilliant_text.BrilliantText;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BrilliantText.MODID)
public class ModRegistry {
//    public static TestItem TEST_ITEM = new TestItem("test_item", EnumRarity.RARE);

    public static void init() {

    }

    @SubscribeEvent
    public static void registerItemEvent(RegistryEvent.Register<Item> event) {
//        event.getRegistry().register(TEST_ITEM);
    }

    @SubscribeEvent
    public static void registerRecipeEvent(RegistryEvent.Register<IRecipe> event) {
    }

    @SubscribeEvent
    public static void registerPotionEvent(RegistryEvent.Register<Potion> event) {
    }

    @SubscribeEvent
    public static void registerPotionTypeEvent(RegistryEvent.Register<PotionType> event) {
    }
}