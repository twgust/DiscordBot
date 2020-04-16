package ModerationModule.InfoModule;

import ModerationModule.ModCommand;

import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class InfoCommand extends ModCommand {
    private Permission perm = Permission.MESSAGE_MANAGE;
    public InfoCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        if (member == null) return;
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
        channel.sendMessage(infoMsg).queue();
    }

    public Permission getPerm() {
        return perm;
    }
}
