package brilliant_text.shader.builtin;

import brilliant_text.shader.BrilliantParticle;
import brilliant_text.shader.IParticleSpawner;
import brilliant_text.shader.RandomInstance;
import net.minecraft.util.math.Vec2f;

import java.util.Random;

import static net.minecraft.util.math.MathHelper.clamp;

public class FlameParticle extends BrilliantParticle {
    public final Vec2f velocity;
    public final int originalColor;
    public final float reducedLifetime;

    public FlameParticle(Vec2f position) {
        super(
                position.x,
                position.y,
                1,
                0xFFCC5500,
                1,
                2,
                1F,
                0,
                IParticleSpawner.GLOW_PARTICLE_TEXTURE_2,
                false
        );

        Random rand = RandomInstance.getInstance();

        int lifetime = rand.nextInt(20) + 10;
        this.color = rand.nextInt(2) == 0 ? 0xFFFF4433 : 0xFFCC5500;
        this.maxLifetime = lifetime;
        this.currentLifetime = lifetime;
        this.dimensions = rand.nextInt(4) + 1;
        this.velocity = new Vec2f((float) ((Math.random() - 0.5) * 0.025), -0.025F);
        this.originalColor = this.color;
        this.reducedLifetime = rand.nextInt(20);
    }

    @Override
    public void onRenderTick() {
        float delta = clamp((this.currentLifetime - this.reducedLifetime) / this.maxLifetime, 0, 1);
        int r = (int) ((float) ((this.originalColor >> 16) & 0xFF) * delta);
        int g = (int) ((float) ((this.originalColor >> 8) & 0xFF) * delta);
        int b = (int) ((float) (this.originalColor & 0xFF) * delta);
        this.color = 0xFF000000 + (r << 16) + (g << 8) + b;

        this.x += this.velocity.x;
        this.y += this.velocity.y;
    }
}
