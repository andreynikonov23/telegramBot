package org.company.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Question {
    private int id;
    private String question;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String rightAnswer;
    private List<String> mediaFiles;

    @Override
    public String toString(){
        return String.format("[%d, %s, %s, %s, %s, %s, %s]", id, question, answerA, answerB, answerC, answerD, rightAnswer);
    }
}