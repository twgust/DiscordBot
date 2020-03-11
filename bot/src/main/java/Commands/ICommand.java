package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {
    public void execute(GuildMessageReceivedEvent event);
}
