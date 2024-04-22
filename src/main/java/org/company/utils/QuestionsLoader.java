package org.company.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.company.model.AnswerType;
import org.company.model.Question;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
public class QuestionsLoader {
    private static final Logger logger = Logger.getLogger(QuestionsLoader.class);
    private Resource media;


    public QuestionsLoader(Resource media) throws IOException {
        this.media = media;
    }

    public Resource getMedia() {
        return media;
    }

    public void setMedia(Resource media) {
        this.media = media;
    }


    //Добавить проверку
    public List<Question> getChiCiQuestionsList() {
        return getQuestionList(AnswerRecognizer.CHI_CI_TAG);
    }
    public List<Question> getAspiratedInitialsQuestionsList(){
        return getQuestionList(AnswerRecognizer.ASPIRATED_INITIALS_TAG);
    }
    public List<Question> getBackLangFinalsQuestionsList(){
        return getQuestionList(AnswerRecognizer.BACK_LANG_FINALS_TAG);
    }
    public List<Question> getEFinalQuestionsList(){
        return getQuestionList(AnswerRecognizer.E_FINAL_TAG);
    }
    public List<Question> getJqxInitialsQuestionsList(){
        return getQuestionList(AnswerRecognizer.JQX_INITIALS_TAG);
    }
    public List<Question> getRInitialsQuestionsList(){
        return getQuestionList(AnswerRecognizer.R_INITIAL_TAG);
    }
    public List<Question> getSpecialFinalQuestionsList(){
        return getQuestionList(AnswerRecognizer.SPECIAL_FINALS_TAG);
    }
    public List<Question> getIanIangQuestionsList(){
        return getQuestionList(AnswerRecognizer.IAN_IANG_TAG);
    }
    public List<Question> getUFinalQuestionsList(){
        return getQuestionList(AnswerRecognizer.U_FINAL_TAG);
    }

    private List<Question> getQuestionList(String tag) {
        String dir = "/media/" + tag + "/";
        logger.debug(dir + "tasks.csv reading");
        List<Question> questions = new ArrayList<>();
        List<String[]> strings;
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(dir + "tasks.csv")))).withSkipLines(1).build()){
            strings = reader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (CsvException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println(strings);
        try {
            parseToQuestionList(strings, questions, dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.debug("question for" + tag + "have been loaded");
        return questions;
    }
    private void parseToQuestionList(List<String[]> strings, List<Question> questions, String dir) throws IOException {
        int id = 1;
        for (String[] arr : strings){
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new String(arr[i].getBytes(), StandardCharsets.UTF_8);
                System.out.println(arr[i]);
            }
            Question question = new Question();
            question.setId(id);
            question.setQuestionTxt(arr[0]);
            question.setAnswerA(arr[1]);
            question.setAnswerB(arr[2]);
            question.setAnswerC(arr[3]);
            question.setAnswerD(arr[4]);
            question.setAnswerE(arr[5]);
            question.setRightAnswer(arr[6]);

            List<String> mediaFiles = new ArrayList<>();
            if (!(arr[7].equals(""))){
                mediaFiles.add(arr[7]);
            }
            if (!(arr[8].equals(""))){
                mediaFiles.add(arr[8]);
            }
            if (!(arr[9].equals(""))){
                mediaFiles.add(arr[9]);
            }
            question.setMediaFiles(mediaFiles);

            if (arr[10].equals("choice")){
                question.setType(AnswerType.CHOICE);
            } else if (arr[10].equals("input")){
                question.setType(AnswerType.INPUT);
            }
            questions.add(question);
            id++;
        }
    }
//    private void loadFiles(String[] arr, Question question, String dir) throws IOException {
//        List<String> mediaFiles = new ArrayList<>();
//        if (arr[7] != null){
//            mediaFiles.add(file);
//        }
//        if (arr[8] != null){
//            File file = new File(media.getFile().getAbsolutePath() + dir + arr[8]);
//            mediaFiles.add(file);
//        }
//        if (arr[9] != null){
//            File file = new File(media.getFile().getAbsolutePath() + dir + arr[9]);
//            mediaFiles.add(file);
//        }
//        question.setMediaFiles(mediaFiles);
//    }

}
