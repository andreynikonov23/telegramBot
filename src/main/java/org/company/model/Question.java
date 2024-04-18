package org.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.SerializationUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Question implements Externalizable {
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeObject(questionTxt);
        out.writeObject(answerA);
        out.writeObject(answerB);
        out.writeObject(answerC);
        out.writeObject(answerD);
        out.writeObject(answerE);
        out.writeObject(rightAnswer);
        ArrayList<String> filePaths = new ArrayList<>();
        for (File file : mediaFiles){
            filePaths.add(file.getAbsolutePath());
        }
        out.writeObject(filePaths);
        out.writeObject(type);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        questionTxt = (String) in.readObject();
        answerA = (String) in.readObject();
        answerB = (String) in.readObject();
        answerC = (String) in.readObject();
        answerD = (String) in.readObject();
        answerE = (String) in.readObject();
        rightAnswer = (String) in.readObject();
        mediaFiles = new ArrayList<>();
        ArrayList<String> filePaths = (ArrayList<String>) in.readObject();
        for (String path : filePaths){
            mediaFiles.add(new File(path));
        }
        type = (AnswerType) in.readObject();
    }
}
