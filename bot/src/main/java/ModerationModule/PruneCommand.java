package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

public class PruneCommand extends Command {
    Controller ctrl;

    public PruneCommand(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");
        int delAmount = 0;

        try{
            delAmount = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            return;
        }
        List<Message> messages = event.getChannel().getIterableHistory().complete();
        if (messages.size() < delAmount) delAmount = messages.size();
        for (int i = 0; i < delAmount+1; i++) {
            messages.get(i).delete().queue();
        }

        try{
            ctrl.getLogChannel().sendMessage(event.getAuthor().getName() + " has pruned " + delAmount + " messages in " + event.getChannel().getName()).queue();
        }catch (Exception e){
        }
    }
}
