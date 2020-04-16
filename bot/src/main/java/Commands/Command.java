package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Command extends ListenerAdapter implements ICommand {
    private String helpText = "No help available for this command.";

    @Override
    public void execute(GuildMessageReceivedEvent event) {

    }

    @Override
    public String getHelp() {
        return helpText;
    }

    @Override
    public Permission getPerm() {
        return Permission.MESSAGE_READ;
    }


}