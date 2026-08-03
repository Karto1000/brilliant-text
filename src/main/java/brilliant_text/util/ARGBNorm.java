package brilliant_text.util;

import lombok.ToString;

@ToString
public class ARGBNorm {
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
