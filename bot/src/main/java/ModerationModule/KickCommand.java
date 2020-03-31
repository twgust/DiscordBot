package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class KickCommand extends Command {
    private Controller ctrl;

    public KickCommand(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");
        //Variables
        String reason = "";
        Member member;
        //Gets member by tag or id
        try {
            member = event.getGuild().getMemberById(Long.parseLong(arguments[1]));
        } catch (Exception e) {
            try {
                member = event.getGuild().getMemberById(arguments[1].substring(3, arguments[1].length() - 1));
            } catch (Exception e2) {
                event.getChannel().sendMessage("Invalid target user").queue();
                return;
            }
        }
        //Gets reason
        try {
            for (int i = 2; i < arguments.length; i++) {
                reason += arguments[i];
                if (i < arguments.length - 1) reason += " ";
            }
        } catch (Exception e2) {
        }
        //Notifies the server and kicks the member
        event.getChannel().sendMessage("Member " + member.getEffectiveName() + " was kicked.").queue();
        try {
            ctrl.getLogChannel().sendMessage("Member " + member.getEffectiveName() + " was kicked by " + event.getMessage().getAuthor().getName()
                    + "\nReason: " + reason).queue();
        } catch (Exception e) {
        }
        member.kick(reason).queue();
    }
}
