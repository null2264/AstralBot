package dev.erdragh.astralbot.fabric.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class ServerMessageEvents {
    companion object {
        @JvmStatic
        val CHAT_MESSAGE: Event<ChatMessage> =
            EventFactory.createArrayBacked(ChatMessage::class.java) { callbacks -> ChatMessage { message, player -> run {
                for (callback in callbacks) {
                    callback.onChatMessage(message, player);
                }}}
            }

        @JvmStatic
        val GAME_MESSAGE: Event<GameMessage> =
            EventFactory.createArrayBacked(GameMessage::class.java) { callbacks -> GameMessage { message -> run {
                for (callback in callbacks) {
                    callback.onGameMessage(message);
                }}}
            }
    }

    fun interface ChatMessage {
        fun onChatMessage(message: Component, player: ServerPlayer?)
    }

    fun interface GameMessage {
        fun onGameMessage(message: Component)
    }
}