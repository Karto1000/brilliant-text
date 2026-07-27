package brilliant_text.util;

import brilliant_text.BrilliantText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class TextureTarget {
    private final int framebufferObjectId;
    private final int textureId;
    private int width;
    private int height;

    public TextureTarget(int width, int height) {
        this.width = width;
        this.height = height;
        this.framebufferObjectId = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferObjectId);

        this.textureId = GL11.glGenTextures();
        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureId, 0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) BrilliantText.LOGGER.error("FBO not complete: {}", status);

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        // Bind it again to update it
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferObjectId);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureId, 0);

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    public void clear() {
        bind();
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        unbind();
    }

    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferObjectId);

        GlStateManager.viewport(0, 0, width, height);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GL11.glOrtho(0, res.getScaledWidth(), res.getScaledHeight(), 0, 0, 3000.0);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
    }

    public void unbind() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
