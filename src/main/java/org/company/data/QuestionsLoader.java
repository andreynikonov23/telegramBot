package org.company.data;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.company.model.TaskTags;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public class QuestionsLoader {
    private static final Logger logger = Logger.getLogger(QuestionsLoader.class);


    public List<Question> getQuestionList(String tag) {
        String questionDir = "/media/" + tag + "/";
        logger.debug(questionDir + "tasks.csv reading");
        List<Question> questions = new ArrayList<>();

        List<String[]> strings;
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(questionDir + "tasks.csv")))).withSkipLines(1).build()) {
            strings = reader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            parseToQuestionList(strings, questions, tag);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        logger.debug("question for " + tag + " have been loaded");
        return questions;
    }

    public List<Question> getRandomQuestionList() {
        String[] tags = new String[5];

        int i = 0;
        for (String tag : TaskTags.getAllTags()) {
            if (tag.equals("unit")) {
                continue;
            }
            tags[i] = tag;
            if (i == 4) {
                break;
            } else {
                i++;
            }
        }

        List<Question> randomQuestionsList = new ArrayList<>();
        for (int j = 0; j < tags.length; j++) {
            List<Question> allQuestionsInTask = getQuestionList(tags[j]);
            List<Integer> nonRepeatingIndexes = new ArrayList<>();
            for (int k = 0; k < 3; k++) {
                int randomIndex = (int) (Math.random() * (allQuestionsInTask.size()));
                if (nonRepeatingIndexes.contains(randomIndex)){
                    k--;
                } else {
                    nonRepeatingIndexes.add(randomIndex);
                    randomQuestionsList.add(allQuestionsInTask.get(randomIndex));
                }
            }
        }
        return randomQuestionsList;
    }

    private void parseToQuestionList(List<String[]> strings, List<Question> questions, String tag) throws IOException {
        for (String[] arr : strings) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new String(arr[i].getBytes(), StandardCharsets.UTF_8);
            }
            Question question = new Question();
            question.setTag(tag);
            question.setQuestionTxt(arr[0]);
            question.setAnswerA(arr[1]);
            question.setAnswerB(arr[2]);
            question.setAnswerC(arr[3]);
            question.setAnswerD(arr[4]);
            question.setAnswerE(arr[5]);
            question.setRightAnswer(arr[6]);

            List<String> mediaFiles = new ArrayList<>();
            if (!(arr[7].equals(""))) {
                mediaFiles.add(arr[7]);
            }
            if (!(arr[8].equals(""))) {
                mediaFiles.add(arr[8]);
            }
            if (!(arr[9].equals(""))) {
                mediaFiles.add(arr[9]);
            }
            question.setMediaFiles(mediaFiles);

            if (arr[10].equals("choice")) {
                question.setType(AnswerType.CHOICE);
            } else if (arr[10].equals("input")) {
                question.setType(AnswerType.INPUT);
            }

            questions.add(question);
        }
    }
}
