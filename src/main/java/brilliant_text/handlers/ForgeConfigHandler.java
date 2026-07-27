package brilliant_text.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import brilliant_text.BrilliantText;

@Config(modid = BrilliantText.MODID)
public class ForgeConfigHandler {
	
	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();

	public static class ClientConfig {
	}

	@Mod.EventBusSubscriber(modid = BrilliantText.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(BrilliantText.MODID)) {
				ConfigManager.sync(BrilliantText.MODID, Config.Type.INSTANCE);
			}
		}
	}
}