package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.AudioPlayerSendHandler;
import MusicModule.MusicController;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicPlayCommand extends Command {
    private  MusicController controller;

    public MusicPlayCommand(MusicController controller){
        this.controller = controller;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event){


        Guild server = event.getGuild();
        Message messageTrue = event.getMessage();
        Member user = messageTrue.getMember();
        String message = messageTrue.getContentRaw();
        String[] array = message.split(" ", 2);


        AudioSourceManagers.registerRemoteSources(controller.getPlayerManager());

        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(controller.getPlayer()));

        controller.loadMusic(array[1], user, event);

    }
}
