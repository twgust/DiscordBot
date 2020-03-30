package QuizModule;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.Random;

//The Quiz game
public class Quiz extends Thread{
    private LinkedList<Question> questions = new LinkedList<Question>();
    private static String defaulturl = "https://opentdb.com/api.php?amount=10&type=multiple"; //default api 10 questions, random categories and multiple choices
    private QuizParser parser;
    private Question currentQuestion = null;
    private QuizCommand command;
    private User lastCorrectUser = null;
    private Boolean answered = false;
    private String[] hints;
    private int lastRandCharPos;

    /*
    Constructors
     */
    public Quiz(QuizCommand command){
        this.command=command;
        parser = new QuizParser(defaulturl);
        fillQuestions();
    }

    //ToDo
    public Quiz(String category){

    }

    //ToDo
    public Quiz(String category, int nbr){

    }

    //ToDo
    public Quiz(int nbr){

    }

    //Thread
    @Override
    public void run(){
        deleteMessages();
        lockQuizChannel();
        try {
            postMessage("**Welcome to a new session of Quiz!**");
            Thread.sleep(2000);
            postMessage("**The quiz will begin shortly, prepare yourself.**");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Session begins
        while(!Thread.interrupted()) {
            if (currentQuestion != null) {
                try {
                    Thread.sleep(5000);
                    postEmptyLine();
                    postLine();
                    postMessage("> "+getQuestion()); //The first question in line is automatically polled from the Question list before its presented
                    postEmptyLine();
                    postMessage("```"+getAlternatives()+"```");

                    unlockQuizChannel(); //Unlocks Quiz-chat
                    limitChat(15); //Slows down the Quiz-chat to 1 message per user, per 15 seconds.

                    for(int i = 0; i<30; i++){ //For loop to check for correct answers. Loops for 15 seconds or until correct answer is posted by a user
                        Thread.sleep(500);
                        if(answered){
                            break;
                        }
                    }
                    lockQuizChannel(); //Locks Quiz-chat
                    postCorrectAnswer(); //Posts the correct answer to the chat
                    resetData(); //Resets references used in the thread to null
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    postMessage("**Out of questions! Session will end shortly**"); //Session ending
                    Thread.sleep(3000);
                    deleteMessages();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break; //End of thread
            }
        }
    }

    /*
    Methods used by the thread
     */

    //Fills up the LinkedList with questions from Open Trivia DB
    private void fillQuestions(){
        for(Question q : parser.getQuestions()){
            questions.push(q);
        }
        pollQuestion();
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
            lastCorrectUser = author;
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
        if(lastCorrectUser == null){
            postMessage("**The correct answer is " + currentQuestion.getCorrectAnswer() + "!**");
        }
        else {
            postMessage("**"+lastCorrectUser.getName() + " is correct with the answer " + currentQuestion.getCorrectAnswer() + "!**");
        }
    }

    //Posts a message in the chat
    private void postMessage(String msg){
        command.postMessage(msg);
    }

    // _ _ creates a blank space in discord
    private void postEmptyLine(){
        command.postMessage("_ _");
    }

    //Creates a line that stretches the length of a code block
    private void postLine(){
        command.postMessage("**----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------**");
    }

    //Future use
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

    //Resets some data used by the thread
    private void resetData(){
        answered = false;
        lastCorrectUser = null;
        hints = null;
    }

    //ToDo
    private void lockQuizChannel(){

    }

    //ToDo
    private void unlockQuizChannel(){

    }

    //Limits the user activity in the chat to 1 msg per user per 15 seconds
    private void limitChat(int index){
        command.limitChat(index);
    }

    //Deletes all messages in the chat
    private void deleteMessages(){
        command.deleteMessages();
    }

}
