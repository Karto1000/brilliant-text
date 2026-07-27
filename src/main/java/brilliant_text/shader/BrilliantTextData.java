package brilliant_text.shader;

import brilliant_text.util.AABB2D;

public class BrilliantTextData {
    public final AABB2D aabb;
    public final ITextShader shader;

    public BrilliantTextData(AABB2D aabb, ITextShader shader) {
        this.aabb = aabb;
        this.shader = shader;
    }
}
