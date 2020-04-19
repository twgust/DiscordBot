package LevelModule;

import EconomyModule.EconomyController;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class LevelListener extends ListenerAdapter {
    EconomyController economyController;
    public LevelListener(EconomyController economyController) {
        this.economyController = economyController;
    }
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()){
            LevelController.addExp(event.getGuild(), event.getMember(), event.getChannel());
            economyController.activityReward(event.getMember().getId());
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        LevelController.addGuild(event.getGuild());
    }
}
