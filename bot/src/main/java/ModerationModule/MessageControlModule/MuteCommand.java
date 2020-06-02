package ModerationModule.MessageControlModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

import static ModerationModule.ModerationController.getLogChannel;

public class MuteCommand extends ModCommand {
    String muteRoleName = "%BotMuted";
    private Permission perm = Permission.MESSAGE_MANAGE;
    private EmbedBuilder eb = new EmbedBuilder();

    public MuteCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

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
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Moderation Module - Mute \uD83D\uDC80", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/MessageControlModule");
        eb.setDescription("Mute Users!");
        eb.addField("mute [user]", "- Mute the user", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
