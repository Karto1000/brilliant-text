package brilliant_text.shader;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public interface ITextShader {
    /// Method that should return the OpenGL id referencing the shader program
    ///
    /// @return The shader program id
    int getShaderProgramId() throws ShaderNotFoundException;

    /// Apply the shader to the drawn text.
    ///
    /// @param buffer            The buffer to write to
    /// @param brilliantTextData The text data
    /// @param res               The scaled resolution
    default void renderPass(
            @Nonnull BufferBuilder buffer,
            @Nonnull BrilliantTextData brilliantTextData,
            @Nonnull ScaledResolution res
    ) {
        int programId = this.getShaderProgramId();
        ARBShaderObjects.glUseProgramObjectARB(programId);

        int textureUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_texture");
        int scaledScreenSizeUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_scaledScreenSize");
        int stringTopLeftUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_stringTopLeft");
        int stringBottomRightUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_stringBottomRight");
        int timeUniform = ARBShaderObjects.glGetUniformLocationARB(programId, "u_time");

        float padding = 1;
        float minX = brilliantTextData.aabb.getMinX() - padding;
        float minY = brilliantTextData.aabb.getMinY() - padding;
        float maxX = brilliantTextData.aabb.getMaxX() + padding;
        float maxY = brilliantTextData.aabb.getMaxY() + padding;

        int timeMillis = Math.toIntExact(System.currentTimeMillis() % 1_000_000L);
        ARBShaderObjects.glUniform1iARB(timeUniform, timeMillis);

        if (stringTopLeftUniform != -1) ARBShaderObjects.glUniform2fARB(stringTopLeftUniform, minX, minY);
        if (stringBottomRightUniform != -1) ARBShaderObjects.glUniform2fARB(stringBottomRightUniform, maxX, maxY);
        if (textureUniform != -1) ARBShaderObjects.glUniform1iARB(textureUniform, 0);
        if (scaledScreenSizeUniform != -1)
            ARBShaderObjects.glUniform2fARB(scaledScreenSizeUniform, res.getScaledWidth(), res.getScaledHeight());
        if (timeUniform != -1) ARBShaderObjects.glUniform1iARB(timeUniform, timeMillis);
    }
}
