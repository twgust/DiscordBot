package QuizModule.QuizMulti;

import QuizModule.QuizMulti.enums.QuizDifficulty;
import QuizModule.QuizMulti.enums.QuizType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A question from multi answer game
 * @author Carl Johan Helgstrand
 * @version 1.0
 */
public class QuestionMulti {
    private QuizDifficulty difficulty;
    private String question;
    private ArrayList<String> alternatives;
    private String correctAnswer;
    private String category;
    private QuizType type;


    public QuestionMulti(){

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
