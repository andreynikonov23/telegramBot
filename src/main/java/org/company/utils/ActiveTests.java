package org.company.utils;

import org.company.service.AbsSectionManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActiveTests {
    private static final HashMap<Long, Set<AbsSectionManager>> saveTests = new HashMap<>();
    private static final HashMap<Long, AbsSectionManager> activeTests = new HashMap<>();

    private ActiveTests(){}

    public static void saveTest(Long chatId, AbsSectionManager sectionManager){
        System.out.println(saveTests);
        if (saveTests.containsKey(chatId)){
            saveTests.get(chatId).add(sectionManager);
        } else {
            Set<AbsSectionManager> set = new HashSet<>();
            set.add(sectionManager);
            saveTests.put(chatId, set);
        }
    }
    public static void addActiveTest(Long chatId, AbsSectionManager sectionManager){
        System.out.println(activeTests);
        System.out.println(saveTests);
        activeTests.put(chatId, sectionManager);
    }
    public static Set<AbsSectionManager> getSectionManagersSet(long chatId){
        return saveTests.get(chatId);
    }
    public static AbsSectionManager getActiveSectionManager(long chatId){
        return activeTests.get(chatId);
    }
    public static void clear(Long chatId, AbsSectionManager sectionManager){
        saveTests.get(chatId).remove(sectionManager);
        activeTests.remove(chatId);
    }
}
