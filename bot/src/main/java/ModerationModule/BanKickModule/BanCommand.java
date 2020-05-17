package ModerationModule.BanKickModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

import static ModerationModule.ModerationController.getLogChannel;

public class BanCommand extends ModCommand {
    private Permission perm = Permission.BAN_MEMBERS;
    private EmbedBuilder eb = new EmbedBuilder();

    public BanCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){
        if (num < 0) num = 0;
        channel.sendMessage("Member " + member.getUser().getName() + " was banned.").queue();
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + member.getUser().getName() + " was banned").queue();
        member.ban(num, text).queue();

    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83D\uDC80 Moderation Module - Ban \uD83D\uDC80", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/BanKickModule");
        eb.setDescription("Kick Users!");
        eb.addField("<Command Placeholder>", "- Description Placeholder", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.getHSBColor(102,0,153));
        return eb;
    }
}
