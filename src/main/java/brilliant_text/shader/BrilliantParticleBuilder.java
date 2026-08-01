package brilliant_text.shader;

import mezz.jei.util.FieldsAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class BrilliantParticleBuilder {
    private final Vec2f pos;
    private final ResourceLocation texture;
    private int color = 0xFFFFFFFF;
    private int lifetime = 80;
    private int dimensions = 8;
    private float rotationsPerFrame = 0;
    private float rotation = 0;

    public BrilliantParticleBuilder(ResourceLocation texture, Vec2f pos) {
        this.texture = texture;
        this.pos = pos;
    }

    public BrilliantParticleBuilder color(int color) {
        this.color = color;
        return this;
    }

    public BrilliantParticleBuilder lifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public BrilliantParticleBuilder dimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public BrilliantParticleBuilder rotationsPerFrame(float rotationsPerFrame) {
        this.rotationsPerFrame = rotationsPerFrame;
        return this;
    }

    public BrilliantParticleBuilder rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public BrilliantParticle build() {
        return new BrilliantParticle(
                this.pos.x,
                this.pos.y,
                this.lifetime,
                this.color,
                this.lifetime,
                this.dimensions,
                this.rotationsPerFrame,
                this.rotation,
                this.texture
        );
    }
}
