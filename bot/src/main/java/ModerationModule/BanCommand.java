package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class BanCommand extends Command {
    private Controller ctrl;

    public BanCommand(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        System.out.println("im in");
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

        int delDays = 0;
        String reason = "";

        try {
            delDays = Integer.parseInt(arguments[2]);
        } catch (Exception e) {
        }
        try {
            for (int i = 3; i < arguments.length; i++) {
                reason += arguments[i];
                if (i < arguments.length-1) reason += " ";
            }
        } catch (Exception e) {
        }

        List<Member> banTargets = event.getGuild().getMembersByName(arguments[1], false);
        if(banTargets.size() > 1){
            String msg = "There are more then one user by that name";
            event.getChannel().sendMessage(msg).queue();
        } else{
            event.getChannel().sendMessage("Member " + banTargets.get(0).getEffectiveName() + " was banned.").queue();

            try{ctrl.getLogChannel().sendMessage("Member " + banTargets.get(0).getEffectiveName() + " was banned by " + event.getMessage().getAuthor().getName()
                    + "\nReason: " + reason).queue();}
            catch (Exception e){}

            banTargets.get(0).ban(delDays, reason).queue();
        }
        /*
        */
    }
}
