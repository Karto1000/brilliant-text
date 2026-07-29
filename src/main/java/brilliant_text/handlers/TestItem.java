package brilliant_text.handlers;

import brilliant_text.BrilliantText;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;

public class TestItem extends Item {
    private final EnumRarity rarity;

    public TestItem(String id, EnumRarity rarity) {
        this.setRegistryName(BrilliantText.MODID,id);
        this.setTranslationKey(String.format("brilliant_text.%s", id));
        this.rarity = rarity;
    }

    public TestItem(String id) {
        this.setRegistryName(BrilliantText.MODID,id);
        this.setTranslationKey(String.format("brilliant_text.%s", id));
        this.rarity = EnumRarity.COMMON;
    }

    @Override
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return this.rarity;
    }
}

