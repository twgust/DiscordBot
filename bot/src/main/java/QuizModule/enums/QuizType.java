package QuizModule.enums;

public enum QuizType {
    BOOLEAN("boolean"),
    MULTIPLE("multiple");

    private String type;

    private QuizType(String type){
        this.type=type;
    }
}
