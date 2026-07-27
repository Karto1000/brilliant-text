package brilliant_text.util;

public class ColorHelper {
    public static class ARGBNorm {
        public float a;
        public float r;
        public float g;
        public float b;

        public ARGBNorm(float a, float r, float g, float b) {
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public static ARGBNorm hexToARGBNorm(int color) {
        float a = (float) (color >> 24 & 255) / 255;
        float r = (float) (color >> 16 & 255) / 255;
        float g = (float) (color >> 8 & 255) / 255;
        float b = (float) (color & 255) / 255;
        return new ARGBNorm(a, r, g, b);
    }
}
