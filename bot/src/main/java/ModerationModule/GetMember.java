package ModerationModule;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GetMember {
    public static Member get(GuildMessageReceivedEvent event){
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");
        Member member;
        try {
            member = event.getGuild().getMemberById(Long.parseLong(arguments[1]));
        } catch (Exception e) {
            try {
                member = event.getGuild().getMemberById(arguments[1].substring(3, arguments[1].length() - 1));
            } catch (Exception e2) {
                return null;
            }
        }
        return member;
    }
}
