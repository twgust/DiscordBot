package LevelModule;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class LevelListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()){
            LevelController.addExp(event.getGuild(), event.getMember(), event.getChannel());
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        LevelController.addGuild(event.getGuild());
    }
}
