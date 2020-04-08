package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MuteCommand extends Command {
    String muteRoleName = "%BotMuted";
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Role[] roles = (Role[])(event.getGuild().getRoles().toArray());
        for (int i = 0; i < roles.length; i++) {
            if(roles[i].getName().equals(muteRoleName)){
                event.getGuild().createRole().setName(muteRoleName).setPermissions();
            }
        }
    }
}
