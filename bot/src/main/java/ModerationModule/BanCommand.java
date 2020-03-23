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

        int delDays = 0;
        String reason = "";

        try{delDays = Integer.parseInt(arguments[2]);}catch(Exception e){}
        try{reason = arguments[3];}catch(Exception e){}

        Member banTarget = event.getGuild().getMemberByTag(arguments[1]);
        event.getChannel().sendMessage("Member " + banTarget.getEffectiveName() + " was banned.").queue();
        banTarget.ban(delDays, reason).queue();
    }
}
