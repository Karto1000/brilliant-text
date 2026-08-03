package brilliant_text.shader;

import lombok.AllArgsConstructor;
import mezz.jei.util.FieldsAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;

@FieldsAreNonnullByDefault
@AllArgsConstructor
public class BrilliantParticle {
    /// The current position
    public float x;
    public float y;
    /// The current lifetime
    public float currentLifetime;
    /// The color of the particle
    public int color;
    /// How many frames the particle will be visible
    public int maxLifetime;
    /// The 2d dimensions of the particle
    public int dimensions;
    /// The amount the particle rotates per frame
    public float rotationsPerFrame;
    /// The initial rotation of the particle
    public float rotation;
    /// The texture of the particle
    public ResourceLocation texture;
    /// If the particle should shrink during its lifetime
    public boolean shouldShrink;

    public void onRenderTick() {};
}
