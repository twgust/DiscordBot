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

        String reason = "";

        try {
            for (int i = 2; i < arguments.length; i++) {
                reason += arguments[i];
                if (i < arguments.length - 1) reason += " ";
            }
        } catch (Exception e) {}

        List<Member> KickTargets = event.getGuild().getMembersByName(arguments[1], false);

        if(KickTargets.size() > 1){
            String msg = "There are more then one user by that name";
            event.getChannel().sendMessage(msg).queue();
        } else{
            event.getChannel().sendMessage("Member " + KickTargets.get(0).getEffectiveName() + " was kicked.").queue();

            try{ctrl.getLogChannel().sendMessage("Member " + KickTargets.get(0).getEffectiveName() + " was kicked by " + event.getMessage().getAuthor().getName()
                    + "\nReason: " + reason).queue();}
            catch (Exception e){}

            KickTargets.get(0).kick(reason).queue();
        }
    }
}
