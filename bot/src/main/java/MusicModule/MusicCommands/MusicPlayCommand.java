package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import MusicModule.Controller.MusicController;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicPlayCommand extends Command {
    private MusicController musicController;

    public MusicPlayCommand(MusicController musicController){
        this.musicController = musicController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event){
        Guild server = event.getGuild();
        Message message = event.getMessage();
        Member user = message.getMember();
        String messageRaw = message.getContentRaw();

        //String manipulation to get identifier from the command message
        String[] array = messageRaw.split(" ", 2);
        String substring = messageRaw.substring(6);


        System.out.println("IDENTIFIER: " + substring);

        //a URL should not use YTSearch function
        if(substring.contains("youtube.com") || substring.contains("youtu.be")){
                musicController.youtubeTrackLoaded(substring, user, event);
        }
        else musicController.youtubeTrackLoaded("ytsearch:" + substring, user, event);
        //default case

    }
}
