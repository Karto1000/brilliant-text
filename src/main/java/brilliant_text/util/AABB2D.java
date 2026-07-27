package brilliant_text.util;

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
}
