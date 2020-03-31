package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand extends Command {
    private Controller ctrl;

    public BanCommand(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");
        //Variables
        int delDays = 0;
        String reason = "";
        Member member;
        //Gets member from tag or id
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
        //Gets delDays and reason
        try {
            delDays = Integer.parseInt(arguments[2]);
            try {
                for (int i = 3; i < arguments.length; i++) {
                    reason += arguments[i];
                    if (i < arguments.length - 1) reason += " ";
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
            //Gets reason if there was no delDays
            try {
                for (int i = 2; i < arguments.length; i++) {
                    reason += arguments[i];
                    if (i < arguments.length - 1) reason += " ";
                }
            } catch (Exception e2) {
            }
        }
        //Notifies the server and bans the member
        event.getChannel().sendMessage("Member " + member.getEffectiveName() + " was banned.").queue();
        try {
            ctrl.getLogChannel().sendMessage("Member " + member.getEffectiveName() + " was banned by " + event.getMessage().getAuthor().getName()
                    + "\nReason: " + reason).queue();
        } catch (Exception e) {
        }
        member.ban(delDays, reason).queue();
    }
}
