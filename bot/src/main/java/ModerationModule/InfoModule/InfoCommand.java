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
import java.util.EnumSet;
import java.util.List;

public class InfoCommand extends ModCommand {
    private Permission perm = Permission.MESSAGE_MANAGE;
    public InfoCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        eb.clear();
        if (member == null) return;
        List<Role> infoRoles = member.getRoles();
        String roles ="";
        for (int i = 0; i < infoRoles.size(); i++) {
            roles += infoRoles.get(i).getName() + "\n";
        }
        eb.setTitle("User Information");
        eb.setThumbnail(member.getUser().getAvatarUrl());
        eb.addField("__**Member name:**__", member.getEffectiveName(), false);
        eb.addField("__**Member ID:**__", member.getId(), false);
        eb.addField("__**Roles**__", roles, false);
        eb.addField("__**Permissions:**__", member.getPermissions().toString(), false);
        eb.addField("__**Member joined discord:**__", member.getTimeCreated().toString(), false);
        eb.setColor(member.getColor());
        channel.sendMessage(eb.build()).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("ℹ️ Moderation Module - Info ℹ️", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/InfoModule");
        eb.setDescription("Show user information!");
        eb.addField("info [user]", "- Shows information about the user", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.getHSBColor(102,0,153));
        return eb;
    }
}
