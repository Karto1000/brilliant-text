package brilliant_text.shader.builtin;

import brilliant_text.shader.*;
import brilliant_text.util.ARGBNorm;
import brilliant_text.util.ColorHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;

@SideOnly(Side.CLIENT)
public interface IOutlinedTextShader extends ITextShader {
    /// Decides the color of the glow in the background of the text
    ///
    /// @return A non-null list of colors that will be interpolated through. Return only a single element for a solid color.
    ///         Returning an empty list causes no glow to be drawn
    @Nonnull
    default NonNullList<Integer> getGlowColors() {
        return NonNullList.create();
    }

    /// The time that it takes to transition from one glow color to the next
    ///
    /// @return The time
    @Nonnull
    default Duration getGlowColorTransitionDuration() {
        return Duration.ofMillis(1000);
    }

    /// Decides the color of the text outline
    ///
    /// @return A non-null list of colors that will be interpolated through. Return only a single element for a solid color.
    ///         Returning an empty list causes no outline to be drawn
    @Nonnull
    default NonNullList<Integer> getOutlineColors() {
        return NonNullList.create();
    }

    /// The time that it takes to transition from one outline color to the next
    ///
    /// @return The time
    @Nonnull
    default Duration getOutlineColorTransitionDuration() {
        return Duration.ofMillis(1000);
    }

    /// Decides the color of the text
    ///
    /// @return A non-null list of colors that will be interpolated through. Return only a single element for a solid color.
    ///         Returning an empty list causes no text to be drawn
    @Nonnull
    default NonNullList<Integer> getTextColors() {
        return NonNullList.withSize(1, 0xFFFFFFFF);
    }

    /// The time that it takes to transition from one text color to the next
    ///
    /// @return The time
    @Nonnull
    default Duration getTextColorTransitionDuration() {
        return Duration.ofMillis(1000);
    }

    /// Decides the color of the wiper that goes across the text
    ///
    /// @return The color wrapped in an optional, an empty optional means that no wiper will be drawn
    @Nonnull
    default Optional<Integer> getWiperColor() {
        return Optional.empty();
    }

    /// A number that determines the speed of the wiper
    ///
    /// @return The slowdown amount, higher = slower
    default int getWiperSlowdown() {
        return 50;
    }

    @Override
    default int getShaderProgramId() throws ShaderNotFoundException {
        Optional<Integer> id = BrilliantShaderManager.getProgramId(BrilliantTextManager.DEFAULT_SHADER_DESIGNATION);
        return id.orElseThrow(() -> new ShaderNotFoundException("No Shader program"));
    }

    @Override
    default void renderPass(
            @Nonnull BufferBuilder buffer,
            @Nonnull BrilliantTextData brilliantTextData,
            @Nonnull ScaledResolution res
    ) {
        ITextShader.super.renderPass(buffer, brilliantTextData, res);
        int programId = this.getShaderProgramId();

        int outlineColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_outlineColor");
        int glowColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_glowColor");
        int textColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_textColor");
        int wiperColorUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_wiperColor");
        int wiperSlowdownUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_wiperSlowdown");

        IOutlinedTextShader shader = (IOutlinedTextShader) brilliantTextData.shader;

        NonNullList<Integer> outlineColors = shader.getOutlineColors();
        ARGBNorm outlineColor = ColorHelper.smoothInterpolate(
                this.getOutlineColorTransitionDuration(),
                outlineColors
        );
        ARBShaderObjects.glUniform4fARB(
                outlineColorUniform,
                outlineColor.r,
                outlineColor.g,
                outlineColor.b,
                outlineColor.a
        );

        NonNullList<Integer> glowColors = shader.getGlowColors();
        ARGBNorm glowColor = ColorHelper.smoothInterpolate(
                this.getGlowColorTransitionDuration(),
                glowColors
        );
        ARBShaderObjects.glUniform4fARB(
                glowColorUniform,
                glowColor.r,
                glowColor.g,
                glowColor.b,
                glowColor.a
        );


        NonNullList<Integer> textColors = shader.getTextColors();
        ARGBNorm textColor = ColorHelper.smoothInterpolate(
                this.getTextColorTransitionDuration(),
                textColors
        );
        ARBShaderObjects.glUniform4fARB(
                textColorUniform,
                textColor.r,
                textColor.g,
                textColor.b,
                textColor.a
        );

        ARGBNorm wiperColor = ColorHelper.hexToARGBNorm(shader.getWiperColor().orElse(0x00000000));
        ARBShaderObjects.glUniform4fARB(
                wiperColorUniform,
                wiperColor.r,
                wiperColor.g,
                wiperColor.b,
                wiperColor.a
        );

        ARBShaderObjects.glUniform1iARB(wiperSlowdownUniform, this.getWiperSlowdown());

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
