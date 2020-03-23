package QuizModule;

import QuizModule.enums.QuizDifficulty;
import QuizModule.enums.QuizType;

public class Question {
    private QuizDifficulty difficulty;
    private String question;
    private Boolean correctAnswer;
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

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
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
}
