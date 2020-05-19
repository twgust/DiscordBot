package Main;

import Commands.*;
import EconomyModule.EconomyController;
import EconomyModule.GamesModule.SlotsCommand;
import EconomyModule.TransferCommand;
import EconomyModule.WalletCommand;
import LastfmModule.LastFmCommand;
import LevelModule.AddLevelRoleCommand;
import LevelModule.ProfileCommand;
import ModerationModule.*;
import ModerationModule.BanKickModule.BanCommand;
import ModerationModule.BanKickModule.KickCommand;
import ModerationModule.BanKickModule.UnBanCommand;
import ModerationModule.InfoModule.HelpCommand;
import ModerationModule.InfoModule.InfoCommand;
import ModerationModule.MessageControlModule.LockCommand;
import ModerationModule.MessageControlModule.MuteCommand;
import ModerationModule.MessageControlModule.PruneCommand;
import ModerationModule.MessageControlModule.UnlockCommand;
import MusicModule.MusicCommands.*;
import MusicModule.*;
import QuizModule.QuizCommand;
import WeatherModule.WeatherCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import LevelModule.LevelListener;

import javax.security.auth.login.LoginException;
import java.io.IOException;


/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID..
 */
public class Controller {
    private CommandMap cmdMap = new CommandMap();
    private ErrorCommand error = new ErrorCommand();
    private EventWaiter waiter;
    private Token token;
    private MusicController musicController;

    private QuizCommand quizCommand;
    private ModerationController modCtrl = new ModerationController(this);
    private EconomyController economyController;

    public Controller() throws LoginException, IOException {
        token = new Token();
        JDA jda = new JDABuilder(token.getToken()).build();
        waiter = new EventWaiter();
        musicController = new MusicController(waiter);
        quizCommand = new QuizCommand();
        economyController = new EconomyController();

        jda.addEventListener(new EventListener(this));
        jda.addEventListener(new LevelListener(economyController));
        jda.addEventListener(new LastFmCommand(waiter, musicController));
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
            if (cmdMap.containsKey(arguments[0]) && cmdMap.get(arguments[0]) instanceof Command) {

                if (!event.getMember().hasPermission(((Command) cmdMap.get(arguments[0])).getPerm())) {
                    event.getChannel().sendMessage("You do not have the permission to use that command.").queue();
                    return;
                }

                int needHelp = event.getMessage().getContentRaw().indexOf(" ");
                if (needHelp == -1 && !arguments[0].equalsIgnoreCase("hello") && !arguments[0].equalsIgnoreCase("lock")
                        && !arguments[0].equalsIgnoreCase("unlock") && !arguments[0].equalsIgnoreCase("goodbye")
                        && !arguments[0].equalsIgnoreCase("ping") && !arguments[0].equalsIgnoreCase("fm")
                        && !arguments[0].equalsIgnoreCase("queue") && !arguments[0].equalsIgnoreCase("skip")
                        && !arguments[0].equalsIgnoreCase("pause") && !arguments[0].equalsIgnoreCase("resume")
                        && !arguments[0].equalsIgnoreCase("play") && !arguments[0].equalsIgnoreCase("current")
                        && !arguments[0].equalsIgnoreCase("playing") && !arguments[0].equalsIgnoreCase("song")
                        && !arguments[0].equalsIgnoreCase("profile") && !arguments[0].equalsIgnoreCase("oldmusic")
                        && !arguments[0].equalsIgnoreCase("wallet") && !arguments[0].equalsIgnoreCase("music")
                        && !arguments[0].equalsIgnoreCase("stop")){

                    event.getChannel().sendMessage(((Command) cmdMap.get(arguments[0])).getHelp()).queue();
                    return;
                }
                arguments[0] = arguments[0].toLowerCase();
                ((Command) cmdMap.get(arguments[0])).execute(event);
            } else error.throwMissingCommand(event);
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
        cmdMap.put("fm", new LastFmCommand(waiter, musicController));

        //music commands
        cmdMap.put("queue", new MusicQueueCommand(musicController));
        cmdMap.put("skip", new MusicSkipCommand(musicController));
        cmdMap.put("pause", new MusicPauseCommand(musicController));
        cmdMap.put("resume", new MusicResumeCommand(musicController));
        cmdMap.put("play", new MusicPlayCommand(musicController));
        cmdMap.put("current", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("playing", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("song", new MusicCurrentlyPlayingCommand(musicController));
        cmdMap.put("search", new MusicSearchCommand(musicController, waiter));
        //deprecated command, only used for displaying differences between embedded and normal message
        cmdMap.put("oldmusic", new MusicCommand());
        cmdMap.put("music", new MusicHelp());
        cmdMap.put("stop", new MusicStopCommand(musicController));



        cmdMap.put("quiz", quizCommand);
        cmdMap.put("profile", new ProfileCommand());
        cmdMap.put("addlevelrole", new AddLevelRoleCommand());

        //Moderation commands
        cmdMap.put("lock", new LockCommand(modCtrl));
        cmdMap.put("unlock", new UnlockCommand(modCtrl));
        cmdMap.put("info", new InfoCommand(modCtrl));
        cmdMap.put("mute", new MuteCommand(modCtrl));
        cmdMap.put("prune", new PruneCommand(modCtrl));
        cmdMap.put("ban", new BanCommand(modCtrl));
        cmdMap.put("unban", new UnBanCommand(modCtrl));
        cmdMap.put("kick", new KickCommand(modCtrl));
        cmdMap.put("help", new HelpCommand(modCtrl, this));

        //Economy commands

        cmdMap.put("wallet", new WalletCommand(economyController));
        cmdMap.put("transfer", new TransferCommand(economyController));
        cmdMap.put("slots", new SlotsCommand(economyController));
    }

    public CommandMap getCmdMap() {
        return cmdMap;
    }


}
