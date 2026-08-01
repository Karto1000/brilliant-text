package brilliant_text.handlers;

import brilliant_text.BrilliantText;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TestItem extends Item {
    private final EnumRarity rarity;
    private final String registryId;

    public TestItem(String id, EnumRarity rarity) {
        this.setRegistryName(BrilliantText.MODID, id);
        this.setTranslationKey(String.format("brilliant_text.%s", id));
        this.registryId = id;
        this.rarity = rarity;
    }

    public TestItem(String id) {
        this.setRegistryName(BrilliantText.MODID, id);
        this.setTranslationKey(String.format("brilliant_text.%s", id));
        this.registryId = id;
        this.rarity = EnumRarity.COMMON;
    }

    @Override
    @Nonnull
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return this.rarity;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!I18n.hasKey(String.format("tooltip.brilliant_text.%s", registryId))) return;
        tooltip.add(I18n.format(String.format("tooltip.brilliant_text.%s", registryId)));
    }
}

