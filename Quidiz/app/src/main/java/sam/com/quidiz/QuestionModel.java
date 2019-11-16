package sam.com.quidiz;

public class QuestionModel {

    private String question, questionA, questionB, questionC, questionD, correctANS;
    private int setNo;

    public QuestionModel(){
        //For Firebase Database
    }

    public QuestionModel(String question, String questionA, String questionB, String questionC, String questionD, String correctANS, int setNo) {
        this.setNo = setNo;
        this.question = question;
        this.questionA = questionA;
        this.questionB = questionB;
        this.questionC = questionC;
        this.questionD = questionD;
        this.correctANS = correctANS;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionA() {
        return questionA;
    }

    public void setQuestionA(String questionA) {
        this.questionA = questionA;
    }

    public String getQuestionB() {
        return questionB;
    }

    public void setQuestionB(String questionB) {
        this.questionB = questionB;
    }

    public String getQuestionC() {
        return questionC;
    }

    public void setQuestionC(String questionC) {
        this.questionC = questionC;
    }

    public String getQuestionD() {
        return questionD;
    }

    public void setQuestionD(String questionD) {
        this.questionD = questionD;
    }

    public String getCorrectANS() {
        return correctANS;
    }

    public void setCorrectANS(String correctANS) {
        this.correctANS = correctANS;
    }

    public int getSetNo() {
        return setNo;
    }

    public void setSetNo(int setNo) {
        this.setNo = setNo;
    }
}
