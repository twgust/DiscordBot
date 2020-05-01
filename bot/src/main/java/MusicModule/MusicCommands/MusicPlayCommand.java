package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.AudioPlayerSendHandler;
import MusicModule.*;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
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
        Message message = event.getMessage();
        Member user = message.getMember();
        String messageRaw = message.getContentRaw();


        String[] array = messageRaw.split(" ", 2);
        String substring = messageRaw.substring(6);
        //prints
        System.out.println(substring);


        AudioSourceManagers.registerRemoteSources(controller.getPlayerManager());

        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(controller.getPlayer()));

        controller.loadMusic("ytsearch:" + substring, user, event);
        //controller.loadMusic(array[1], user, event);
        //SpotifyPlayer.loadMusic for spotify (switch case)

    }
}
