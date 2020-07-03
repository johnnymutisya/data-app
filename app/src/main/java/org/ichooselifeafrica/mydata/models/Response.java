package org.ichooselifeafrica.mydata.models;

public class Response {
    private  int question_id;
    private int value;

    public Response(int question_id, int value) {
        this.question_id = question_id;
        this.value = value;
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
