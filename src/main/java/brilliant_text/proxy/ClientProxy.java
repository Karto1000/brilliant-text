package brilliant_text.proxy;

import brilliant_text.shader.BrilliantTextRenderer;
import brilliant_text.shader.BrilliantTextManager;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        BrilliantTextRenderer.init();
        BrilliantTextManager.init();
    }
}