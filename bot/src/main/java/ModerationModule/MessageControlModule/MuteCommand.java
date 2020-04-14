package ModerationModule.MessageControlModule;

import ModerationModule.ModCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import static ModerationModule.ModerationController.getLogChannel;

public class MuteCommand extends ModCommand {
    String muteRoleName = "%BotMuted";

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        int roleIndex = -1;
        for (int i = 0; i < channel.getGuild().getRoles().size(); i++) {
            if (channel.getGuild().getRoles().get(i).getName().equals(muteRoleName)) {
                roleIndex = i;
            }
        }
        if (roleIndex == -1) {
            channel.getGuild().createRole().setName("%BotMuted").queue();
            channel.sendMessage("Role created. Please try again.").queue();

        } else {
            channel.getGuild().addRoleToMember(member, channel.getGuild().getRoles().get(roleIndex)).queue();
            channel.sendMessage(member.getEffectiveName() + " was muted!").queue();
            if(getLogChannel() != null) getLogChannel().sendMessage(member.getEffectiveName() + " was muted.").queue();
        }
    }
}
