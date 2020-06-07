package QuizModule;

import Commands.Command;
import Main.EventListener;
import QuizModule.QuizMulti.QuizMulti;
import QuizModule.QuizSingle.QuizSingle;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;


import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * QuizCommand is a command that will launch a quiz game. The game is either single-answer or multi-answer based
 * @author Carl Johan Helgstrand
 * @version 2.0
 */
public class QuizCommand extends Command {
    private QuizSQLConnector dbConnection = new QuizSQLConnector();
    private QuizSingle quizS = new QuizSingle();
    private QuizMulti quizM = new QuizMulti();
    private TextChannel channel;
    private EmbedBuilder eb = new EmbedBuilder();
    private EventWaiter waiter;
    private JDA jda;

    public QuizCommand(JDA jda, EventWaiter waiter) {
        this.jda=jda;
        this.waiter=waiter;
    }



    /**
     * Execute method that handles all interaction between user and the game
     * @param event A Discord event such as a message being sent by a user
     */
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        eb.clear();
        eb.setColor(Color.YELLOW);
        channel = event.getChannel();
        quizS.setTextChannel(channel);
        quizS.setDatabaseConnection(dbConnection);
        quizM.setTextChannel(channel);
        quizM.setDatabaseConnection(dbConnection);
        quizM.setEventWaiter(waiter);
        String subCommand = event.getMessage().getContentRaw().substring(6);

        switch(subCommand){
            case "start single":
                if(quizM.isAlive()){
                    eb.setTitle("A session of Multi-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Single-answer Quiz");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
                else {
                    quizS.start(event.getAuthor());
                }
                break;
            case "stop single":
                quizS.stop(event.getAuthor());
                break;
            case "skip":
                if(quizS.isAlive()) {
                    quizS.skip(event.getAuthor());
                }
                else if(quizM.isAlive()) {
                    quizM.skip(event.getAuthor());
                }
                break;
            case "start multi":
                if(quizS.isAlive()){
                    eb.setTitle("A session of Single-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Multi-answer Quiz");
                    event.getChannel().sendMessage(eb.build()).queue();
                    break;
                }
                else {
                    quizM.start(event.getAuthor());
                    break;
                }
            case "stop multi":
                quizM.stop(event.getAuthor());
                break;
            case "points":
                int points = getPoints(event.getAuthor());
                if(points == -1) {
                    eb.setTitle(event.getAuthor().getAsTag() + " has " + points + " points!");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
                else {
                    eb.setTitle(event.getAuthor().getAsTag() + " has " + points + " points!");
                    event.getChannel().sendMessage(eb.build()).queue();
                    break;
                }
            case "highscore":
                AbstractMap.SimpleEntry<String,Integer> bestScore = dbConnection.getHighestScore();
                if (bestScore.getKey().equalsIgnoreCase("-1")) {
                    eb.setTitle("No score has yet been entered in the database!");
                }
                else {
                    User user = jda.getUserById(bestScore.getKey());
                    eb.setAuthor(user.getAsTag());
                    eb.setThumbnail(user.getAvatarUrl());
                    eb.setDescription("**has the highest score with " + bestScore.getValue() + " points!**");
                }
                event.getChannel().sendMessage(eb.build()).queue();
        }

    }

    /**
     * Special method that will track user activity while a quiz game is running to gather answers
     * @param event A Discord event such as a message being sent by a user
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().equals(channel) && !event.getAuthor().isBot()) {
            Message message = event.getMessage();
            User user = event.getAuthor();
            if(quizS != null){
                if(quizS.isAlive()) {
                    quizS.checkAnswer(user, message);
                }
            }
        }
    }

    /**
     * Retrieves the user's current points
     * @param user A Discord user
     * @return Returns the number of points
     */
    private int getPoints(User user){
        return dbConnection.getPoints(user.getId());
    }

    /**
     * A special event that will get called when a user types the quiz command without parameter. It
     * will show the user how to use the module
     * @return The help message made with an EmbedBuilder
     */
    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Quiz Module ❓", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/QuizModule");
        eb.setDescription("A trivia game!");
        eb.addField("quiz start single", "- Starts a single answer game", true);
        eb.addField("quiz stop single", "- Stops a single answer game", true);
        eb.addField("quiz skip", "- Skips a question", false);
        eb.addField("quiz start multi", "- Starts a multi answer game", true);
        eb.addField("quiz stop multi", "- Stops a multi answer game", true);
        eb.addField("quiz points", "- Shows your global points", true);
        eb.addField("quiz highscore", "- shows the highest score", true);
        eb.setFooter("DM Johs#7898 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
