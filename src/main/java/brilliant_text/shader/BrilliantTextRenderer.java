package brilliant_text.shader;

import brilliant_text.BrilliantText;
import brilliant_text.util.AABB2D;
import brilliant_text.util.ColorHelper;
import brilliant_text.util.TextureTarget;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BrilliantText.MODID, value = Side.CLIENT)
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class BrilliantTextRenderer {
    @Getter
    private static TextureTarget fboTarget;

    private static int lastWidth;
    private static int lastHeight;

    private static final List<BrilliantTextData> brilliantTexts = new ArrayList<>();

    @Getter
    private static final List<BrilliantParticle> particles = new ArrayList<>();

    public static void init() {
        Minecraft mc = Minecraft.getMinecraft();
        fboTarget = new TextureTarget(mc.displayWidth, mc.displayHeight);
        lastWidth = mc.displayWidth;
        lastHeight = mc.displayHeight;
    }

    public static void addBrilliantTextAt(AABB2D aabb, ITextShader shader) {
        BrilliantTextData data = new BrilliantTextData(aabb, shader);
        brilliantTexts.add(data);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        // To avoid calling resize twice
        if (lastWidth != mc.displayWidth && lastHeight != mc.displayHeight) {
            lastWidth = mc.displayWidth;
            lastHeight = mc.displayHeight;
            fboTarget.resize(mc.displayWidth, mc.displayHeight);
            return;
        }

        if (lastWidth != mc.displayWidth) {
            lastWidth = mc.displayWidth;
            fboTarget.resize(mc.displayWidth, lastHeight);
        }

        if (lastHeight != mc.displayHeight) {
            lastHeight = mc.displayHeight;
            fboTarget.resize(lastWidth, mc.displayHeight);
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            fboTarget.clear();
            brilliantTexts.clear();
        } else if (event.phase == TickEvent.Phase.END) {
            BrilliantTextRenderer.renderParticles();
        }
    }

    public static void flush() {
        if (brilliantTexts.isEmpty()) return;

        BrilliantTextRenderer.drawFBOToScreen();

        // Clear queue & FBO so later passes start fresh
        brilliantTexts.clear();
        fboTarget.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) flush();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGuiDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (brilliantTexts.isEmpty()) {
            particles.clear();
            return;
        }

        flush();
    }

    private static void renderParticles() {
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
            particle.onRenderTick();

            mc.getTextureManager().bindTexture(particle.texture);

            if (particle.currentLifetime == 0) {
                particles.remove(i);
                continue;
            }

            float alpha = particle.currentLifetime / particle.maxLifetime;

            float hw = particle.dimensions;
            float hh = particle.dimensions;
            float cx = particle.x + hw;
            float cy = particle.y + hh;

            // Calculate rotation
            float angleRad = (float) Math.toRadians(particle.rotation);
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

            ColorHelper.ARGBNorm argb = ColorHelper.hexToARGBNorm(particle.color);
            buffer.pos(x1, y1, 0).tex(0, 0).color(argb.r, argb.g, argb.b, argb.a * alpha).endVertex();
            buffer.pos(x2, y2, 0).tex(0, 1).color(argb.r, argb.g, argb.b, argb.a * alpha).endVertex();
            buffer.pos(x3, y3, 0).tex(1, 1).color(argb.r, argb.g, argb.b, argb.a * alpha).endVertex();
            buffer.pos(x4, y4, 0).tex(1, 0).color(argb.r, argb.g, argb.b, argb.a * alpha).endVertex();

            particle.currentLifetime--;
            particle.rotation += particle.rotationsPerFrame;

        }

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private static void drawFBOToScreen() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, res.getScaledWidth(), res.getScaledHeight(), 0.0D, 0D, 3000.0D);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Bind the FBO texture using GlStateManager
        GlStateManager.bindTexture(fboTarget.getTextureId());
        GlStateManager.enableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        if (BrilliantShaderManager.hasAnyShaders()) {
            for (BrilliantTextData brilliantTextData : brilliantTexts) {
                if (brilliantTextData.shader instanceof IParticleSpawner) {
                    IParticleSpawner spawner = (IParticleSpawner) brilliantTextData.shader;
                    BrilliantParticle particle = spawner.getNewParticle(brilliantTextData);
                    Minecraft mc = Minecraft.getMinecraft();
                    if (spawner.shouldSpawnParticle(mc.world.rand)) BrilliantTextRenderer.getParticles().add(particle);
                }

                brilliantTextData.shader.renderPass(buffer, brilliantTextData, res);
                tessellator.draw();
            }

            ARBShaderObjects.glUseProgramObjectARB(0);
        }

        GlStateManager.enableDepth();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        GlStateManager.bindTexture(0);
    }
}
