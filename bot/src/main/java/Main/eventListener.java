package Main;

import LastfmModule.LastFmCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class eventListener extends ListenerAdapter {
    private LastFmCommand lastFmCommand;

    public char getPrefix() {
        return prefix;
    }

    private static char prefix = '%';
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
        if (!event.getMember().getUser().isBot() && msg.charAt(0) == prefix){
            ctrl.processMessage(event);
        }
    }

    public static void setPrefix(char inprefix) {
        prefix = inprefix;
    }

}
