package Main;

import Commands.*;
import LastfmModule.LastFmCommand;
import ModerationModule.*;
import MusicModule.MusicCommands.*;
import MusicModule.*;
import QuizModule.QuizCommand;
import WeatherModule.WeatherCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;
import java.io.IOException;


/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID..
 */
public class Controller {
    private CommandMap cmdMap = new CommandMap();
    private ErrorCommand error = new ErrorCommand();
    private EventWaiter waiter;
    private QuizCommand quizCommand;
    private Token token;
    private MusicController musicController;
    private ModerationController modCtrl = new ModerationController();

    public Controller() throws LoginException, IOException {

        token = new Token();
        JDA jda = new JDABuilder(token.getToken()).build();
        waiter = new EventWaiter();
        quizCommand = new QuizCommand();
        musicController = new MusicController();


        jda.addEventListener(new EventListener(this));
        jda.addEventListener(new LastFmCommand(waiter));
        jda.addEventListener(waiter);
        jda.addEventListener(quizCommand);
        addCommands();
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
        } catch (Exception e) {
            error.throwFailedMessageProcessing(event);
            System.out.println(e.getMessage());
        }
    }

    private void addCommands() {
        cmdMap.put("hello", new HelloCommand());
        cmdMap.put("goodBye", new GoodbyeCommand());
        cmdMap.put("ping", new PingCommand());
        cmdMap.put("weather", new WeatherCommand());
        cmdMap.put("prefix", new PrefixCommand());
        cmdMap.put("fm", new LastFmCommand(waiter));
        cmdMap.put("queue", new MusicQueueCommand(musicController));
        cmdMap.put("skip", new MusicSkipCommand(musicController));
        cmdMap.put("pause", new MusicPauseCommand(musicController));
        cmdMap.put("resume", new MusicResumeCommand(musicController));
        cmdMap.put("play", new MusicPlayCommand(musicController));
        cmdMap.put("current", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("playing", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("song", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("quiz", quizCommand);

        //Moderation commands
        cmdMap.put("lock", modCtrl);
        cmdMap.put("unlock", modCtrl);
        cmdMap.put("info", modCtrl);
        cmdMap.put("mute", modCtrl);
        cmdMap.put("prune", modCtrl);
        cmdMap.put("ban", modCtrl);
        cmdMap.put("unban", modCtrl);
        cmdMap.put("kick", modCtrl);
        cmdMap.put("help", modCtrl);
        cmdMap.put("setlogchannel", modCtrl);
    }
}
