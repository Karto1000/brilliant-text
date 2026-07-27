package brilliant_text.shader.builtin;

import lombok.Builder;
import net.minecraft.util.ResourceLocation;

@Builder
public class ParticleSettings {
    public final int color;
    public final int maxLifetime;
    public final int particleEveryXFrames;
    public final int dimensions;
    public final float rotationPerFrame;
    public final float startingRotationDegrees;
    public final ResourceLocation resourceLocation;
}
