package brilliant_text.shader.builtin;

import brilliant_text.BrilliantText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class CopperShader implements IOutlinedTextShader {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(
            BrilliantText.MODID,
            "textures/particles/glow.png"
    );

    @Override
    public Integer getTextColor() {
        return 0xFF60241E;
    }

    @Override
    public Optional<Integer> getOutlineColor() {
        return Optional.of(0xFFE77B49);
    }
}
