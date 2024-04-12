package org.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Question {
    private int id;
    private String questionTxt;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private String rightAnswer;
    private List<File> mediaFiles;
    private AnswerType type;

    @Override
    public String toString(){
        return String.format("[%d, %s, %s, %s, %s, %s, %s, %s]", id, questionTxt, answerA, answerB, answerC, answerD, answerE, rightAnswer);
    }
}
