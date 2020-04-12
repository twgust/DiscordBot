package QuizModule;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

//The Quiz game
public class Quiz implements Runnable{
    private Thread session;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private LinkedList<Question> questions = new LinkedList<Question>();
    private TextChannel channel;
    private static String defaultUrl = "https://opentdb.com/api.php?amount=10&type=multiple"; //default api 10 questions, random categories and multiple choices
    private Question currentQuestion = null;
    private User correctUser;
    private Boolean answered = false;

    /*
    Constructors
     */
    public Quiz(TextChannel channel){
        this.channel=channel;
        questions = new QuizParser(defaultUrl).getQuestions();
    }


    public void start(){
        if(session == null) {
            session = new Thread(this);
            session.start();
            System.out.println("Starting quiz session");
        }
        else{
            System.out.println("Starting quiz session failed");
        }
    }


    public void stop(){
        if(session != null) {
            running.set(false);
            System.out.println("Stopping quiz session");
        }
        else{
            System.out.println("Stopping quiz session failed");
        }
    }

    public boolean isAlive(){
        return running.get();
    }


    //Thread
    @Override
    public void run() {
        try{
            running.set(true);
            deleteMessages(); //Clear chat
            postMessage("**Welcome to a new session of Quiz!**");
            Thread.sleep(2000);
            postMessage("**The quiz will begin shortly, prepare yourself.**");
            Thread.sleep(5000);

            //Session begins
            while (running.get()) {
                if (!questions.isEmpty()) {
                    postMessage(emptyLine());
                    postMessage(line());
                    postMessage("> " + getQuestion()); //The first question in line is automatically polled from the Question list before its presented
                    postMessage(emptyLine());
                    postMessage("```" + getAlternatives() + "```");


                    limitChat(15); //Slows down the Quiz-chat to 1 message per user, per 15 seconds.
                    for (int i = 0; i < 300; i++) { //For loop to check for correct answers. Loops for 15 seconds or until correct answer is posted by a user
                        Thread.sleep(50);
                        if (answered || !running.get()) {
                            break;
                        }
                    }

                    if (!running.get()) {
                        break;
                    }

                    postCorrectAnswer(); //Posts the correct answer to the chat

                } else{
                    postMessage("**Out of questions! Session will end shortly**"); //Session ending
                    Thread.sleep(3000);
                    deleteMessages(); //Clear chat
                    break; //End of thread
                }
                Thread.sleep(5000);
            }
        if (!running.get()) {
            postMessage("**Quz session cancelled!**"); //Session ending
            Thread.sleep(3000);
            deleteMessages(); //Clear chat
        }
        else {
            running.set(false);
        }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
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
        if(msg.getContentRaw().equalsIgnoreCase(currentQuestion.getCorrectAnswer()) && !answered){
            correctUser = author;
            answered = true;
        }
    }


    //Retrieves all the alternative answers. Only one is correct
    private String getAlternatives(){
        String res = "";
        for(String alternative : currentQuestion.getAlternatives()){
            res += " " + alternative + "\n";
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
    private void postMessage(String msg){
        channel.sendMessage(msg).queue();
    }


    // _ _ creates a blank space in discord
    private String emptyLine(){
        return "_ _";
    }


    //Creates a line that stretches the length of a code block
    private String line(){
        return "**-------------------------------------------------------------------------------" +
                "---------------------------------------------------------------------------------------------------------------------**";
    }

    //Future use
    /*
    private void giveHints(){
        if(hints == null && currentQuestion != null){
            hints = new String[currentQuestion.getCorrectAnswer().length()];
            for(int s = 0; s<hints.length; s++){
                hints[s] = "_";
            }
           lastRandCharPos = -1;
        }
        Random rand = new Random();
        int pos;
        while((pos = rand.nextInt(currentQuestion.getCorrectAnswer().length())) == lastRandCharPos){
        }
        lastRandCharPos = pos;
        hints[pos] = String.valueOf(currentQuestion.getCorrectAnswer().charAt(pos));
        String strHint = createHint();

        postMessage("Hints: " + strHint);
    }

    //Future use -bundled with giveHints()
    private String createHint(){
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
    */


    //Resets some data used by the thread
    private void resetData(){
        answered = false;
        correctUser = null;
        // hints = null;
    }


    //Limits the user activity in the chat to 1 msg per user per 15 seconds
    private void limitChat(int limit){
        channel.getManager().setSlowmode(limit);
    }


    //Deletes all messages in the chat
    private void deleteMessages(){
        System.out.println("Delete messages");
        List<Message> messages = channel.getHistory().retrievePast(100).complete();

        if(!messages.isEmpty()){
            if(messages.size() >= 2) {
                channel.deleteMessages(messages).queue();  //Not feasible at the moment, method won't work if messages are older than 2 weeks.
            }
            else if (messages.size() < 2){
                messages.get(0).delete().queue();
            }
            else { //If messages exceed 100, do nothing for now

            }
        }
    }

}
