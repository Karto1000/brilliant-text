package brilliant_text.util;

import net.minecraft.util.NonNullList;

import java.time.Duration;

public class ColorHelper {

    public static ARGBNorm hexToARGBNorm(int color) {
        float a = (float) (color >> 24 & 255) / 255;
        float r = (float) (color >> 16 & 255) / 255;
        float g = (float) (color >> 8 & 255) / 255;
        float b = (float) (color & 255) / 255;
        return new ARGBNorm(a, r, g, b);
    }

    public static int mix(int color1, int color2, float ratio) {
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = Math.round(a1 * (1 - ratio) + a2 * ratio);
        int r = Math.round(r1 * (1 - ratio) + r2 * ratio);
        int g = Math.round(g1 * (1 - ratio) + g2 * ratio);
        int b = Math.round(b1 * (1 - ratio) + b2 * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int darken(int color, float factor) {
        factor = Math.max(0.0f, factor);

        int a = (color >> 24) & 0xFF;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.min(255, Math.max(0, Math.round(r * (1.F - factor))));
        g = Math.min(255, Math.max(0, Math.round(g * (1.F - factor))));
        b = Math.min(255, Math.max(0, Math.round(b * (1.F - factor))));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int brighten(int color, float factor) {
        factor = Math.max(0.0f, factor);

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.min(255, Math.max(0, Math.round(r * (1.F + factor))));
        g = Math.min(255, Math.max(0, Math.round(g * (1.F + factor))));
        b = Math.min(255, Math.max(0, Math.round(b * (1.F + factor))));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static ARGBNorm smoothInterpolate(
            Duration duration,
            NonNullList<Integer> colors
    ) {
        long transitionTime = duration.toMillis();
        long timeMillis = System.currentTimeMillis();
        ARGBNorm color = new ARGBNorm(0, 0, 0, 0);

        if (!colors.isEmpty() && transitionTime > 0) {
            long totalCycleTime = transitionTime * colors.size();
            long cycleTime = timeMillis % totalCycleTime;
            int colorIndex = (int) (cycleTime / transitionTime);
            int nextColorIndex = (colorIndex + 1) % colors.size();
            float progress = (float) (cycleTime % transitionTime) / transitionTime;

            color = ColorHelper.hexToARGBNorm(
                    ColorHelper.mix(
                            colors.get(colorIndex),
                            colors.get(nextColorIndex),
                            progress
                    )
            );
        }

        return color;
    }

    public static void printAsARGB(int... colors) {
        StringBuilder sb = new StringBuilder();

        for (int color : colors) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            sb.append(String.format("ARGB(%d, %d, %d, %d), ", a, r, g, b));
        }

        System.out.println(sb);
    }
}
