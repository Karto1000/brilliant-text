package brilliant_text.shader.builtin;

import brilliant_text.shader.BrilliantShaderManager;
import brilliant_text.shader.BrilliantTextData;
import brilliant_text.shader.ITextShader;
import brilliant_text.shader.ShaderNotFoundException;
import lombok.NonNull;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static brilliant_text.shader.BrilliantTextManager.FLAME_SHADER_DESIGNATION;

public class FlameTextShader implements ITextShader {
    @Override
    public int getShaderProgramId() throws ShaderNotFoundException {
        return BrilliantShaderManager.getProgramId(FLAME_SHADER_DESIGNATION)
                .orElseThrow(() -> new ShaderNotFoundException("Flame shader program not registered"));
    }

    @Override
    public void renderPass(
            @Nonnull BufferBuilder buffer,
            @NonNull BrilliantTextData brilliantTextData,
            @NonNull ScaledResolution res
    ) {
        ITextShader.super.renderPass(buffer, brilliantTextData, res);

        float padding = 2.0f;
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