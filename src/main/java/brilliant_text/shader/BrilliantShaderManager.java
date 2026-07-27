package brilliant_text.shader;

import brilliant_text.util.ShaderHelper;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Optional;

public class BrilliantShaderManager {
    private final static HashMap<String, Integer> shaderPrograms = new HashMap<>();

    public static void registerShader(
            String designation,
            ResourceLocation fragmentShader,
            ResourceLocation vertexShader
    ) {
        int programId = ShaderHelper.createShader(fragmentShader, vertexShader);
        shaderPrograms.put(designation, programId);
    }

    public static Optional<Integer> getProgramId(String designation) {
        Integer id = shaderPrograms.get(designation);
        return Optional.ofNullable(id);
    }

    public static boolean hasAnyShaders() {
        return !shaderPrograms.isEmpty();
    }
}
