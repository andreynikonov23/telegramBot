package org.company.service;

public interface SectionManager {
    void start();
    void initOrderQuestions();
    void setCallbackAnswer(Integer numberOfQuestion, String answer);
    void setTextAnswer(String text);
    void sendQuestion();
    void result();

}
