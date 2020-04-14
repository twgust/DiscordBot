package ModerationModule.InfoModule;

import ModerationModule.ModCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand extends ModCommand {
    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.sendMessage(help()).queue();
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(help()).queue();
    }
}
