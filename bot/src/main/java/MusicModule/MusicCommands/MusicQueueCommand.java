package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import MusicModule.Controller.MusicController;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Iterator;

public class MusicQueueCommand extends Command {
  private MusicController musicController;


  public MusicQueueCommand(MusicController musicController){
    this.musicController = musicController;
  }

  @Override
  public void execute(GuildMessageReceivedEvent event){
    Iterator<AudioTrack> itr = musicController.getScheduler().getQueue().iterator();
    int i = 1;
    EmbedBuilder builder = new EmbedBuilder();
    StringBuilder str = new StringBuilder();
    AudioTrack nowPlayingTrack = musicController.getPlayer().getPlayingTrack();

    String nowPlayingTrackTitle = nowPlayingTrack.getInfo().title;
    String nowPlayingTrackURI = nowPlayingTrack.getInfo().uri;
    str.append("Now playing : [").append(nowPlayingTrackTitle)
            .append("](").append(nowPlayingTrackURI).append(")").append("\n\n");

    //iterates through the queue to add track data to the %queue printout
    while(itr.hasNext() && i <= 5){
      AudioTrack track = itr.next();
      String trackName = track.getInfo().title;
      String trackUrl= track.getInfo().uri;
      str.append("Track ").append(i).append(" : [").append(trackName).append("](").append(trackUrl).append(")")
              .append("\n").append(musicController.timeFormatting(track.getDuration())).append("\n\n");
      i++;
    }
    //Limiting how much information is going to be in one message
    if(i > 5){
      str.append("More Tracks are queued but not yet visible!");
    }

    builder.setColor(Color.YELLOW);
    builder.setTitle("Current queue: ");
    builder.setDescription(str);
    builder.setFooter("%music for help");
    event.getChannel().sendMessage(builder.build()).queue();
    builder.clear();
  }
}
