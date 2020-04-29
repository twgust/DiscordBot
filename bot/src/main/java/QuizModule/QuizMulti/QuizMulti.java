package QuizModule.QuizMulti;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.LinkedList;

//The Quiz game
public class QuizMulti implements Runnable{
    private Thread thread;
    private volatile boolean isRunning = false;
    private LinkedList<QuestionMulti> questions = new LinkedList<QuestionMulti>();
    private TextChannel channel;
    private String defaultUrl = "https://opentdb.com/api.php?amount=10&type=multiple"; //default api 10 questions, random categories and multiple choices
    private QuestionMulti currentQuestion = null;
    private User correctUser;
    private Boolean answered = false;
    private EmbedBuilder eb = new EmbedBuilder();

    /*
    Constructors
     */
    public QuizMulti(TextChannel channel){
        this.channel=channel;
        questions = new QuizMultiParser(defaultUrl).getQuestions();
    }

    public QuizMulti(){
        questions = new QuizMultiParser(defaultUrl).getQuestions();
    }

    public void setTextChannel(TextChannel channel){
        this.channel=channel;
    }


    public void start(User user){
        if(!isRunning) {
            postMessage("Multi-answer Quiz game was started by " + user.getName());
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
        else{
            postMessage("A session of Multi-answer Quiz is already running");
        }
    }

    public void stop(User user){
        if(isRunning) {
            postMessage("Multi-answer Quiz game was stopped by " + user.getName());
            isRunning = false;
            thread.interrupt();
        }
        else{
            postMessage("There is no current session to stop");
        }
    }

    public boolean isAlive(){
        return isRunning;
    }


    //Thread
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

    /*
    Methods used by the thread
     */

    //Retrieves the current question if there is one
    public String getQuestion(){
        pollQuestion();
        if(currentQuestion != null){
            return currentQuestion.getQuestion();
        }
        return null;
    }

    //Retrieves the current questions type
    public String getQuestionType(){
        if(currentQuestion != null) {
            return currentQuestion.getType().toString();
        }
        else{
            return null;
        }
    }

    //Polls the current first question in the list
    private void pollQuestion(){
        if(questions.size() != 0) {
            currentQuestion = questions.poll();
        }
    }


    //Checks if a user entered a correct answer
    public void checkAnswer(User author, Message msg){
        if(currentQuestion!= null) {
            if (msg.getContentRaw().equalsIgnoreCase(currentQuestion.getCorrectAnswer()) && !answered) {
                correctUser = author;
                answered = true;
            }
        }
    }


    //Retrieves all the alternative answers. Only one is correct
    private String getAlternatives(){
        String res = "";
        int counter = 1;
        for(String alternative : currentQuestion.getAlternatives()){
            res += counter+". " + alternative + "\n";
            counter++;
        }
        return res;
    }


    //Posts the correct answer
    private void postCorrectAnswer(){
        if(correctUser == null){
            postMessage("**The correct answer is " + currentQuestion.getCorrectAnswer() + "!**");
        }
        else {
            postMessage("**"+correctUser.getName() + " is correct with the answer " + currentQuestion.getCorrectAnswer() + "!**");
        }
        resetData(); //Resets references used in the thread to null
    }


    //Posts a message in the chat
    private void postMessage(String message){
        if(channel != null) {
            eb.setTitle(message);
            channel.sendMessage(eb.build()).queue();
        }
        else{
            System.out.println("Channel missing");
        }
    }

    //Resets some data used by the thread
    private void resetData(){
        answered = false;
        correctUser = null;
    }


    //Limits the user activity in the chat to 1 msg per user per 15 seconds
    private void limitChat(int limit){
        channel.getManager().setSlowmode(limit);
    }

}
