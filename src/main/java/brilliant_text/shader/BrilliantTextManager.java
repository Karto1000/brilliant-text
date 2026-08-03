package brilliant_text.shader;

import brilliant_text.BrilliantText;
import brilliant_text.config.ForgeConfigHandler;
import brilliant_text.shader.builtin.FlameTextShader;
import brilliant_text.shader.builtin.IOutlinedTextShader;
import brilliant_text.util.ColorHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class BrilliantTextManager {
    @Getter
    private final static HashMap<FormatCharacter, ITextShader> shaders = new HashMap<>();

    public final static char FORMAT_CHARACTER_PREFIX = '§';
    public static final ResourceLocation DEFAULT_VERTEX_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/outline.vert"
    );
    public static final ResourceLocation DEFAULT_FRAGMENT_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/outline.frag"
    );

    public static final ResourceLocation FLAME_VERTEX_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/flame.vert"
    );

    public static final ResourceLocation FLAME_FRAGMENT_SHADER = new ResourceLocation(
            BrilliantText.MODID,
            "shaders/post/flame.frag"
    );

    public static final String DEFAULT_SHADER_DESIGNATION = "default";
    public static final String FLAME_SHADER_DESIGNATION = "flame";

    public static void init() {
        BrilliantShaderManager.registerShader(
                DEFAULT_SHADER_DESIGNATION,
                DEFAULT_FRAGMENT_SHADER,
                DEFAULT_VERTEX_SHADER
        );
        BrilliantShaderManager.registerShader(
                FLAME_SHADER_DESIGNATION,
                FLAME_FRAGMENT_SHADER,
                FLAME_VERTEX_SHADER
        );

        BrilliantTextManager.bindCharToShader(FormatCharacter.tryFrom('v'), new FlameTextShader());
    }

    public static void bindCharToShader(FormatCharacter character, ITextShader shader) {
        shaders.put(character, shader);
    }

    public static Optional<ITextShader> getShader(FormatCharacter character) {
        ITextShader shader = shaders.get(character);
        return Optional.ofNullable(shader);
    }
}
