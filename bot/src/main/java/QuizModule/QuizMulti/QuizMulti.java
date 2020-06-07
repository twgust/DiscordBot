package QuizModule.QuizMulti;

import QuizModule.QuizSQLConnector;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.json.JSONException;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * QuizMulti is the Quiz Multi Answer game
 * @author Carl Johan Helgstrand
 * @version 2.0
 */
public class QuizMulti implements Runnable {
    private final String one = "1️⃣";
    private final String two = "2️⃣";
    private final String three = "3️⃣";
    private final String four = "4️⃣";
    private Thread thread;
    private volatile boolean isRunning = false;
    private LinkedList<QuestionMulti> questions = new LinkedList<QuestionMulti>();
    private TextChannel channel;
    private String defaultUrl = "https://opentdb.com/api.php?amount=10&type=multiple"; //default api 10 questions, random categories and multiple choices
    private QuestionMulti currentQuestion = null;
    private Boolean answered = false;
    private Boolean working = true;
    private EmbedBuilder eb = new EmbedBuilder();
    private QuizSQLConnector dbConnection;
    private EventWaiter waiter;
    private ArrayList<String> answers;

    /*
    Constructors
     */
    public QuizMulti(TextChannel channel) {
        this.channel=channel;

    }

    public QuizMulti() {
        try {
        }
        catch(JSONException e ) {
            System.out.println("API-call error");
            working = false;
        }
    }

    public void setTextChannel(TextChannel channel) {
        this.channel=channel;
    }

