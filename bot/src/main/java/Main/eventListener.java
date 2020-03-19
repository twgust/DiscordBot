package Main;

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

    /**
     * kallas varje gång ett meddelande tas emot från en användare. Om meddelandet inte är skrivet av botten och har
     * prefixet '%' anropas processMessage() metoden.
     * @param event
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        if (!event.getMember().getUser().isBot() && msg.charAt(0) == prefix && event.getMessage().getAttachments().isEmpty()){
            ctrl.processMessage(event);
        }
    }
}
