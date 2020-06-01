package ModerationModule.MessageControlModule;

import Commands.Command;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class UnlockCommand extends ModCommand {
    private Permission perm = Permission.MANAGE_CHANNEL;
    public UnlockCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.getManager().putPermissionOverride(channel.getGuild().getRoles().get(channel.getGuild().getRoles().size()-1), 2048, 0).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("\uD83D\uDC80 Moderation Module - Lock \uD83D\uDC80", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/MessageControlModule");
        eb.setDescription("Unlock channels!");
        eb.addField("unlock", "- Unlocks the current channel", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}