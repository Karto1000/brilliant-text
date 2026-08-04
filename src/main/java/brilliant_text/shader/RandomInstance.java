package brilliant_text.shader;

import net.minecraft.client.Minecraft;

import java.util.Random;

public class RandomInstance {
    private static final Random RANDOM = new Random();

    private RandomInstance() {
    }

    public static Random getInstance() {
        Minecraft mc = Minecraft.getMinecraft();

        // Since world.rand is not available when the world isn't loaded (i.e., the main menu), we need to use a
        // different Random instance
        if (mc.world == null) return RANDOM;
        return mc.world.rand;
    }
}
