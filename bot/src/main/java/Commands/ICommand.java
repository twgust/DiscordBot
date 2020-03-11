package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand extends Comparable {
    public void execute(GuildMessageReceivedEvent event);
}
