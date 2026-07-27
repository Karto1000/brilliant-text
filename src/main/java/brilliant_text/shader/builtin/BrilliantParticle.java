package brilliant_text.shader.builtin;

import net.minecraft.util.math.Vec2f;

public class BrilliantParticle {
    public final Vec2f pos;
    public float currentLifetime;
    public float currentRotation;
    public ParticleSettings settings;

    public BrilliantParticle(
            Vec2f pos,
            ParticleSettings settings
    ) {
        this.pos = pos;
        this.settings = settings;
        this.currentLifetime = settings.maxLifetime;
        this.currentRotation = settings.startingRotationDegrees;
    }
}
