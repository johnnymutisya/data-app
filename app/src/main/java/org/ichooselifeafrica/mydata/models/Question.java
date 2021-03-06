package org.ichooselifeafrica.mydata.models;

public class Question {
    private int id;
    private String title;
    private  int answers=2;
    public boolean firstAnswerChecked = false;
    public boolean secondAnswerChecked = false;
    public boolean thirdAnswerChecked = false;
    public String inputValueAns="";

    public Question(int id, String title, int answers) {
        this.id = id;
        this.title = title;
        this.answers = answers;
    }

    public String getInputValueAns() {
        return inputValueAns;
    }

    public void setInputValueAns(String inputValueAns) {
        this.inputValueAns = inputValueAns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }
}
