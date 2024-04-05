package org.company.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.company.tasks.Question;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskReader {

    //Добавить проверку

    public void getAllObjects(String file){
        List<String[]> strings;
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build()) {
            strings = reader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < strings.size(); i++) {
            System.out.println(Arrays.toString(strings.get(i)));
        }
    }

    private void parseToQuestion(String str, Question question){
        List<String> line = new ArrayList<>(List.of(str.split(",")));
        question.setQuestion(line.get(0));
        question.setAnswerA(line.get(1));
        question.setAnswerB(line.get(2));
        question.setAnswerC(line.get(3));
        question.setAnswerD(line.get(4));
        question.setRightAnswer(line.get(5).toCharArray()[0]);
        List<String> mediaFiles = new ArrayList<>(List.of(line.get(6), line.get(7), line.get(8)));
        question.setMediaFiles(mediaFiles);
    }
}
