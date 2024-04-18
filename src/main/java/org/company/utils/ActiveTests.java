package org.company.utils;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.service.AbsSectionManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveTests implements Serializable {
    private static final Logger logger = Logger.getLogger(ActiveTests.class);

    private static final File DATA_FILE = new File("C:\\telegramBotConf\\file.dat");
    private static HashMap<Long, Set<AbsSectionManager>> saveTests;
    private static HashMap<Long, AbsSectionManager> activeTests;

    private ActiveTests(){}

    public static void saveTest(Long chatId, AbsSectionManager sectionManager){
        logger.info(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        if (saveTests.containsKey(chatId)){
            saveTests.get(chatId).add(sectionManager);
            serialize();
            logger.info(String.format("saveTests add new key-value %d-%s", chatId, sectionManager));
        } else {
            Set<AbsSectionManager> set = new HashSet<>();
            set.add(sectionManager);
            saveTests.put(chatId, set);
            serialize();
            logger.info(String.format("saveTests key=%s put=%s", chatId, sectionManager));
        }
    }
    public static void addActiveTest(Long chatId, AbsSectionManager sectionManager){
        logger.debug(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        activeTests.put(chatId, sectionManager);
        serialize();
    }
    public static Set<AbsSectionManager> getSectionManagersSet(long chatId){
        return saveTests.get(chatId);
    }
    public static AbsSectionManager getActiveSectionManager(long chatId){
        return activeTests.get(chatId);
    }
    public static AbsSectionManager getIncompleteTest(long chatId, String tag){
        logger.info(String.format("ChatId=%d getIncompleteTest()", chatId));
        AbsSectionManager incompleteTest = null;
        if (saveTests.containsKey(chatId)){
            for (AbsSectionManager test : saveTests.get(chatId)){
                if (test.getTag().equals(tag)){
                    incompleteTest = test;
                }
            }
        }
        return incompleteTest;
    }
    public static void clear(Long chatId, String tag){
        saveTests.get(chatId).removeIf(section -> section.getTag().equals(tag));
        activeTests.remove(chatId);
        serialize();
        logger.info(String.format("ChatId=%d test %s hashMaps clear", chatId, tag));
    }

    public static void serialize(){
        logger.debug("saving data");
        try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            stream.writeObject(saveTests);
            stream.writeObject(activeTests);
        } catch (FileNotFoundException e) {
            logger.error("saving data error");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("saving data error");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void deserialize(AnnotationConfigApplicationContext context) throws IOException {
        if (!(DATA_FILE.exists())){
            logger.debug("creation file.dat");
            Files.createFile(DATA_FILE.toPath());
            saveTests = new HashMap<>();
            activeTests = new HashMap<>();
        } else if (Files.size(DATA_FILE.toPath()) > 0){
            logger.debug("deserialization data...");
            try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(DATA_FILE))){
                saveTests = (HashMap<Long, Set<AbsSectionManager>>) stream.readObject();
                activeTests = (HashMap<Long, AbsSectionManager>) stream.readObject();
                for (Map.Entry<Long, Set<AbsSectionManager>> entry : saveTests.entrySet()){
                    for (AbsSectionManager manager : entry.getValue()){
                        manager.setBot(context.getBean(TelegramBot.class));
                    }
                }
            } catch (IOException e) {
                logger.error("deserialization data error!");
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                logger.error("deserialization data error!");
                throw new RuntimeException(e);
            }
        } else {
            saveTests = new HashMap<>();
            activeTests = new HashMap<>();
        }
    }
}
