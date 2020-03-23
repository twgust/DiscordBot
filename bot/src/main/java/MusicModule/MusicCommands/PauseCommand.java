package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PauseCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("Pausing...").queue();

    }
}
