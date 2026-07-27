package brilliant_text.shader.builtin;

import brilliant_text.BrilliantText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class GoldShader implements IOutlinedTextShader {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(
            BrilliantText.MODID,
            "textures/particles/glow.png"
    );

    @Override
    public Integer getTextColor() {
        return 0xFF986B31;
    }

    @Override
    public Optional<Integer> getGlowColor() {
        return this.getOutlineColor();
    }

    @Override
    public Optional<Integer> getOutlineColor() {
        return Optional.of(0xFFFCE670);
    }

    @Override
    public Optional<ParticleSettings> getSettingsForNewParticle() {
        Minecraft mc = Minecraft.getMinecraft();
        return Optional.of(
                ParticleSettings.builder()
                        .resourceLocation(PARTICLE_TEXTURE)
                        .color(this.getOutlineColor().get())
                        .maxLifetime(200)
                        .particleEveryXFrames(100)
                        .startingRotationDegrees(mc.world.rand.nextInt(360))
                        .rotationPerFrame(mc.world.rand.nextFloat())
                        .dimensions(mc.world.rand.nextInt(4) + 2)
                        .build()
        );
    }
}
