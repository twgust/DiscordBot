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
    String str = "\n" +  "\n";
    int i = 1;
    str += "Now playing: " + musicController.getPlayer().getPlayingTrack().getInfo().title + "\n";
    while(itr.hasNext()){

      str +=  i + ") " + itr.next().getInfo().title +"\n";
      i++;
    }

    EmbedBuilder builder = new EmbedBuilder();
    builder.setColor(Color.YELLOW);
    builder.setTitle("Current queue: ");
    builder.setDescription(str);
    builder.setFooter("%music for help");
    event.getChannel().sendMessage(builder.build()).queue();
  }
}
