package QuizModule;

import java.util.LinkedList;

public class Quiz extends Thread{
    private LinkedList<Question> questions = new LinkedList<Question>();
    private static String defaulturl = "https://opentdb.com/api.php?amount=10&type=boolean";
    private QuizParser parser;
    private Question currentQuestion = null;
    private QuizCommand command;
    private CheckForAnswers checker;

    public Quiz(QuizCommand command){
        this.command=command;
        parser = new QuizParser(defaulturl);
        checker = new CheckForAnswers();
        fillQuestions();
        start();
    }

    //ToDo
    public Quiz(String category){

    }

    @Override
    public void run(){
        try {
            command.postMessage("Welcome to a new session of Quiz!");
            Thread.sleep(5000);
            command.postMessage("Quiz will begin shortly, prepare yourself.");
            Thread.sleep(10000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Session begins
        while(!Thread.interrupted()) {
            if (currentQuestion != null) {
                try {
                    Thread.sleep(10000);
                    command.postMessage(getQuestion()); //The first question in line is automatically polled from the Question list before its presented
                    checker.start(); //Starting inner thread to check for answers for 30 seconds per question
                    Thread.sleep(30000);
                    checker.interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                command.postMessage("Out of questions! Session will end shortly");
                Thread.interrupted();
                break;
            }
        }
    }


    private void fillQuestions(){
        for(Question q : parser.getQuestions()){
            questions.push(q);
        }

    }

    private void pollQuestion(){
        if(questions.size() != 0) {
            currentQuestion = questions.poll();
        }
    }

    //ToDo
    public Boolean checkAnswer(String answer){
        return false;
    }

    public String getQuestion(){
        pollQuestion();
        if(currentQuestion != null){
            return currentQuestion.getQuestion();
        }
        return null;
    }

    //ToDo
    public String getQuestionType(){
        return currentQuestion.getType().toString();
    }


    //ToDo - Thread to check answers for 30 seconds, for each question
    private class CheckForAnswers extends Thread{

        @Override
        public void run(){
            while(!Thread.interrupted()){
                if(checkAnswer("placeholderAnswer")){
                    command.postMessage("PlaceholderUser" + " answered correctly with: " + "placeholderAnswer");
                    Thread.interrupted();
                }
            }
        }
    }
}
