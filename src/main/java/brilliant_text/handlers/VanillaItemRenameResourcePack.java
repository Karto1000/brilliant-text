package brilliant_text.handlers;

import brilliant_text.BrilliantText;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.ReloadRequirements;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class VanillaItemRenameResourcePack implements IResourcePack {
    private static final String DEFAULT_PATH = "lang/en_us.lang";

    public static void init() {
        try {
            // "field_110449_ao" is the SRG name for 'defaultResourcePacks' in 1.12.2
            List<IResourcePack> defaultPacks = ObfuscationReflectionHelper.getPrivateValue(
                    Minecraft.class,
                    Minecraft.getMinecraft(),
                    "field_110449_ao"
            );

            // Add our custom pack to the list
            defaultPacks.add(new VanillaItemRenameResourcePack());

            FMLClientHandler.instance().refreshResources(ReloadRequirements.all());
        } catch (Exception e) {
            BrilliantText.LOGGER.error(e);
        }
    }

    private boolean isTargetFile(ResourceLocation location) {
        return location.getNamespace().equals(BrilliantText.MODID) && location.getPath().equals(DEFAULT_PATH);
    }

    private boolean isCorrectResource(ResourceLocation location) {
        return this.isTargetFile(location) && ForgeConfigHandler.client.CHANGE_VANILLA_ITEM_FORMATTING;
    }

    private String getFileContent() {
        if (!ForgeConfigHandler.client.CHANGE_VANILLA_ITEM_FORMATTING) return "";

        StringBuilder sb = new StringBuilder();
        for (String b : ForgeConfigHandler.client.VANILLA_ITEM_FORMATTING_BINDING) {
            String[] split = b.split("=");
            if (split.length < 2) continue;

            String translationKey = split[0];
            String shaderChar = split[1];
            if (translationKey.isEmpty() || shaderChar.isEmpty()) continue;

            String text = I18n.format(split[0]);
            if (text.isEmpty()) continue;

            sb.append(translationKey).append("=§").append(shaderChar).append(text).append("\n");
        }

        return sb.toString();
    }

    @Override
    @Nonnull
    public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        if (this.isCorrectResource(location)) {
            String langFileContent = this.getFileContent();
            return new ByteArrayInputStream(langFileContent.getBytes(StandardCharsets.UTF_8));
        }

        throw new FileNotFoundException(location.toString());
    }

    @Override
    public boolean resourceExists(@Nonnull ResourceLocation location) {
        return this.isCorrectResource(location);
    }

    @Override
    @Nonnull
    public Set<String> getResourceDomains() {
        return ImmutableSet.of(BrilliantText.MODID);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(
            @Nonnull MetadataSerializer metadataSerializer,
            @Nonnull String metadataSectionName
    ) throws IOException {
        return null;
    }

    @Override
    @Nonnull
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    @Nonnull
    public String getPackName() {
        return "Brilliant Text Replace Vanilla Text";
    }
}
