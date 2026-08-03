package brilliant_text.shader;

import brilliant_text.BrilliantText;
import brilliant_text.handlers.ForgeConfigHandler;
import brilliant_text.shader.builtin.FlameTextShader;
import brilliant_text.shader.builtin.IOutlinedTextShader;
import brilliant_text.util.ColorHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class BrilliantTextManager {
    @Getter
    private final static HashMap<FormatCharacter, ITextShader> shaders = new HashMap<>();

    public final static char FORMAT_CHARACTER_PREFIX = '§';
    public static final ResourceLocation DEFAULT_VERTEX_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/outline.vert"
    );
    public static final ResourceLocation DEFAULT_FRAGMENT_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/outline.frag"
    );

    public static final ResourceLocation FLAME_VERTEX_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/flame.vert"
    );

    public static final ResourceLocation FLAME_FRAGMENT_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/flame.frag"
    );

    public static final String DEFAULT_SHADER_DESIGNATION = "default";
    public static final String FLAME_SHADER_DESIGNATION = "flame";

    public static void init() {
        BrilliantShaderManager.registerShader(
                DEFAULT_SHADER_DESIGNATION,
                DEFAULT_FRAGMENT_SHADER,
                DEFAULT_VERTEX_SHADER
        );
        BrilliantShaderManager.registerShader(
                FLAME_SHADER_DESIGNATION,
                FLAME_FRAGMENT_SHADER,
                FLAME_VERTEX_SHADER
        );

        BrilliantTextManager.loadBindingsFromConfig();
        BrilliantTextManager.bindCharToShader(FormatCharacter.tryFrom('v'), new FlameTextShader());
    }

    private static int[] readRangeValue(String range) {
        int lower;
        int upper;

        if (range != null) {
            String[] split = range.split("-");
            if (split.length == 2) {
                lower = Integer.parseInt(split[0].trim());
                upper = Integer.parseInt(split[1].trim());
            } else {
                lower = 0;
                upper = 0;
            }
        } else {
            lower = 0;
            upper = 0;
        }

        return new int[]{lower, upper};
    }

    private static void loadBindingsFromConfig() {
        for (String entry : ForgeConfigHandler.client.CHARACTER_BINDINGS) {
            try {
                String[] parts = entry.split("=");
                if (parts.length != 2) continue;

                char charCode = parts[0].trim().charAt(0);
                String[] params = parts[1].split("\\|");
                if (params.length < 1) continue;

                Integer textColor = Integer.parseUnsignedInt(params[0].trim(), 16);
                Integer outlineColor = params.length > 1 ? Integer.parseUnsignedInt(params[1].trim(), 16) : null;
                Integer glowColor = params.length > 2 ? Integer.parseUnsignedInt(params[2].trim(), 16) : null;
                ResourceLocation particleTextureLocation = params.length > 3 ? new ResourceLocation(params[3]) : null;
                int particleColor = params.length > 4 ? Integer.parseUnsignedInt(params[4].trim(), 16) : 0x00000000;
                int particleRarity = params.length > 5 ? Integer.parseInt(params[5].trim()) : 100;
                int particleLifetime = params.length > 6 ? Integer.parseInt(params[6].trim()) : 200;

                String particleDimensionsStr = params.length > 7 ? params[7].trim() : null;
                int[] particleDimensions = readRangeValue(particleDimensionsStr);

                String particleRotationStr = params.length > 8 ? params[8].trim() : null;
                int[] particleRotations = readRangeValue(particleRotationStr);

                String particleRotationsPerFrameStr = params.length > 9 ? params[9].trim() : null;
                int[] particleRotationsPerFrame = readRangeValue(particleRotationsPerFrameStr);

                boolean shouldShrink = params.length > 10 && Boolean.parseBoolean(params[10].trim());

                class ConfigShader implements IOutlinedTextShader, IParticleSpawner {

                    /// Decides whether to spawn a particle in a single frame
                    ///
                    /// @param random An instance of random
                    /// @return Whether to spawn a particle
                    @Override
                    public boolean shouldSpawnParticle(@Nonnull Random random) {
                        if (particleTextureLocation == null) return false;
                        return random.nextInt(particleRarity) == 0;
                    }

                    /// Build a new particle
                    ///
                    /// @return The particle that should be spawned
                    @Nonnull
                    @Override
                    public BrilliantParticle getNewParticle(@Nonnull BrilliantTextData data) {
                        assert particleTextureLocation != null;
                        Minecraft mc = Minecraft.getMinecraft();
                        return new BrilliantParticleBuilder(
                                particleTextureLocation,
                                data.aabb.getRandomPositionInside()
                        )
                                .color(particleColor)
                                .lifetime(particleLifetime)
                                .rotationsPerFrame((mc.world.rand.nextFloat() * particleRotationsPerFrame[1]) + particleRotationsPerFrame[0])
                                .rotation((mc.world.rand.nextFloat() * particleRotations[1]) + particleRotations[0])
                                .dimensions((int) ((mc.world.rand.nextFloat() * particleDimensions[1]) + particleDimensions[0]))
                                .shouldShrink(shouldShrink)
                                .build();
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getTextColors() {
                        NonNullList<Integer> colors = NonNullList.create();
                        colors.add(textColor);
                        return colors;
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getOutlineColors() {
                        if (outlineColor == null) return NonNullList.create();

                        NonNullList<Integer> colors = NonNullList.create();
                        colors.add(outlineColor);
                        colors.add(ColorHelper.brighten(outlineColor, .6F));

                        return colors;
                    }

                    @Nonnull
                    @Override
                    public NonNullList<Integer> getGlowColors() {
                        if (glowColor == null) return NonNullList.create();
                        return NonNullList.withSize(1, glowColor);
                    }
                }

                FormatCharacter character = FormatCharacter.tryFrom(charCode);
                BrilliantTextManager.bindCharToShader(
                        character,
                        new ConfigShader()
                );
            } catch (Exception e) {
                BrilliantText.LOGGER.error(e);
            }
        }
    }

    public static void bindCharToShader(FormatCharacter character, ITextShader shader) {
        shaders.put(character, shader);
    }

    public static Optional<ITextShader> getShader(FormatCharacter character) {
        ITextShader shader = shaders.get(character);
        return Optional.ofNullable(shader);
    }
}
