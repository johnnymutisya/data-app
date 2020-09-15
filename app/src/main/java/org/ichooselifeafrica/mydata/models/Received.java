package org.ichooselifeafrica.mydata.models;

public class Received {
    private  String question, yes, no, undecided;
    private String answers="2";

    public Received(String question, String yes, String no, String undecided) {
        this.question = question;
        this.yes = yes;
        this.no = no;
        this.undecided = undecided;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getUndecided() {
        return undecided;
    }

    public void setUndecided(String undecided) {
        this.undecided = undecided;
    }
}
