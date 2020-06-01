package ModerationModule.MessageControlModule;

import ModerationModule.GetMember;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

import static ModerationModule.ModerationController.getLogChannel;

public class PruneCommand extends ModCommand {
    private Permission perm = Permission.MESSAGE_MANAGE;
    public PruneCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private EmbedBuilder eb = new EmbedBuilder();


    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        if (member == null) {
            List<Message> messages = channel.getIterableHistory().complete();
            if (messages.size() - 1 < num) num = messages.size();
            if (text != "") {
                int j = 0;
                for (int i = 0; i < num + 1; j++) {
                    if (messages.get(j).getContentRaw().indexOf(text) != -1) {
                        messages.get(j).delete().queue();
                        i++;
                    }
                }
            } else {
                for (int i = 0; i < num + 1; i++) {
                    messages.get(i).delete().queue();
                }
            }
        } else {
            List<Message> messages = channel.getIterableHistory().complete();
            if (messages.size() - 1 < num) num = messages.size();
            int j = 0;
            if (text != "") {
                for (int i = 0; i < num + 1; j++) {
                    if (messages.get(j).getContentRaw().indexOf(text) != -1 && messages.get(j).getMember().equals(member)) {
                        messages.get(j).delete().queue();
                        i++;
                    }
                }
            } else {
                for (int i = 0; i < num + 1; j++) {
                    if (messages.get(j).getMember().equals(member)) {
                        messages.get(j).delete().queue();
                        i++;
                    }
                }
            }
        }
        if (getLogChannel() != null)
            getLogChannel().sendMessage("Pruned " + num + " messages in " + channel.getName()).queue();
    }

    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("\uD83D\uDC80 Moderation Module - Prune \uD83D\uDC80", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/MessageControlModule");
        eb.setDescription("Prune messages!");
        eb.addField("Placeholder", "- Placeholder", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
