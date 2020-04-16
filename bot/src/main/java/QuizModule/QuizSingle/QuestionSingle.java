package QuizModule.QuizSingle;

public class QuestionSingle {
    private long id;
    private String answer;
    private String question;
    private Boolean answered = false;

    public QuestionSingle(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getAnswered(){
        return answered;
    }

    public void setAnswered(Boolean answered){
        this.answered=answered;
    }
}
