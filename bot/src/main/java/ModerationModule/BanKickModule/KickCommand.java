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

public class KickCommand extends ModCommand {
    private Permission perm = Permission.KICK_MEMBERS;
    private EmbedBuilder eb = new EmbedBuilder();

    public KickCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){
        channel.sendMessage("User " + member.getUser().getName() + " was kicked").queue();
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + member.getUser().getName() + " was kicked").queue();
        member.kick(text).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83E\uDDB5 Moderation Module - Kick \uD83E\uDDB5", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/BanKickModule");
        eb.setDescription("Kick Users!");
        eb.addField("<Command Placeholder>", "- Description Placeholder", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.getHSBColor(102,0,153));
        return eb;
    }
}
