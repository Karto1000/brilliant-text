package brilliant_text.mixin.vanilla;

import brilliant_text.BrilliantText;
import brilliant_text.shader.BrilliantTextRenderer;
import brilliant_text.shader.BrilliantTextManager;
import brilliant_text.shader.FormatCharacter;
import brilliant_text.shader.ITextShader;
import brilliant_text.util.AABB2D;
import brilliant_text.util.TextureTargetManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SideOnly(Side.CLIENT)
@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    @Shadow
    public abstract int getStringWidth(String text);

    @Shadow
    public int FONT_HEIGHT;

    @ModifyVariable(
            method = "drawString(Ljava/lang/String;FFIZ)I",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    public boolean brilliant_text_drawString(
            boolean dropShadow,
            String text,
            float x,
            float y,
            int color
    ) {
        if (BrilliantTextRenderer.getFboTarget() == null) return dropShadow;
        if (text.trim().length() < 2) return dropShadow;

        String strippedText = text.trim().replaceAll("(?i)§[0-9A-FK-OR]", "");
        if (strippedText.length() < 2) return dropShadow;

        String firstTwo = strippedText.substring(0, 2);
        if (!firstTwo.startsWith(String.valueOf(BrilliantTextManager.FORMAT_CHARACTER_PREFIX))) return dropShadow;

        char formatChar = firstTwo.charAt(1);
        if (FormatCharacter.isInvalidCharacter(formatChar)) return dropShadow;

        try {
            FormatCharacter formatCharacter = FormatCharacter.tryFrom(formatChar);

            Optional<ITextShader> oShader = BrilliantTextManager.getShader(formatCharacter);
            if (!oShader.isPresent()) {
                BrilliantText.LOGGER.warn("No Shader for format character {}", formatCharacter);
                return dropShadow;
            }

            return false;

        } catch (IllegalArgumentException ignored) {
            BrilliantText.LOGGER.warn("Character {} is not allowed", formatChar);
        }

        return dropShadow;
    }

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"))
    public void brilliant_text_drawString(
            String text,
            float x,
            float y,
            int color,
            boolean dropShadow,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (BrilliantTextRenderer.getFboTarget() == null) return;
        if (text.trim().length() < 2) return;

        String strippedText = text.trim().replaceAll("(?i)§[0-9A-FK-OR]", "");
        if (strippedText.length() < 2) return;

        String firstTwo = strippedText.substring(0, 2);
        if (!firstTwo.startsWith(String.valueOf(BrilliantTextManager.FORMAT_CHARACTER_PREFIX))) return;

        char formatChar = firstTwo.charAt(1);
        if (FormatCharacter.isInvalidCharacter(formatChar)) return;

        try {
            FormatCharacter formatCharacter = FormatCharacter.tryFrom(formatChar);

            Optional<ITextShader> oShader = BrilliantTextManager.getShader(formatCharacter);
            if (!oShader.isPresent()) {
                BrilliantText.LOGGER.warn("No Shader for format character {}", formatCharacter);
                return;
            }

            ITextShader shader = oShader.get();

            BrilliantTextRenderer.addBrilliantTextAt(
                    new AABB2D(x, y, this.getStringWidth(text), this.FONT_HEIGHT),
                    shader
            );
            TextureTargetManager.bindFBO(BrilliantTextRenderer.getFboTarget());
        } catch (IllegalArgumentException ignored) {
            BrilliantText.LOGGER.warn("Character {} is not allowed", formatChar);
        }
    }

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("TAIL"))
    public void brilliant_text_drawStringTail(
            String text,
            float x,
            float y,
            int color,
            boolean dropShadow,
            CallbackInfoReturnable<Integer> cir
    ) {
        TextureTargetManager.bindScreen();
    }

    @Redirect(
            method = "renderStringAtPos",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;indexOf(I)I"
            )
    )
    private int redirectFormatIndexOf(String formatCodes, int ch) {
        int index = formatCodes.indexOf(ch);

        // If it's a custom code (index == -1), return 99 (> 21) so it bypasses
        // the `if (i1 < 16)` check that resets styles and forces white color (i1 = 15)
        if (index == -1) return 99;
        return index;
    }
}
