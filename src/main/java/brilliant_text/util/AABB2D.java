package brilliant_text.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec2f;

public class AABB2D {
    public final float x;
    public final float y;
    public final int width;
    public final int height;

    public AABB2D(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getMaxX() {
        return this.x + this.width;
    }

    public float getMaxY() {
        return this.y + this.height;
    }

    public float getMinX() {
        return this.x;
    }

    public float getMinY() {
        return this.y;
    }

    public Vec2f getRandomPositionInside() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Vec2f(
                this.x + mc.world.rand.nextInt(this.width),
                this.y - ((float) this.height / 2) + mc.world.rand.nextInt(this.height)
        );
    }
}
