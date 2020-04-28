package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Iterator;

public class MusicQueueCommand extends Command {
  private MusicController controller;


  public MusicQueueCommand(MusicController controller){
      this.controller = controller;
  }

  @Override
  public void execute(GuildMessageReceivedEvent event){
    Iterator<AudioTrack> itr = controller.getScheduler().getQueue().iterator();
    String str = "\nCurrent Queue: " + controller.getScheduler().getQueue().size() + "\n";

    while(itr.hasNext()){
      str += itr.next().getInfo().title +"\n";
    }

    event.getChannel().sendMessage("```"
            + str
            + "\nNow playing: " + controller.getPlayer().getPlayingTrack().getInfo().title
            + "\nLink: " + controller.getPlayer().getPlayingTrack().getInfo().uri
            + "``` ... More commands related to queue coming").queue();
  }
}


