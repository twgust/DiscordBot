package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {
    public void execute(GuildMessageReceivedEvent event);
    public EmbedBuilder getHelp();
    public Permission getPerm();
}
