package brilliant_text.shader;

import brilliant_text.BrilliantText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

@SideOnly(Side.CLIENT)
public interface IParticleSpawner {
    ResourceLocation GLOW_PARTICLE_TEXTURE_1 = new ResourceLocation(
            BrilliantText.MODID,
            "textures/particles/glow.png"
    );

    ResourceLocation GLOW_PARTICLE_TEXTURE_2 = new ResourceLocation(
            BrilliantText.MODID,
            "textures/particles/glow_2.png"
    );

    /// Decides whether to spawn a particle in a single frame
    ///
    /// @param random An instance of random
    /// @return Whether to spawn a particle
    boolean shouldSpawnParticle(@Nonnull Random random);

    /// Build a new particle
    ///
    /// @param data The text data
    /// @return The particle that should be spawned
    @Nonnull
    BrilliantParticle getNewParticle(@Nonnull BrilliantTextData data);
}
