package ModerationModule.MessageControlModule;

import ModerationModule.ModCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LockCommand extends ModCommand {

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.getManager().putPermissionOverride(channel.getGuild().getRoles().get(channel.getGuild().getRoles().size()-1), 0, 2048).queue();
    }
}
