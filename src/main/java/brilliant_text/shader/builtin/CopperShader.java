package brilliant_text.shader.builtin;

import brilliant_text.BrilliantText;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CopperShader implements IOutlinedTextShader {
    @Override
    public int getTextColor() {
        return 0xFF60241E;
    }

    @Override
    public @Nonnull Optional<Integer> getOutlineColor() {
        return Optional.of(0xFFE77B49);
    }
}
