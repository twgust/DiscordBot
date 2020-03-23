package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//git
public class StopCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("stopping...").queue();
    }
}
