package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LockCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().getManager().putPermissionOverride(event.getGuild().getRoles().get(event.getGuild().getRoles().size()-1), 0, 2048).queue();

        event.getGuild().getRoles().get(event.getGuild().getRoles().size()-1).getPermissions(event.getChannel()).forEach(perm -> {
            System.out.println(perm.getName());
            System.out.println(perm.getRawValue());
        });
    }
}
