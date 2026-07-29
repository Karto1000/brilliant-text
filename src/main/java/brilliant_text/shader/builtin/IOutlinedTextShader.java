package brilliant_text.shader.builtin;

import brilliant_text.shader.*;
import brilliant_text.util.ColorHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public interface IOutlinedTextShader extends ITextShader {
    /// Decides the color of the glow in the background of the text
    ///
    /// @return The color represented as hex wrapped in an optional. An empty optional indicates that no glow is present
    @Nonnull
    default Optional<Integer> getGlowColor() {
        return Optional.empty();
    }

    /// Decides the color of the text outline
    ///
    /// @return The color represented as hex wrapped in an optional. An empty optional indicates that no outline is present
    @Nonnull
    default Optional<Integer> getOutlineColor() {
        return Optional.empty();
    }

    /// Decides the color of the text
    ///
    /// @return The color of the text as hex
    default int getTextColor() {
        return 0xFFFFFFFF;
    }

    @Override
    default int getShaderProgramId() throws ShaderNotFoundException {
        Optional<Integer> id = BrilliantShaderManager.getProgramId(BrilliantTextManager.DEFAULT_SHADER_DESIGNATION);
        return id.orElseThrow(() -> new ShaderNotFoundException("No Shader program"));
    }

    default void renderPass(
            BufferBuilder buffer,
            BrilliantTextData brilliantTextData,
            ScaledResolution res
    ) {
        ITextShader.super.renderPass(buffer, brilliantTextData, res);
        int programId = this.getShaderProgramId();

        int outlineColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_outlineColor");
        int glowColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_glowColor");
        int textColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_textColor");

        IOutlinedTextShader shader = (IOutlinedTextShader) brilliantTextData.shader;

        ColorHelper.ARGBNorm outlineColor = ColorHelper.hexToARGBNorm(shader.getOutlineColor().orElse(0x00000000));
        ARBShaderObjects.glUniform4fARB(
                outlineColorUniform,
                outlineColor.r,
                outlineColor.g,
                outlineColor.b,
                outlineColor.a
        );

        ColorHelper.ARGBNorm glowColor = ColorHelper.hexToARGBNorm(shader.getGlowColor().orElse(0x00000000));
        ARBShaderObjects.glUniform4fARB(
                glowColorUniform,
                glowColor.r,
                glowColor.g,
                glowColor.b,
                glowColor.a
        );

        ColorHelper.ARGBNorm textColor = ColorHelper.hexToARGBNorm(shader.getTextColor());
        ARBShaderObjects.glUniform4fARB(
                textColorUniform,
                textColor.r,
                textColor.g,
                textColor.b,
                textColor.a
        );

        float padding = 1;
        float minX = brilliantTextData.aabb.getMinX() - padding;
        float minY = brilliantTextData.aabb.getMinY() - padding;
        float maxX = brilliantTextData.aabb.getMaxX() + padding;
        float maxY = brilliantTextData.aabb.getMaxY() + padding;
        float uMin = minX / res.getScaledWidth();
        float vMin = 1.0f - (maxY / res.getScaledHeight());
        float uMax = maxX / res.getScaledWidth();
        float vMax = 1.0f - (minY / res.getScaledHeight());

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(minX, minY, 0.0D).tex(uMin, vMax).endVertex();
        buffer.pos(minX, maxY, 0.0D).tex(uMin, vMin).endVertex();
        buffer.pos(maxX, maxY, 0.0D).tex(uMax, vMin).endVertex();
        buffer.pos(maxX, minY, 0.0D).tex(uMax, vMax).endVertex();
    }
}
