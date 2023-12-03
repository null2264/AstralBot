package dev.erdragh.astralbot.forge

import dev.erdragh.astralbot.LOGGER
import dev.erdragh.astralbot.config.AstralBotConfig
import dev.erdragh.astralbot.startAstralbot
import dev.erdragh.astralbot.stopAstralbot
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@Mod("astralbot")
object BotMod {
    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AstralBotConfig.SPEC)
        FORGE_BUS.addListener(::onServerStart)
        FORGE_BUS.addListener(::onServerStop)
    }

    private fun onServerStart(event: ServerStartedEvent) {
        LOGGER.info("AstralBot starting on Forge")
        startAstralbot(event.server)
    }

    private fun onServerStop(event: ServerStoppingEvent) {
        stopAstralbot()
    }
}