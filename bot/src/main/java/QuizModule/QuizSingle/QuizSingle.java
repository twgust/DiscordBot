package QuizModule.QuizSingle;

import QuizModule.QuizSQLConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Random;

/**
 * QuizSingle is the Quiz Single Answer game
 * @author Carl Johan Helgstrand
 * @version 1.0
 */
public class QuizSingle implements Runnable{
    private volatile boolean isRunning = false;
    private TextChannel channel;
    private QuizSingleParser parser = new QuizSingleParser("http://jservice.io/api/random");
    private EmbedBuilder eb = new EmbedBuilder();
    private QuestionSingle question;
    private String[] hints;
    private Thread thread;
    private int hintCounter;
    private QuizSQLConnector dbConnection;

    public QuizSingle(TextChannel channel){
        this.channel=channel;
    }

    public QuizSingle(){

    }

    public void setTextChannel(TextChannel channel){
        this.channel=channel;
    }

    /**
     * Starts the quiz game
     * @param user The user calling the start method
     */
    public void start(User user){
        if(!isRunning) {
            postMessage("Single-answer Quiz game was started by " + user.getName());
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
        else{
            postMessage("A session of Single-answer Quiz is already running");
        }
    }

    /**
     * Stops the game
     * @param user The user calling the stop method
     */
    public void stop(User user){
        if(isRunning) {
            postMessage("Single-answer Quiz game was stopped by " + user.getName());
            isRunning = false;
            thread.interrupt();
        }
        else{
            postMessage("There is no current session to stop");
        }
    }

    /**
     * Skips a given question
     * @param user The user calling the skip method
     */
    public void skip(User user) {
        if(isRunning) {
            postMessage("The question was skipped by " + user.getName());
            thread.interrupt();
        }
        else{
            postMessage("There is no current session running in which a question can be skipped");
        }
    }

    /**
     * Checks if the quiz thread is running
     * @return True or false
     */
    public boolean isAlive(){
        return isRunning;
    }

    @Override
    public void run() {
        try {
            thread.sleep(3000);
        }catch (InterruptedException e) {
        }
        while(isRunning){
            try {
                question = parser.getQuestion();
                hints = null;
                generateHints();
                postMessage(question.getQuestion() + "\nHint:  " + getHint());
                thread.sleep(10000);
                for(int i= 0; i<hintCounter-1;i++) {
                    generateHints();
                    postMessage("Hint:  " + getHint());
                    thread.sleep(5000);
                }
                if(!question.getAnswered()){
                    postMessage("Nobody answered correctly! \nThe correct answer is " + question.getAnswer());
                }
            }
            catch(NullPointerException e){
                isRunning = false;
                break;
            }
            catch(InterruptedException e){
                if(!question.getAnswered() && isRunning){
                    try {
                        thread.sleep(2000);
                    } catch (InterruptedException iE) {
                    }
                    postMessage("Nobody answered correctly! \nThe correct answer is " + question.getAnswer());
                }
            }
            catch(IllegalArgumentException e){
            }
            try {
                thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates hints for the users
     */
    private void generateHints(){
        if(hints == null){
            hintCounter = 0;
            hints = new String[question.getAnswer().length()];
            for(int i = 0; i<hints.length; i++){
                if(question.getAnswer().charAt(i) == '&' || question.getAnswer().charAt(i) == ' ' ||
                        question.getAnswer().charAt(i) == '-' || question.getAnswer().charAt(i) == '\'' ||
                        question.getAnswer().charAt(i) == '.' || question.getAnswer().charAt(i) == ','){
                    hints[i] = String.valueOf(question.getAnswer().charAt(i));
                }
                else {
                    hints[i] = "_";
                    hintCounter++;
                }
            }
        }
        else {
            Random rand = new Random();
            int pos = rand.nextInt(question.getAnswer().length());
            while(hints[pos] != "_"){
                pos = rand.nextInt(question.getAnswer().length());
            }
            hints[pos] = String.valueOf(question.getAnswer().charAt(pos));
        }
    }

    /**
     * Returns a hint
     * @return A hint, ex:B A _ _ U answer would be BASTU
     */
    private String getHint(){
        String res = "";
        for(String s : hints){
            if(s.equals("_")){
                res += "\\" + s + " ";
            }
            else {
                res += s + " ";
            }
        }
        return res.toUpperCase();
    }

    /**
     * Check if a user's given answer is correct
     * @param user Discord user Id
     * @param message Discord User's message
     */
    public void checkAnswer(User user, Message message){
        if(question != null) {
            if (message.getContentRaw().equalsIgnoreCase(question.getAnswer())) {
                postMessage("**" + user.getName() + " is correct with the answer " + question.getAnswer() + "!**" +
                 "\n**1 point is awarded!**");
                question.setAnswered(true);
                dbConnection.addToPoints(user.getId(), 1);
                thread.interrupt();
            }
        }
    }

    /**
     * Posts a message in the channel where the quiz game is running
     * @param message Outgoing message, ex: Posting a question
     */
    private void postMessage(String message){
        if(channel != null) {
            eb.clear();
            eb.setTitle(message);
            eb.setColor(Color.YELLOW);
            channel.sendMessage(eb.build()).queue();
        }
        else{
            System.out.println("Channel missing");
        }
    }

    public void setDatabaseConnection(QuizSQLConnector dbConnection) {
        this.dbConnection=dbConnection;
    }
}
