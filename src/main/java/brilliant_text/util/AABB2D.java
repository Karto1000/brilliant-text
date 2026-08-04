package brilliant_text.util;

import brilliant_text.shader.RandomInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec2f;

import java.util.Random;

public class AABB2D {
    public final float x;
    public final float y;
    public final int width;
    public final int height;

    public AABB2D(float x, float y, int width, int height) {
        if (width < 0) throw new IllegalArgumentException("width cannot be negative");
        if (height < 0) throw new IllegalArgumentException("height cannot be negative");

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
        Random rand = RandomInstance.getInstance();
        int randX = this.width > 0 ? rand.nextInt(this.width) : 0;
        int randY = this.height > 0 ? rand.nextInt(this.height) : 0;

        return new Vec2f(
                this.x + randX,
                this.y - ((float) this.height / 2) + randY
        );
    }
}
