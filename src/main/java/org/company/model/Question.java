package org.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Question implements Serializable {
    private String tag;
    private String questionTxt;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private String rightAnswer;
    private List<String> mediaFiles;
    private AnswerType type;

    @Override
    public String toString() {
        return String.format("[%s, %s, %s, %s, %s, %s, %s, %s]", questionTxt, answerA, answerB, answerC, answerD, answerE, rightAnswer, tag);
    }
}