    /**
     * Starts the quiz game
     * @param user The user calling the start method
     */
    public void start(User user) {
        if(!isRunning && working) {
            postMessage("Multi-answer Quiz game was started by " + user.getName(), false);
            questions = new QuizMultiParser(defaultUrl).getQuestions();
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
        else if(!isRunning && !working) {
            postMessage("Multi-answer Quiz cannot be started at this time, please try again later", false);
        }
        else{
            postMessage("A session of Multi-answer Quiz is already running", false);
        }
    }

    /**
     * Stops the game
     * @param user The user calling the stop method
     */
    public void stop(User user) {
        if(isRunning) {
            postMessage("Multi-answer Quiz game was stopped by " + user.getName(), false);
            isRunning = false;
            thread.interrupt();
        }
        else{
            postMessage("There is no current session to stop", false);
        }
    }

    /**
     * Checks if the quiz thread is running
     * @return True or false
     */
    public boolean isAlive() {
        return isRunning;
    }



    @Override
    public void run() {
        try {
            thread.sleep(3000);
        }catch (InterruptedException e) {
        }
        while(isRunning){
            if (!questions.isEmpty()) {
                try {
                    resetData();
                    postMessage(getQuestion() + "\n" + getAlternatives(), true);
                    for (int i = 0; i < 300; i++) {
                        thread.sleep(50);
                    }
                    if(!answered){
                        postMessage("Nobody answered correctly! \nThe correct answer is " + currentQuestion.getCorrectAnswer(), false);
                    }
                }
            catch(NullPointerException e){
                    isRunning = false;
                    break;
                }
            catch(InterruptedException e){
                    if (answered && isRunning) {
                        try {
                            thread.sleep(2000);
                        } catch (InterruptedException iE) {
                        }
                    } else if (!answered && isRunning) {
                        try {
                            thread.sleep(2000);
                        } catch (InterruptedException iE) {
                        }
                        postMessage("Nobody answered correctly! \nThe correct answer is " + currentQuestion.getCorrectAnswer(), false);
                    }
                }
            }
            else {
                    postMessage("Out of questions! Session will end shortly", false); //Session ending
                    isRunning = false;
                    break; //End of thread
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

    /**
     * Returns a question
     * @return The actual question - from QuestionMulti
     */
    public String getQuestion() {
        pollQuestion();
        if(currentQuestion != null){
            return currentQuestion.getQuestion();
        }
        return null;
    }


    /**
     * Gets a question from a LinkedList
     */
    private void pollQuestion() {
        if(questions.size() != 0) {
            currentQuestion = questions.poll();
        }
    }


    /**
     * Check if a user's given answer is correct
     * @param user User name in Discord server
     * @param answer  The answer given
     */
    public synchronized void checkAnswer(User user, String answer) {
        if(currentQuestion!= null && answer.equals(currentQuestion.getCorrectAnswer())) {
                answered = true;
                postMessage("**" + user.getName() + " answered " + currentQuestion.getCorrectAnswer() + " which is correct!**" +
                        "\n**" + user.getName() + " is awarded 1 point!**", false);
                dbConnection.addToPoints(user.getId(), 1);
                thread.interrupt();
        }
    }


    /**
     * Returns alternate answers - only one is correct
     * @return String with all alternate answers
     */
    private String getAlternatives() {
        answers = new ArrayList<String>();
        String res = "";
        int counter = 1;
        System.out.println();
        System.out.println("Correct answer:" + currentQuestion.getCorrectAnswer());
        for(String alternative : currentQuestion.getAlternatives()){
            answers.add(alternative);
            System.out.println("Alternative: " + alternative);
            res += counter + "." + "  " + alternative + "\n";
            counter++;
        }
        return res;
    }


    /**
     * Posts a message in the channel where the quiz game is running
     * @param post Outgoing message, ex: Posting a question
     */
    private void postMessage(String post, boolean reactions) {
        if(channel != null) {
            eb.clear();
            eb.setTitle(post);
            eb.setColor(Color.YELLOW);
            if(reactions) {
                channel.sendMessage(eb.build()).queue(message -> {
                    message.addReaction(one).queue();
                    message.addReaction(two).queue();
                    message.addReaction(three).queue();
                    message.addReaction(four).queue();
                    initWaiter(message.getIdLong(), message.getChannel(), answers);
                });
            }
            else {
                channel.sendMessage(eb.build()).queue();
            }
        }
        else{
            System.out.println("Channel missing");
        }
    }

    /**
     * Resets game data between answers
     */
    private void resetData() {
        answered = false;
    }

    public void setDatabaseConnection(QuizSQLConnector dbConnection) {
        this.dbConnection=dbConnection;
    }

    public void skip(User user) {
        if(isRunning) {
            postMessage("The question was skipped by " + user.getName(), false);
            thread.interrupt();
        }
        else{
            postMessage("There is no current session running in which a question can be skipped", false);
        }
    }


    /**
     *
     * @param messageId Unique ID for the message.
     * @param channel The channel in which the message is sent
     * @param answers The answers to choose from
     */
    private void initWaiter(long messageId, MessageChannel channel, ArrayList<String> answers) {
            waiter.waitForEvent(MessageReactionAddEvent.class, e -> {
                User user = e.getUser();
                return checkEmote(e.getReactionEmote().getName()) && !user.isBot() && e.getMessageIdLong() == messageId;
            }, (e) -> {
                handleReaction(answers, e.getReactionEmote().getName(), channel, e.getUser());
                if(!answered) {
                    initWaiter(messageId, channel, answers);
                }
            }, 15, TimeUnit.SECONDS, () -> {
                Thread.interrupted();
            });
    }

    /**
     *
     * @param emote The clicked reaction emote
     * @return
     */
    private boolean checkEmote(String emote) {
        switch (emote) {
            case one:
            case two:
            case three:
            case four:
                return true;
            default:
                System.out.println(false);
                return false;
        }
    }

    /**
     * Handles the response when a user clicks on an emote to answer a question
     * @param answers The listed answers
     * @param emote The clicked emote
     * @param channel The channel in which the emote was clicked
     * @param user The user who clicked the emote
     */
    private void handleReaction(ArrayList<String> answers, String emote, MessageChannel channel, User user) {
        if(currentQuestion != null && !answered) {
            if (emote.equalsIgnoreCase("1️⃣")) {
                System.out.println("user clicked alt 1");
                checkAnswer(user, answers.get(0));
            } else if (emote.equalsIgnoreCase("2️⃣")) {
                System.out.println("user clicked alt 2");
                checkAnswer(user, answers.get(1));
            } else if (emote.equalsIgnoreCase("3️⃣")) {
                System.out.println("user clicked alt 3");
                checkAnswer(user, answers.get(2));
            } else if (emote.equalsIgnoreCase("4️⃣")) {
                System.out.println("user clicked alt 4");
                checkAnswer(user, answers.get(3));
            }
        }
    }


    public void setEventWaiter(EventWaiter waiter) {
        this.waiter=waiter;
    }
}
