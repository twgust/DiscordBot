package Main;

import Commands.ErrorCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
    public char getPrefix() {
        return prefix;
    }

    public static char prefix = '%';
    private Controller ctrl;
    private ErrorCommand error = new ErrorCommand();
    public EventListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    /**
     * kallas varje gång ett meddelande tas emot från en användare. Om meddelandet inte är skrivet av botten och har
     * prefixet '%' anropas processMessage() metoden.
     *
     * @param event
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        try {
            String msg = event.getMessage().getContentRaw();
            if (!event.getMember().getUser().isBot() && msg.charAt(0) == prefix) {
                ctrl.processMessage(event);
            }
        } catch (Exception e){
        }
    }

    public static void setPrefix(char inprefix) {
        prefix = inprefix;
    }

}
