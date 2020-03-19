package Commands;

import Main.eventListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PrefixCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String recievedMessage = event.getMessage().getContentRaw().substring(8);
        Member member = event.getMember();

        if (member != null) {
            if (member.hasPermission(Permission.ADMINISTRATOR) ||  member.getUser().getId().equalsIgnoreCase("110372734118174720")) {
                if (recievedMessage.length() > 1) {
                    event.getChannel().sendMessage("Prefix has to be 1 letter").queue();
                } else if (recievedMessage.length() == 0) {
                    event.getChannel().sendMessage("You didn't enter a prefix").queue();
                } else {
                    eventListener.setPrefix(recievedMessage.charAt(0));
                    event.getChannel().sendMessage("The prefix is now " + "'" + recievedMessage + "'").queue();
                }
            }
            else event.getChannel().sendMessage("You do not have these privileges").queue();
        }


    }
}
