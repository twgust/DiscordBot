package QuizModule.QuizMulti.enums;

public enum QuizDifficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private String level;

    private QuizDifficulty(String level){
        this.level=level;
    }
}
