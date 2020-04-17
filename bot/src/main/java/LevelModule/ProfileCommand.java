package LevelModule;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ProfileCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(LevelController.getUserInfo(event.getGuild(), event.getMember())).queue();
    }
}
