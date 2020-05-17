package QuizModule;

import Commands.Command;
import Main.EventListener;
import QuizModule.QuizMulti.QuizMulti;
import QuizModule.QuizSingle.QuizSingle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.AbstractMap;

public class QuizCommand extends Command {
    private QuizSQLConnector dbConnection = new QuizSQLConnector();
    private QuizSingle quizS = new QuizSingle();
    private QuizMulti quizM = new QuizMulti();
    private TextChannel channel;
    private EmbedBuilder eb = new EmbedBuilder();


    @Override
    public void execute(GuildMessageReceivedEvent event) {
        channel = event.getChannel();
        quizS.setTextChannel(channel);
        quizS.setDatabaseConnection(dbConnection);
        quizM.setTextChannel(channel);
        quizM.setDatabaseConnection(dbConnection);
        String subCommand = event.getMessage().getContentRaw().substring(6);

        switch(subCommand){
            case "start single":
                if(quizM.isAlive()){
                    eb.setTitle("A session of Multi-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Single-answer Quiz");
                    eb.setDescription("");
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
                quizS.skip(event.getAuthor());
                break;
            case "start multi":
                if(quizS.isAlive()){
                    eb.setTitle("A session of Single-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Multi-answer Quiz");
                    eb.setDescription("");
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
                    eb.setDescription("");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
                else {
                    eb.setTitle(event.getAuthor().getAsTag() + " has " + points + " points!");
                    eb.setDescription("");
                    event.getChannel().sendMessage(eb.build()).queue();
                    break;
                }
            case "highscore":
                AbstractMap.SimpleEntry<String,Integer> bestScore = dbConnection.getHighestScore();
                if (bestScore.getKey().equalsIgnoreCase("-1")) {
                    eb.setTitle("No score has yet been entered in the database!");
                }
                else {
                    eb.setTitle("<@" + bestScore.getKey() + "> has the highest score with " + bestScore.getValue() + " points!");
                }
                eb.setDescription("");
                event.getChannel().sendMessage(eb.build()).queue();
        }

    }

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
            if(quizM != null){
                if(quizM.isAlive()){
                    quizM.checkAnswer(user, message);
                }
            }
        }
    }

    private int getPoints(User user){
        return dbConnection.getPoints(user.getId());
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("❓ Quiz Module ❓", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/QuizModule");
        eb.setDescription("A trivia game!");
        eb.addField("<%quiz start single >", "- Starts a single answer game", true);
        eb.addField("<%quiz stop single>", "- Stops a single answer game", true);
        eb.addField("<%quiz skip>", "- Skips a question in a single answer game", false);
        eb.addField("<%quiz start multi>", "- Starts a multi answer game", true);
        eb.addField("<%quiz stop multi>", "- Stops a multi answer game", true);
        eb.addField("<%quiz points>", "- Shows your global points", true);
        eb.addField("<%quiz highscore>", "- shows the highest score", true);
        eb.setFooter("DM Johs#7898 if you have suggestions");
        eb.setColor(Color.RED);
        return eb;
    }
}
