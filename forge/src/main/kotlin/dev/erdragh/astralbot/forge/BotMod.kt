package dev.erdragh.astralbot.forge

import dev.erdragh.astralbot.*
import dev.erdragh.astralbot.config.AstralBotConfig
import dev.erdragh.astralbot.forge.event.SystemMessageEvent
import dev.erdragh.astralbot.handlers.DiscordMessageComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@Mod(MODID)
object BotMod {
    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AstralBotConfig.SPEC)
        FORGE_BUS.addListener(::onServerStart)
        FORGE_BUS.addListener(::onServerStop)
        FORGE_BUS.addListener(::onChatMessage)
        FORGE_BUS.addListener(::onSystemMessage)

        FORGE_BUS.addListener(::onPlayerJoin)
        FORGE_BUS.addListener(::onPlayerLeave)
    }

    private fun onServerStart(event: ServerStartedEvent) {
        LOGGER.info("AstralBot starting on Forge")
        startAstralbot(event.server)
    }

    private fun onServerStop(event: ServerStoppingEvent) {
        stopAstralbot()
    }

    private fun onChatMessage(event: ServerChatEvent) {
        minecraftHandler?.sendChatToDiscord(event.player, event.component.string)
    }

    private fun onSystemMessage(event: SystemMessageEvent) {
        if (event.component !is DiscordMessageComponent) {
            minecraftHandler?.sendChatToDiscord(null as ServerPlayer?, event.component.string)
        }
    }

    private fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        minecraftHandler?.onPlayerJoin(event.entity.name.string)
    }

    private fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        minecraftHandler?.onPlayerLeave(event.entity.name.string)
    }
}