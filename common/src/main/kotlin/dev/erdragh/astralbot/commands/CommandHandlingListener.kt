package dev.erdragh.astralbot.commands

import dev.erdragh.astralbot.textChannel
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * Event listener for [JDA](https://jda.wiki) that sets up commands on servers
 *
 * @author Erdragh
 */
object CommandHandlingListener : ListenerAdapter() {
    /**
     * A way to register the commands without kicking the bot
     * @param event the event that was fired when the bot
     * received a message
     */
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw
        if (content == "!sync") {
            event.guild.updateCommands().addCommands(
                commands.map { it.command }
            ).queue {
                event.channel.sendMessage("Commands synced").queue()
            }
        }
    }

    /**
     * Registers commands on the Guild (Discord Server)
     * @param event the event that was fired when the bot
     * joined the server.
     */
    override fun onGuildJoin(event: GuildJoinEvent) {
        event.guild.updateCommands().addCommands(
            commands.map { it.command }
        ).queue()
    }

    /**
     * Handles the event that a user runs a slash command by
     * delegating it to the [HandledSlashCommand] implementations
     * @param event the interaction event that gets passed on
     * to the handlers
     */
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val usedCommand = commands.find { it.command.name == event.name }
        if (usedCommand != null) {
            usedCommand.handle(event)
        } else {
            event.reply("Something went wrong").queue()
        }
    }

    /**
     * Handles the event that a user is writing a slash command
     * that has autocompletion by delegating the suggestions
     * to the [AutocompleteCommand] implementations.
     * @param event the event that gets passed on to the handlers
     */
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val usedCommand =
            commands.find { it is AutocompleteCommand && it.command.name == event.name } as AutocompleteCommand?
        usedCommand?.autocomplete(event)
    }
}