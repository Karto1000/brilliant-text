package brilliant_text.util;

import brilliant_text.BrilliantText;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderHelper {
    public static int createShader(ResourceLocation fragmentShader, ResourceLocation vertexShader) {
        int program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0) return 0;

        int vertShader = createShaderObject(vertexShader, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        int fragShader = createShaderObject(fragmentShader, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        if (vertShader != 0) ARBShaderObjects.glAttachObjectARB(program, vertShader);
        if (fragShader != 0) ARBShaderObjects.glAttachObjectARB(program, fragShader);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(
                program,
                ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB
        ) == GL11.GL_FALSE) {
            BrilliantText.LOGGER.error("Error linking shader program: {}", getLogInfo(program));
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(
                program,
                ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB
        ) == GL11.GL_FALSE) {
            BrilliantText.LOGGER.error("Error validating shader program: {}", getLogInfo(program));
            return 0;
        }

        return program;
    }

    private static int createShaderObject(ResourceLocation location, int shaderType) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        if (shader == 0) return 0;

        try {
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            String source = IOUtils.toString(in, StandardCharsets.UTF_8);
            in.close();

            ARBShaderObjects.glShaderSourceARB(shader, source);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(
                    shader,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB
            ) == GL11.GL_FALSE) {
                BrilliantText.LOGGER.error("Error compiling shader {}: {}", location, getLogInfo(shader));
                return 0;
            }

            return shader;
        } catch (Exception e) {
            BrilliantText.LOGGER.error("Failed to load shader {}", location, e);
            return 0;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(
                obj,
                ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)
        );
    }
}
