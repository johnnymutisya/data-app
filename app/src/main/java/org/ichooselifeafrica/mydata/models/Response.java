package org.ichooselifeafrica.mydata.models;

public class Response {
    private  int question_id;
    private int value;
    private String inputVal="";
    private String questionType="";

    public Response(int question_id, int value) {
        this.question_id = question_id;
        this.value = value;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getInputVal() {
        return inputVal;
    }

    public void setInputVal(String inputVal) {
        this.inputVal = inputVal;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
