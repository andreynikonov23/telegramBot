package org.company.utils;

import org.apache.log4j.Logger;
import org.company.service.AbsSectionManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActiveTests {
    private static final Logger logger = Logger.getLogger(ActiveTests.class);
    private static final HashMap<Long, Set<AbsSectionManager>> saveTests = new HashMap<>();
    private static final HashMap<Long, AbsSectionManager> activeTests = new HashMap<>();

    private ActiveTests(){}

    public static void saveTest(Long chatId, AbsSectionManager sectionManager){
        logger.info(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        if (saveTests.containsKey(chatId)){
            saveTests.get(chatId).add(sectionManager);
            logger.info(String.format("saveTests add new key-value %d-%s", chatId, sectionManager));
        } else {
            Set<AbsSectionManager> set = new HashSet<>();
            set.add(sectionManager);
            saveTests.put(chatId, set);
            logger.info(String.format("saveTests key=%s put=%s", chatId, sectionManager));
        }
    }
    public static void addActiveTest(Long chatId, AbsSectionManager sectionManager){
        logger.debug(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        activeTests.put(chatId, sectionManager);
    }
    public static Set<AbsSectionManager> getSectionManagersSet(long chatId){
        return saveTests.get(chatId);
    }
    public static AbsSectionManager getActiveSectionManager(long chatId){
        return activeTests.get(chatId);
    }
    public static AbsSectionManager getIncompleteTest(long chatId, String tag){
        AbsSectionManager incompleteTest = null;
        if (saveTests.containsKey(chatId)){
            for (AbsSectionManager test : saveTests.get(chatId)){
                if (test.getTag().equals(tag)){
                    incompleteTest = test;
                }
            }
        }
        logger.info("getIncompleteTest return " + incompleteTest);
        return incompleteTest;
    }
    public static void clear(Long chatId, String tag){
        saveTests.get(chatId).removeIf(section -> section.getTag().equals(tag));
        activeTests.remove(chatId);
        logger.info(String.format("ChatId=%d test %s hashMaps clear", chatId, tag));
    }
}
