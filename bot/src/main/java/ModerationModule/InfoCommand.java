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
            String infoMsg = "__**Member name:**__ \n" + member.getEffectiveName() + "\n__**Member ID:**__ \n" + member.getId();
            infoMsg += "\n__**Roles:**__\n";
            for (int i = 0; i < infoRoles.size(); i++) {
                infoMsg += infoRoles.get(i).getName() + "\n";
            }
            infoMsg += "__**Permissions:**__\n";
            infoMsg += member.getPermissions();
            member.getPermissions().forEach(perm -> System.out.println(perm.getName() + " : " + perm.getRawValue()));
            infoMsg += "\n__**Member joined discord:**__ \n" + member.getTimeCreated();
            event.getChannel().sendMessage(infoMsg).queue();
        }
    }
}
