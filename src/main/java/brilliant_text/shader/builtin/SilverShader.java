package brilliant_text.shader.builtin;

import brilliant_text.BrilliantText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class SilverShader implements IOutlinedTextShader {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(
            BrilliantText.MODID,
            "textures/particles/glow.png"
    );

    @Override
    public Integer getTextColor() {
        return 0xFF4C5E6F;
    }

    @Override
    public Optional<Integer> getOutlineColor() {
        return Optional.of(0xFFD5EAF8);
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
