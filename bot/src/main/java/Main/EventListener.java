package Main;

import Commands.ErrorCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static ModerationModule.ModerationController.getLogChannel;

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
    public void onGuildJoin(GuildJoinEvent joinEvent){
        joinEvent.getGuild().createRole().setName("%BotMuted").queue();
    }

    public void onRoleCreate(RoleCreateEvent roleCreateEvent){
        try {
            getLogChannel().sendMessage("Role " + roleCreateEvent.getRole().getName() + " has been created.").queue();
        } catch (Exception e) {
        }
        if (roleCreateEvent.getRole().getName().equals("%BotMuted")) roleCreateEvent.getRole().getManager().
                revokePermissions(Permission.MESSAGE_WRITE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).queue();
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + event.getUser().getName() + " was banned.").queue();
    }

    public static void setPrefix(char inprefix) {
        prefix = inprefix;
    }
}
