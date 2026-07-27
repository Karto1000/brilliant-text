package brilliant_text;

import java.util.Map;

import fermiumbooter.FermiumRegistryAPI;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class BrilliantTextPlugin implements IFMLLoadingPlugin {

	public BrilliantTextPlugin() {
		MixinBootstrap.init();
		//Replaced by @MixinConfig.MixinToggle:

		//False for Vanilla/Coremod mixins, true for regular mod mixins
		//FermiumRegistryAPI.enqueueMixin(false, "mixins.replacememodid.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(false, "mixins.brilliant_text.vanilla.json");

		//FermiumRegistryAPI.enqueueMixin(true, "mixins.replacememodid.jei.json", () -> Loader.isModLoaded("jei"));
		//--> Replaced by @MixinConfig.MixinToggle in ForgeConfigHandler. This way is still an option for more complicated conditions
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) { }

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}