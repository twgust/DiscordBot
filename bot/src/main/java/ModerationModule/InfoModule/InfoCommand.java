package ModerationModule.InfoModule;

import ModerationModule.ModCommand;

import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class InfoCommand extends ModCommand {
    private Permission perm = Permission.MESSAGE_MANAGE;
    public InfoCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private EmbedBuilder eb = new EmbedBuilder();

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
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83E\uDDB5 Moderation Module - Info \uD83E\uDDB5", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/InfoModule");
        eb.setDescription("Show user information!");
        eb.addField("<%info user>", "- Description Placeholder", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.getHSBColor(102,0,153));
        return eb;
    }
}
