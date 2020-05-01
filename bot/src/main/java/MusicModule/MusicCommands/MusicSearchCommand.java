package MusicModule.MusicCommands;

import Commands.Command;
import Main.Controller;
import MusicModule.AudioPlayerSendHandler;
import MusicModule.MusicController;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class MusicSearchCommand extends Command {
    MusicController controller;
    EventWaiter waiter;

    public MusicSearchCommand(MusicController controller, EventWaiter waiter){
        this.controller = controller;
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
        AudioSourceManagers.registerRemoteSources(controller.getPlayerManager());

        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(controller.getPlayer()));
        String s = "ytsearch:" + substring;
        controller.searchMusic("ytsearch:" + substring, event);

        //Chosen song index should be set to the corresponding emote reaction

//        int chosenSongIndex = 0;
//        String chosenSongString = "";
//        switch (chosenSongIndex){
//            case 0:
//                controller.loadMusic("ytsearch:" + controller.getListOfTracks().get(0).getInfo().title, user,event);
//                break;
//            case 1:
//                controller.loadMusic("ytsearch:" + controller.getListOfTracks().get(1).getInfo().title, user,event);
//                break;
//            case 2:
//                controller.loadMusic("ytsearch:" + controller.getListOfTracks().get(2).getInfo().title, user, event);
//                break;
//            case 3:
//                controller.loadMusic("ytsearch:" + controller.getListOfTracks().get(3).getInfo().title, user,event);
//                break;
//        }
    }
}
