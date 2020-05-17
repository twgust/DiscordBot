package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Command extends ListenerAdapter implements ICommand {
    private EmbedBuilder helpText;

    public Command() {
        helpText = new EmbedBuilder().setTitle("No help available for this command");
        helpText.setColor(Color.ORANGE); //Temp color
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {

    }

    @Override
    public EmbedBuilder getHelp() {
        return helpText;
    }

    @Override
    public Permission getPerm() {
        return Permission.MESSAGE_READ;
    }


}