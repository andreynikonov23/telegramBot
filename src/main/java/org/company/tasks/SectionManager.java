package org.company.tasks;

public interface SectionManager {
    void shuffleOrderQuestions();
    void addAnswer(Integer numberOfQuestion, Character answer);
    void sendQuestion();

}
