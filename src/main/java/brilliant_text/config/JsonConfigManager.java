package brilliant_text.config;

import brilliant_text.BrilliantText;
import brilliant_text.shader.*;
import brilliant_text.shader.builtin.IOutlinedTextShader;
import brilliant_text.util.ColorHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class JsonConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "brilliant_text_bindings.json";
    private static final Type mapType = new TypeToken<HashMap<Character, ShaderDefinition>>() {}.getType();

    private static File configFile;

    public static void init() {
        File configDir = new File(Minecraft.getMinecraft().gameDir, "config");
        JsonConfigManager.configFile = new File(configDir, FILE_NAME);

        if (!configFile.exists()) JsonConfigManager.createDefaultConfig();
        JsonConfigManager.loadConfig();
    }

    public static void loadConfig() {
        try (FileReader reader = new FileReader(configFile)) {
            HashMap<Character, ShaderDefinition> definitions = GSON.fromJson(
                    reader,
                    mapType
            );

            for (Map.Entry<Character, ShaderDefinition> entry : definitions.entrySet()) {
                ShaderDefinition definitionData = entry.getValue();
                definitionData.validateAndSanitize();

                char character = entry.getKey();

                class ConfigShader implements IOutlinedTextShader, IParticleSpawner {

                    /// Decides whether to spawn a particle in a single frame
                    ///
                    /// @param random An instance of random
                    /// @return Whether to spawn a particle
                    @Override
                    public boolean shouldSpawnParticle(@Nonnull Random random) {
                        if (definitionData.particleConfig == null) return false;
                        return random.nextInt(definitionData.particleConfig.rarity) == 0;
                    }

                    /// Build a new particle
                    ///
                    /// @return The particle that should be spawned
                    @Nonnull
                    @Override
                    public BrilliantParticle getNewParticle(@Nonnull BrilliantTextData data) {
                        assert definitionData.particleConfig != null;

                        Random rand = RandomInstance.getInstance();

                        return new BrilliantParticleBuilder(
                                definitionData.particleConfig.texture,
                                data.aabb.getRandomPositionInside()
                        )
                                .color(definitionData.particleConfig.color)
                                .lifetime(definitionData.particleConfig.lifetime)
                                .rotationsPerFrame((rand.nextFloat() * definitionData.particleConfig.rotationsPerFrame.max) + definitionData.particleConfig.rotationsPerFrame.min)
                                .rotation((rand.nextFloat() * definitionData.particleConfig.rotation.max) + definitionData.particleConfig.rotation.min)
                                .dimensions((int) ((rand.nextFloat() * definitionData.particleConfig.dimensions.max) + definitionData.particleConfig.dimensions.min))
                                .shouldShrink(definitionData.particleConfig.shouldShrink)
                                .build();
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getTextColors() {
                        NonNullList<Integer> colors = NonNullList.create();
                        colors.addAll(definitionData.textColors);
                        return colors;
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getOutlineColors() {
                        if (definitionData.outlineColors == null) return NonNullList.create();
                        NonNullList<Integer> colors = NonNullList.create();
                        colors.addAll(definitionData.outlineColors);
                        return colors;
                    }

                    @Nonnull
                    @Override
                    public Optional<Integer> getWiperColor() {
                        if (definitionData.wiperConfig == null) return Optional.empty();
                        if (definitionData.wiperConfig.color == null) return this.getTextColors()
                                .stream()
                                .findFirst()
                                .map(c -> ColorHelper.brighten(c, 1.5F));
                        return Optional.of(definitionData.wiperConfig.color);
                    }

                    @Override
                    public int getWiperSlowdown() {
                        if (definitionData.wiperConfig == null) return IOutlinedTextShader.super.getWiperSlowdown();
                        return definitionData.wiperConfig.slowdown;
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getGlowColors() {
                        if (definitionData.glowColors == null) return NonNullList.create();
                        NonNullList<Integer> colors = NonNullList.create();
                        colors.addAll(definitionData.glowColors);
                        return colors;
                    }
                }

                BrilliantTextManager.bindCharToShader(
                        FormatCharacter.tryFrom(character),
                        new ConfigShader()
                );
            }


        } catch (Exception e) {
            BrilliantText.LOGGER.error(e);
        }
    }

    private static void createDefaultConfig() {
        File file = new File(Minecraft.getMinecraft().gameDir, "config/" + FILE_NAME);

        HashMap<Character, ShaderDefinition> definitions = new HashMap<>();

        NonNullList<Integer> gOutlineColors = NonNullList.create();
        gOutlineColors.add(0xFFFCE670);
        gOutlineColors.add(0xFFFFEE9C);

        ShaderDefinition presetG = ShaderDefinition.builder()
                .textColors(NonNullList.withSize(1, 0xFF986B31))
                .outlineColors(gOutlineColors)
                .glowColors(NonNullList.withSize(1, 0xFFFCE670))
                .particleConfig(
                        ShaderDefinition.ParticleConfig.builder()
                                .color(0xFFFCE670)
                                .rarity(20)
                                .lifetime(60)
                                .dimensions(new NumberRange(4.0f, 6.0f))
                                .rotation(new NumberRange(1.0f, 360.0f))
                                .rotationsPerFrame(new NumberRange(0.0f, 1.0f))
                                .shouldShrink(true)
                                .build()
                )
                .wiperConfig(new ShaderDefinition.WiperConfig())
                .build();

        NonNullList<Integer> sOutlineColors = NonNullList.create();
        sOutlineColors.add(0xFFD5EAF8);
        sOutlineColors.add(0xFFF0F7FA);

        ShaderDefinition presetS = ShaderDefinition.builder()
                .textColors(NonNullList.withSize(1, 0xFF4C5E6F))
                .outlineColors(sOutlineColors)
                .particleConfig(
                        ShaderDefinition.ParticleConfig.builder()
                                .color(0xFFD5EAF8)
                                .rarity(20)
                                .lifetime(60)
                                .dimensions(new NumberRange(4.0f, 6.0f))
                                .rotation(new NumberRange(1.0f, 360.0f))
                                .rotationsPerFrame(new NumberRange(0.0f, 1.0f))
                                .shouldShrink(true)
                                .build()
                )
                .wiperConfig(new ShaderDefinition.WiperConfig())
                .build();

        ShaderDefinition presetH = ShaderDefinition.builder()
                .textColors(NonNullList.withSize(1, 0xFF0C3730))
                .outlineColors(NonNullList.withSize(1, 0xFF8CF4E2))
                .glowColors(NonNullList.withSize(1, 0xFF8CF4E2))
                .particleConfig(
                        ShaderDefinition.ParticleConfig.builder()
                                .texture(new ResourceLocation("minecraft", "textures/items/diamond.png"))
                                .color(0xAA8CF4E2)
                                .rarity(40)
                                .lifetime(60)
                                .dimensions(new NumberRange(4.0f, 6.0f))
                                .rotation(new NumberRange(0.0f, 45.0f))
                                .rotationsPerFrame(new NumberRange(0.0f, 0.0f))
                                .shouldShrink(false)
                                .build())
                .wiperConfig(ShaderDefinition.WiperConfig.builder().build())
                .build();

        ShaderDefinition presetQ = ShaderDefinition.builder()
                .textColors(NonNullList.withSize(1, 0xFF60241E))
                .outlineColors(NonNullList.withSize(1, 0xFFE77B49))
                .build();

        definitions.put('h', presetH);
        definitions.put('g', presetG);
        definitions.put('s', presetS);
        definitions.put('q', presetQ);

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(definitions, writer);
            BrilliantText.LOGGER.info("Created default configuration");
        } catch (IOException e) {
            BrilliantText.LOGGER.error(e);
        }
    }
}
