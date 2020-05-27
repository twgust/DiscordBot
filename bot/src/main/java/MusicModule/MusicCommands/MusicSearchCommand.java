package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.AudioPlayerSendHandler;
import MusicModule.MusicController;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicSearchCommand extends Command {
    MusicController musicController;
    EventWaiter waiter;

    public MusicSearchCommand(MusicController musicController, EventWaiter waiter){
        this.musicController = musicController;
        this.waiter = waiter;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Guild server = event.getGuild();
        Message message = event.getMessage();
        Member user = message.getMember();
        String messageRaw = message.getContentRaw();
        String[] array = messageRaw.split(" ", 2);

        String substring = messageRaw.substring(8);
        System.out.println(substring);
        AudioSourceManagers.registerRemoteSources(musicController.getPlayerManager());

        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(musicController.getPlayer()));

        musicController.searchMusic("ytsearch:" + substring, event);
    }
}
