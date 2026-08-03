package brilliant_text.config;

import brilliant_text.BrilliantText;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShaderDefinition {
    @Builder.Default
    @JsonAdapter(HexListColorAdapter.class)
    public NonNullList<Integer> textColors = NonNullList.withSize(1, 0xFFFFFFFF);

    @Builder.Default
    @JsonAdapter(HexListColorAdapter.class)
    public NonNullList<Integer> outlineColors = null;

    @Builder.Default
    @JsonAdapter(HexListColorAdapter.class)
    public NonNullList<Integer> glowColors = null;

    @Builder.Default
    public ParticleConfig particleConfig = null;

    @Builder.Default
    public WiperConfig wiperConfig = null;

    public void validateAndSanitize() {
        if (this.wiperConfig != null) {
            this.wiperConfig.slowdown = Math.max(0, this.wiperConfig.slowdown);
        }

        if (this.particleConfig != null) {
            this.particleConfig.rarity = Math.max(1, this.particleConfig.rarity);
            this.particleConfig.lifetime = Math.max(0, this.particleConfig.lifetime);
            this.particleConfig.dimensions.min = Math.max(1, this.particleConfig.dimensions.min);
            this.particleConfig.dimensions.max = Math.max(1, this.particleConfig.dimensions.max);
            this.particleConfig.rotationsPerFrame.min = Math.max(0, this.particleConfig.rotationsPerFrame.min);
            this.particleConfig.rotationsPerFrame.max = Math.max(0, this.particleConfig.rotationsPerFrame.max);
            this.particleConfig.rotation.min = Math.max(0, this.particleConfig.rotation.min);
            this.particleConfig.rotation.max = Math.max(0, this.particleConfig.rotation.max);
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WiperConfig {
        @JsonAdapter(HexColorAdapter.class)
        public Integer color;

        @Builder.Default
        public int slowdown = 50;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticleConfig {
        @Builder.Default
        @Nonnull
        public ResourceLocation texture = new ResourceLocation(BrilliantText.MODID, "textures/particles/glow.png");

        @Builder.Default
        @JsonAdapter(HexColorAdapter.class)
        public int color = 0xFFFFFFFF;

        @Builder.Default
        public int rarity = 100;

        @Builder.Default
        public int lifetime = 200;

        @Builder.Default
        @Nonnull
        public NumberRange dimensions = new NumberRange(2.0f, 4.0f);

        @Builder.Default
        @Nonnull
        public NumberRange rotation = new NumberRange(1.0f, 360.0f);

        @Builder.Default
        @Nonnull
        public NumberRange rotationsPerFrame = new NumberRange(0.0f, 1.0f);

        @Builder.Default
        public boolean shouldShrink = true;
    }
}
