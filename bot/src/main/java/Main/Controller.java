package Main;

import Commands.*;
import ModerationModule.BanCommand;
import ModerationModule.KickCommand;
import ModerationModule.PruneCommand;
import ModerationModule.SetLogChannelCommand;
import WeatherModule.WeatherCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;

/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID..
 */
public class Controller {
    private CommandMap cmdMap = new CommandMap();
    private ErrorCommand error = new ErrorCommand();
    private TextChannel logChannel;

    public Controller() throws LoginException {
        addCommands();

        JDA jda = new JDABuilder("Njc3ODY4NjM4OTAwMDYwMTYx.XoJ49w.7N-69zC_Z1baAatgex-DdrF_iys").build();

        jda.addEventListener(new EventListener(this));
    }

    /**
     * Denna metod skall söka och exekvera vilket kommando det är som är kallat på från användaren.
     *
     * @param event
     */
    public void processMessage(GuildMessageReceivedEvent event) {
        try {
            String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

            arguments[0] = arguments[0].toLowerCase();
            if (cmdMap.get(arguments[0]) instanceof Command || cmdMap.containsKey(arguments[0]))
                ((Command) cmdMap.get(arguments[0])).execute(event);
            else error.throwMissingCommand(event);
        }catch (Exception e){
            error.throwFailedMessageProcessing(event);
        }
    }

    private void addCommands() {
        cmdMap.put("hello", new HelloCommand());
        cmdMap.put("goodBye", new GoodbyeCommand());
        cmdMap.put("ping", new PingCommand());
        cmdMap.put("weather", new WeatherCommand());
        cmdMap.put("ban", new BanCommand(this));
        cmdMap.put("kick", new KickCommand(this));
        cmdMap.put("setlogchannel", new SetLogChannelCommand(this));
        cmdMap.put("prune", new PruneCommand(this));
    }

    public TextChannel getLogChannel() {
        return logChannel;
    }

    public void setLogChannel(TextChannel logChannel) {
        this.logChannel = logChannel;
    }
}
