package brilliant_text.util;


import lombok.Getter;

public class TextureTargetManager {
    @Getter
    private static TextureTarget activeTarget = null;
    private static int bindDepth = 0;

    public static void bindFBO(TextureTarget target) {
        if (bindDepth == 0) {
            activeTarget = target;
            target.bind();
        }
        bindDepth++;
    }

    public static void bindScreen() {
        if (bindDepth > 0) {
            bindDepth--;
            if (bindDepth == 0 && activeTarget != null) {
                activeTarget.unbind();
                activeTarget = null;
            }
        }
    }

}
