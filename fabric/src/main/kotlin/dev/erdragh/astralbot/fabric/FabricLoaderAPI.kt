package dev.erdragh.astralbot.fabric

import dev.erdragh.astralbot.abc.LoaderAPI
import net.fabricmc.loader.api.FabricLoader

class FabricLoaderAPI : LoaderAPI {
    override fun isModLoaded(modId: String): Boolean =
        FabricLoader.getInstance().isModLoaded(modId)
}