package Main;

import Commands.*;
import LastfmModule.LastFmCommand;
import LastfmModule.LastFmCommandOldv2;
import LastfmModule.TestingClass;
import WeatherModule.WeatherCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;

/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID..
 */
public class controller {
    private CommandMap cmdMap = new CommandMap();
    private ErrorCommand error = new ErrorCommand();
    private EventWaiter waiter;

    public controller() throws LoginException {


        JDA jda = new JDABuilder(/*"Njg3MjMxNTc3MDAwMTE2MjI0.Xmi1Qw.YWg2zrgmgaPk-hcnD1q93a3Ot1E"*/
        "Njc4MDM3ODcwNTMxMDUxNTMx.XnPejA.xLQuDmGNgypXPC1gu54_D-D2N-s").build();
        waiter = new EventWaiter();
        jda.addEventListener(new eventListener(this));
        jda.addEventListener(new LastFmCommand(waiter));
        jda.addEventListener(waiter);
        addCommands();
    }

    /**
     * Denna metod skall söka och exekvera vilket kommando det är som är kallat på från användaren.
     *
     * @param event
     */
    public void processMessage(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

        arguments[0] = arguments[0].substring(0, 1).toUpperCase() + arguments[0].substring(1);
        if (cmdMap.get(arguments[0]) instanceof Command || cmdMap.containsKey(arguments[0]))
            ((Command) cmdMap.get(arguments[0])).execute(event);
        else error.throwMissingCommand(event);
    }

    private void addCommands() {
        cmdMap.put("Hello", new HelloCommand());
        cmdMap.put("GoodBye", new GoodbyeCommand());
        cmdMap.put("Ping", new PingCommand());
        cmdMap.put("Weather", new WeatherCommand());
        cmdMap.put("Prefix", new PrefixCommand());
        cmdMap.put("Fm", new LastFmCommand(waiter));
    }
}
