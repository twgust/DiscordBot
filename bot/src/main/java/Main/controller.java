package Main;

import Commands.*;
import WeatherModule.WeatherCommand;
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

    public controller() throws LoginException {
        addCommands();

        JDA jda = new JDABuilder("Njg3MjMxNTc3MDAwMTE2MjI0.XnMmBg.IcdqgV4zHDMHDGesLh2m-XY6X2U").build();

        jda.addEventListener(new eventListener(this));
    }

    /**
     * Denna metod skall söka och exekvera vilket kommando det är som är kallat på från användaren.
     *
     * @param event
     */
    public void processMessage(GuildMessageReceivedEvent event) {
        try {
            String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

            arguments[0] = arguments[0].substring(0, 1).toUpperCase() + arguments[0].substring(1);
            if (cmdMap.get(arguments[0]) instanceof Command || cmdMap.containsKey(arguments[0]))
                ((Command) cmdMap.get(arguments[0])).execute(event);
            else error.throwMissingCommand(event);
        }catch (Exception e){
            error.throwFailedMessageProcessing(event);
        }
    }

    private void addCommands() {
        cmdMap.put("Hello", new HelloCommand());
        cmdMap.put("GoodBye", new GoodbyeCommand());
        cmdMap.put("Ping", new PingCommand());
        cmdMap.put("Weather", new WeatherCommand());
    }
}
