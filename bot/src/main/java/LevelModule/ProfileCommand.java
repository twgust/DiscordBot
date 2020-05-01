package LevelModule;

import Commands.Command;
import ModerationModule.GetMember;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ProfileCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        int startIndex = event.getMessage().getContentRaw().trim().indexOf(" ");
        if (startIndex != -1){
            member = GetMember.get(event.getMessage().getContentRaw().substring(startIndex).trim(), event.getChannel(), event.getMember());
        }
        LevelController.checkMember(event.getGuild(), member);
        event.getChannel().sendMessage(LevelController.getUserInfo(event.getGuild(), member)).queue();
    }
}
