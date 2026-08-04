package brilliant_text.shader;

import mezz.jei.util.FieldsAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/// A class that holds information about Particles spawned by brilliant text
/// This class can be instantiated using the `BrilliantParticleBuilder`
@FieldsAreNonnullByDefault
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

    protected BrilliantParticle(
            float x,
            float y,
            float currentLifetime,
            int color,
            int maxLifetime,
            int dimensions,
            float rotationsPerFrame,
            float rotation,
            @Nonnull ResourceLocation texture,
            boolean shouldShrink
    ) {
        if (currentLifetime < 1) throw new IllegalArgumentException("currentLifetime cannot be smaller than 1");
        if (maxLifetime < 1) throw new IllegalArgumentException("maxLifetime cannot be smaller than 1");
        if (dimensions < 1) throw new IllegalArgumentException("dimensions cannot be smaller than 1");

        this.x = x;
        this.y = y;
        this.currentLifetime = currentLifetime;
        this.color = color;
        this.maxLifetime = maxLifetime;
        this.dimensions = dimensions;
        this.rotationsPerFrame = rotationsPerFrame;
        this.rotation = rotation;
        this.texture = texture;
        this.shouldShrink = shouldShrink;
    }

    public void onRenderTick() {
    }
}
