package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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

        Member kickTarget = event.getGuild().getMemberByTag(arguments[1]);
        event.getChannel().sendMessage("Member " + kickTarget.getEffectiveName() + " was kicked.").queue();
        try{ctrl.getLogChannel().sendMessage("Member " + kickTarget.getEffectiveName() + " was kicked by " + event.getMessage().getAuthor().getName()
                + "\nReason: " + reason).queue();}
        catch (Exception e){}
        kickTarget.kick(reason).queue();
    }
}
