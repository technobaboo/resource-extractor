package org.technobaboo.resource_extractor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Optional;

public class ResourceExtractorMod implements ClientModInitializer {
	static protected HashMap<String, Boolean> modsContainAssets = new HashMap<>();

	static private boolean ContainsAssetsInternal(Optional<ModContainer> mod) {
		return mod.isPresent() && Files.isDirectory(mod.get().getPath("assets"));
	}
	static public boolean ContainsAssets(String modid) {
		if(!modsContainAssets.containsKey(modid)) {
			Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modid);
			modsContainAssets.put(modid, ContainsAssetsInternal(mod));
		}
		return modsContainAssets.get(modid);
	}

	@Override
	public void onInitializeClient() {
		System.out.println("Initialized Resource Extractor!");

		for(ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			modsContainAssets.put(mod.getMetadata().getId(), ContainsAssetsInternal(Optional.of(mod)));
		}
	}
}
