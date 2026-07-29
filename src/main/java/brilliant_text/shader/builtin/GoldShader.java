package brilliant_text.shader.builtin;

import brilliant_text.shader.BrilliantTextData;
import brilliant_text.shader.IParticleSpawner;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Random;

public class GoldShader implements IOutlinedTextShader, IParticleSpawner {
    @Override
    public int getTextColor() {
        return 0xFF986B31;
    }

    @Nonnull
    @Override
    public Optional<Integer> getGlowColor() {
        return this.getOutlineColor();
    }

    @Override
    public @Nonnull Optional<Integer> getOutlineColor() {
        return Optional.of(0xFFFCE670);
    }

    @Override
    public boolean shouldSpawnParticle(@Nonnull Random random) {
        return random.nextInt(100) == 0;
    }

    @Override
    public @Nonnull BrilliantParticle getNewParticle(@Nonnull BrilliantTextData data) {
        Minecraft mc = Minecraft.getMinecraft();
        int color = this.getOutlineColor().orElseThrow(RuntimeException::new);

        return new BrilliantParticleBuilder(GLOW_PARTICLE_TEXTURE_1, data.aabb.getRandomPositionInside())
                .color(color)
                .lifetime(200)
                .rotation(mc.world.rand.nextInt(360))
                .rotationsPerFrame(mc.world.rand.nextFloat())
                .dimensions(mc.world.rand.nextInt(4) + 2)
                .build();
    }
}
