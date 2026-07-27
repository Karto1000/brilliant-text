package brilliant_text.shader.builtin;

import brilliant_text.shader.*;
import brilliant_text.util.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Optional;

public interface IOutlinedTextShader extends ITextShader {
    /// Decides the color of the glow in the background of the text
    ///
    /// @return The color represented as hex wrapped in an optional. An empty optional indicates that no glow is present
    default Optional<Integer> getGlowColor() {
        return Optional.empty();
    }

    /// Decides the color of the text outline
    ///
    /// @return The color represented as hex wrapped in an optional. An empty optional indicates that no outline is present
    default Optional<Integer> getOutlineColor() {
        return Optional.empty();
    }

    /// Decides the color of the text
    ///
    /// @return The color of the text as hex
    default Integer getTextColor() {
        return 0xFFFFFFFF;
    }

    /// Get the particle settings
    ///
    /// @return The particle settings wrapped in an optional. An empty optional indicates that no particles will be spawned
    default Optional<ParticleSettings> getSettingsForNewParticle() {
        return Optional.empty();
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
        Optional<ParticleSettings> settings = this.getSettingsForNewParticle();
        settings.ifPresent(s -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world.rand.nextInt(s.particleEveryXFrames) == 0) {
                BrilliantParticle particle = new BrilliantParticle(
                        new Vec2f(
                                brilliantTextData.aabb.x + mc.world.rand.nextInt(brilliantTextData.aabb.width),
                                brilliantTextData.aabb.y - ((float) brilliantTextData.aabb.height / 2) + mc.world.rand.nextInt(brilliantTextData.aabb.height)
                        ),
                        s
                );
                BrilliantTextRenderer.getParticles().add(particle);
            }
        });

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

    @Override
    default void onRenderTick() {
        Minecraft mc = Minecraft.getMinecraft();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        List<BrilliantParticle> particles = BrilliantTextRenderer.getParticles();
        for (int i = particles.size() - 1; i >= 0; i--) {
            BrilliantParticle particle = particles.get(i);
            mc.getTextureManager().bindTexture(particle.settings.resourceLocation);

            if (particle.currentLifetime == 0) {
                particles.remove(i);
                continue;
            }

            float alpha = particle.currentLifetime / particle.settings.maxLifetime;

            float hw = particle.settings.dimensions;
            float hh = particle.settings.dimensions;
            float cx = particle.pos.x + hw;
            float cy = particle.pos.y + hh;

            // Calculate rotation
            float angleRad = (float) Math.toRadians(particle.currentRotation);
            float cos = (float) Math.cos(angleRad);
            float sin = (float) Math.sin(angleRad);

            // Calculate rotated corners
            // Top-Left
            float x1 = cx + (-hw * cos - -hh * sin);
            float y1 = cy + (-hw * sin + -hh * cos);
            // Bottom-Left
            float x2 = cx + (-hw * cos - hh * sin);
            float y2 = cy + (-hw * sin + hh * cos);
            // Bottom-Right
            float x3 = cx + (hw * cos - hh * sin);
            float y3 = cy + (hw * sin + hh * cos);
            // Top-Right
            float x4 = cx + (hw * cos - -hh * sin);
            float y4 = cy + (hw * sin + -hh * cos);

            ColorHelper.ARGBNorm argb = ColorHelper.hexToARGBNorm(particle.settings.color);
            buffer.pos(x1, y1, 0).tex(0, 0).color(argb.r, argb.g, argb.b, alpha).endVertex();
            buffer.pos(x2, y2, 0).tex(0, 1).color(argb.r, argb.g, argb.b, alpha).endVertex();
            buffer.pos(x3, y3, 0).tex(1, 1).color(argb.r, argb.g, argb.b, alpha).endVertex();
            buffer.pos(x4, y4, 0).tex(1, 0).color(argb.r, argb.g, argb.b, alpha).endVertex();

            particle.currentLifetime--;
            particle.currentRotation += particle.settings.rotationPerFrame;

        }

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }
}
