import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class eventListener extends ListenerAdapter {
    public char getPrefix() {
        return prefix;
    }

    private char prefix = '%';
    private controller ctrl;

    public eventListener(controller ctrl){
        this.ctrl = ctrl;
    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        if (!event.getMember().getUser().isBot() && msg.charAt(0) == prefix){
            ctrl.processMessage(event);
        }
    }
}
