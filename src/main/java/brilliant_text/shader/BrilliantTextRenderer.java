package brilliant_text.shader;

import brilliant_text.BrilliantText;
import brilliant_text.shader.builtin.BrilliantParticle;
import brilliant_text.util.AABB2D;
import brilliant_text.util.TextureTarget;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BrilliantText.MODID, value = Side.CLIENT)
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
            for (Map.Entry<FormatCharacter, ITextShader> textShader : BrilliantTextManager.getShaders().entrySet()) {
                textShader.getValue().onRenderTick();
            }
        }
    }

    public static void flush() {
        if (brilliantTexts.isEmpty()) return;

        drawFBOToScreen();

        // Clear queue & FBO so subsequent passes start fresh
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
        };

        flush();
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
