package ModerationModule;

import Commands.Command;
import Main.Controller;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetLogChannelCommand extends Command {
    private Controller ctrl;

    public SetLogChannelCommand(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        ctrl.setLogChannel(event.getChannel());
        event.getChannel().sendMessage("Channel " + event.getChannel().getName() + "set as log channel.").queue();
    }
}
