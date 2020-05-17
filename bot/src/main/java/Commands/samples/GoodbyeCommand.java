package Commands.samples;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GoodbyeCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("goodbye!").queue();
    }
}
