package QuizModule.QuizMulti;

import QuizModule.QuizSQLConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONException;

import java.util.LinkedList;

/**
 * QuizMulti is the Quiz Multi Answer game
 * @author Carl Johan Helgstrand
 * @version 2.0
 */
public class QuizMulti implements Runnable {
    private Thread thread;
    private volatile boolean isRunning = false;
    private LinkedList<QuestionMulti> questions = new LinkedList<QuestionMulti>();
    private TextChannel channel;
    private String defaultUrl = "https://opentdb.com/api.php?amount=10&type=multiple"; //default api 10 questions, random categories and multiple choices
    private QuestionMulti currentQuestion = null;
    private User correctUser;
    private Boolean answered = false;
    private Boolean working = true;
    private EmbedBuilder eb = new EmbedBuilder();
    private QuizSQLConnector dbConnection;

    /*
    Constructors
     */
    public QuizMulti(TextChannel channel) {
        this.channel=channel;
        questions = new QuizMultiParser(defaultUrl).getQuestions();
    }

    public QuizMulti() {
        try {
            questions = new QuizMultiParser(defaultUrl).getQuestions();
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
            postMessage("Multi-answer Quiz game was started by " + user.getName());
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
        else if(!isRunning && !working) {
            postMessage("Multi-answer Quiz cannot be started at this time, please try again later");
        }
        else{
            postMessage("A session of Multi-answer Quiz is already running");
        }
    }

    /**
     * Stops the game
     * @param user The user calling the stop method
     */
    public void stop(User user) {
        if(isRunning) {
            postMessage("Multi-answer Quiz game was stopped by " + user.getName());
            isRunning = false;
            thread.interrupt();
        }
        else{
            postMessage("There is no current session to stop");
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
        try{
            thread.sleep(3000);
            while (isRunning) {
                if (!questions.isEmpty()) {
                    postMessage(getQuestion()+"\n"+getAlternatives());
                    limitChat(15);
                    for (int i = 0; i < 300; i++) {
                        thread.sleep(50);
                        if (answered || !isRunning) {
                            break;
                        }
                    }

                    if (!isRunning) {
                        break;
                    }

                    postCorrectAnswer(); //Posts the correct answer to the chat

                } else{
                    postMessage("Out of questions! Session will end shortly"); //Session ending
                    isRunning = false;
                    break; //End of thread
                }
                thread.sleep(5000);
            }
        }
        catch (InterruptedException e) {
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
     * @param author User name in Discord server
     * @param msg Discord user's message
     */
    public void checkAnswer(User author, Message msg) {
        if(currentQuestion!= null) {
            if (msg.getContentRaw().equalsIgnoreCase(currentQuestion.getCorrectAnswer()) && !answered) {
                correctUser = author;
                answered = true;
            }
        }
    }


    /**
     * Returns alternate answers - only one is correct
     * @return String with all alternate answers
     */
    private String getAlternatives() {
        String res = "";
        int counter = 1;
        for(String alternative : currentQuestion.getAlternatives()){
            res += counter+". " + alternative + "\n";
            counter++;
        }
        return res;
    }


    /**
     * Posts the correct answer
     * The post will change depending on if a user answered correctly, or if nobody did
     */
    private void postCorrectAnswer() {
        if(correctUser == null) {
            postMessage("**The correct answer is " + currentQuestion.getCorrectAnswer() + "!**");
        }
        else {
            postMessage("**"+correctUser.getName() + " is correct with the answer " + currentQuestion.getCorrectAnswer() + "!**" +
                    "\n**1 point is awarded!**");
            dbConnection.addToPoints(correctUser.getId(),1);
        }
        resetData();
    }


    /**
     * Posts a message in the channel where the quiz game is running
     * @param message Outgoing message, ex: Posting a question
     */
    private void postMessage(String message) {
        if(channel != null) {
            eb.setTitle(message);
            channel.sendMessage(eb.build()).queue();
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
        correctUser = null;
    }


    /**
     * Limits the chat traffic
     * @param limit seconds when users can only send 1 message
     */
    private void limitChat(int limit) {
        channel.getManager().setSlowmode(limit);
    }

    public void setDatabaseConnection(QuizSQLConnector dbConnection) {
        this.dbConnection=dbConnection;
    }
}
