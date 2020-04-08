package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class InfoCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Member member = GetMember.get(event);
        if (member != null) {
            List<Role> infoRoles = member.getRoles();
            String infoMsg = "__**Member name:**__ " + member.getEffectiveName() + "\n__**Member ID:**__ " + member.getId();
            infoMsg += "\n__**Roles:**__\n";
            for (int i = 0; i < infoRoles.size(); i++) {
                infoMsg += infoRoles.get(i).getName() + "\n";
            }
            infoMsg += "__**Permissions:**__\n";
            Permission[] perms = (Permission[]) member.getPermissions().toArray();
            for (int i = 0; i < perms.length; i++) {
                infoMsg += perms[i].getName() + ":" + perms[i].getRawValue() + "\n";
            }
            infoMsg += "\n__**Member joined:**__ " + member.getTimeCreated();
        }
    }
}
