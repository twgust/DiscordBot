package ModerationModule.MessageControlModule;

import ModerationModule.GetMember;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

import static ModerationModule.ModerationController.getLogChannel;

public class PruneCommand extends ModCommand {
    private Permission perm = Permission.MESSAGE_MANAGE;
    public PruneCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        if (member == null) {
            List<Message> messages = channel.getIterableHistory().complete();
            if (messages.size() - 1 < num) num = messages.size();
            for (int i = 0; i < num + 1; i++) {
                messages.get(i).delete().queue();
            }
        } else {
            List<Message> messages = channel.getIterableHistory().complete();
            if (messages.size() - 1 < num) num = messages.size();
            int j = 0;
            for (int i = 0; i < num + 1; j++) {
                if (messages.get(i).getMember().equals(member)) messages.get(i).delete().queue();
            }
        }
        if(getLogChannel() != null) getLogChannel().sendMessage("Pruned " + num + " messages in " + channel.getName()).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }
}
