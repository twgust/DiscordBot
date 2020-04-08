package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnlockCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().getManager().putPermissionOverride(event.getGuild().getRoles().get(event.getGuild().getRoles().size()-1), 2048, 0).queue();
    }
}