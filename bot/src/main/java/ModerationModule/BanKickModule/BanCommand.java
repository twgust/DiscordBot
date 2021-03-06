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
    public void execute(TextChannel channel, Member member, String text, int num) {
        if (num < 0 ) {
            num = 0;
        }
            eb.clear();
            eb.setTitle("Member " + member.getUser().getName() + " was banned.");
            eb.setColor(Color.YELLOW);
            channel.sendMessage(eb.build()).queue();
            member.ban(num, text).queue();
            if(getLogChannel() != null) {
                eb.clear();
                eb.setTitle("User " + member.getUser().getName() + " was banned");
                eb.setColor(Color.YELLOW);
                getLogChannel().sendMessage(eb.build()).queue();
            }
    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Moderation Module - Ban \uD83D\uDC80", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/BanKickModule");
        eb.setDescription("Ban Users!");
        eb.addField("ban [user]", "- Ban user from the server", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
