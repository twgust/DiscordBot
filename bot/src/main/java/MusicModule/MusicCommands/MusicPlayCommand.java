package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicPlayCommand extends Command {
    private  MusicController musicController;

    public MusicPlayCommand(MusicController musicController){
        this.musicController = musicController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event){
        Guild server = event.getGuild();
        Message message = event.getMessage();
        Member user = message.getMember();
        String messageRaw = message.getContentRaw();

        String[] array = messageRaw.split(" ", 2);
        String substring = messageRaw.substring(6);

        musicController.loadMusic("ytsearch:" + substring, user, event);


    }
}
