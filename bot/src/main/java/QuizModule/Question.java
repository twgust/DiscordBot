package QuizModule;

import QuizModule.enums.QuizDifficulty;
import QuizModule.enums.QuizType;

import java.util.ArrayList;
import java.util.Collections;

public class Question {
    private QuizDifficulty difficulty;
    private String question;
    private ArrayList<String> alternatives;
    private String correctAnswer;
    private String category;
    private QuizType type;


    public Question(){

    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuizDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QuizType getType() {
        return type;
    }

    public void setType(QuizType type) {
        this.type = type;
    }

    public void setAlternatives(ArrayList<String> alternatives){
        this.alternatives = alternatives;
        Collections.shuffle(this.alternatives); //Shuffle the order of the alternatives, otherwise the correct answer will always be placed first
    }

    public ArrayList<String> getAlternatives(){
        return alternatives;
    }

}
