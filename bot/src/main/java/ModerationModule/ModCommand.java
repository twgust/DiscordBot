package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


public class ModCommand extends Command {
    private ModerationController modCTRL;
    public ModCommand(ModerationController modCTRL){
        this.modCTRL = modCTRL;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {

    }

    public void execute(TextChannel channel, Member member, String text, int num) {

    }

    public String help(){
        return "No help available for this command";
    }
}
