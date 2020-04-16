package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicQueueCommand extends Command {
  private MusicController controller;


  public MusicQueueCommand(MusicController controller){
      this.controller = controller;
  }

  @Override
    public void execute(GuildMessageReceivedEvent event){
      event.getChannel().sendMessage("```Current queue: " + controller.getScheduler().getQueue().size()
      + "``` ... More commands related to queue coming").queue();

  }
}
