package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuteCommand extends Command {
    String muteRoleName = "%BotMuted";

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int roleIndex = -1;
        for (int i = 0; i < event.getGuild().getRoles().size(); i++) {
            if (event.getGuild().getRoles().get(i).getName().equals(muteRoleName)) {
                roleIndex = i;
            }
        }
        event.getGuild().addRoleToMember(GetMember.get(event), event.getGuild().getRoles().get(roleIndex)).queue();
    }
}
