package Main;

import Commands.ErrorCommand;
import MusicModule.Music;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class EventListener extends ListenerAdapter {
    public char getPrefix() {
        return prefix;
    }

    public static char prefix = '%';
    private Controller ctrl;
    private ErrorCommand error = new ErrorCommand();
    private Music music;

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

    public void onGuildJoin(GuildJoinEvent joinEvent){
        joinEvent.getGuild().createRole().setName("%BotMuted").queue();
    }

    public void onRoleCreate(RoleCreateEvent roleCreateEvent){
        System.out.println("hi");
        if (roleCreateEvent.getRole().getName().equals("%BotMuted")) roleCreateEvent.getRole().getManager().revokePermissions(Permission.MESSAGE_WRITE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).queue();
    }

    public static void setPrefix(char inprefix) {
        prefix = inprefix;
    }
}
