import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class CombinationChecker {
    private static java.util.Map<Integer, Integer> getFreq(int[] dice) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int d : dice) {
            freq.put(d, freq.getOrDefault(d, 0) + 1);
        }
        return freq;
    }

    public static boolean checkCombo(String comboName, int[] dice) {
        int[] sorted = dice.clone();
        Arrays.sort(sorted);
        switch(comboName) {
            case "Two Pairs":                return isTwoPairs(sorted);
            case "Three of a Kind":          return isThreeOfAKind(sorted);
            case "Little Straight":          return isLittleStraight(sorted);
            case "Full House":               return isFullHouse(sorted);
            case "Four of a Kind":           return isFourOfAKind(sorted);
            case "Large Straight":           return isLargeStraight(sorted);
            case "All Even":                 return isAllEven(sorted);
            case "All Odd":                  return isAllOdd(sorted);
            case "12 or Fewer":              return isSumLE(sorted, 12);
            case "30 or More":               return isSumGE(sorted, 30);
            case "Three Pairs":              return isThreePairs(sorted);
            case "Two Times Three of a Kind":return isTwoTimesThreeOfAKind(sorted);
            case "Four of a Kind and a Pair":return isFourAndPair(sorted);
            default: return false;
        }
    }

    // (The rest of the combo checks remain unchanged, no highlight logic)
    private static boolean isTwoPairs(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        int pairs = 0;
        for (int count : freq.values()) {
            if (count >= 2) pairs++;
        }
        return pairs >= 2;
    }

    private static boolean isThreeOfAKind(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        for (int count : freq.values()) {
            if (count >= 3) return true;
        }
        return false;
    }

    private static boolean isLittleStraight(int[] sorted) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int d : sorted) set.add(d);
        for (int i = 1; i <= 3; i++){
            if (set.contains(i) && set.contains(i+1) && set.contains(i+2) && set.contains(i+3))
                return true;
        }
        return false;
    }

    private static boolean isFullHouse(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        boolean three = false, two = false;
        for (int count : freq.values()){
            if(count == 3) three = true;
            if(count == 2) two = true;
        }
        return three && two;
    }

    private static boolean isFourOfAKind(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        for (int count : freq.values()){
            if (count >= 4) return true;
        }
        return false;
    }

    private static boolean isLargeStraight(int[] sorted) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int d : sorted) set.add(d);
        for (int start = 1; start <= 2; start++){
            boolean ok = true;
            for (int x = start; x < start+5; x++){
                if(!set.contains(x)){
                    ok = false;
                    break;
                }
            }
            if(ok) return true;
        }
        return false;
    }

    private static boolean isAllEven(int[] sorted) {
        for (int d : sorted) {
            if (d % 2 != 0) return false;
        }
        return true;
    }

    private static boolean isAllOdd(int[] sorted) {
        for (int d : sorted) {
            if (d % 2 == 0) return false;
        }
        return true;
    }

    private static boolean isSumLE(int[] sorted, int limit) {
        int sum = 0;
        for (int d : sorted) sum += d;
        return sum <= limit;
    }

    private static boolean isSumGE(int[] sorted, int limit) {
        int sum = 0;
        for (int d : sorted) sum += d;
        return sum >= limit;
    }

    private static boolean isThreePairs(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        if(freq.size() != 3) return false;
        for (int count : freq.values()){
            if(count != 2) return false;
        }
        return true;
    }

    private static boolean isTwoTimesThreeOfAKind(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        if(freq.size() != 2) return false;
        for (int count : freq.values()){
            if (count != 3) return false;
        }
        return true;
    }

    private static boolean isFourAndPair(int[] sorted) {
        java.util.Map<Integer,Integer> freq = getFreq(sorted);
        if (freq.size() != 2) return false;
        boolean foundFour = false, foundTwo = false;
        for (int count : freq.values()){
            if(count == 4) foundFour = true;
            if(count == 2) foundTwo = true;
        }
        return foundFour && foundTwo;
    }

    public static boolean isFiveOfAKind(int[] dice) {
        java.util.Map<Integer,Integer> freq = getFreq(dice);
        for (int count : freq.values()){
            if(count == 5) return true;
        }
        return false;
    }

    public static boolean isSixOfAKind(int[] dice) {
        java.util.Map<Integer,Integer> freq = getFreq(dice);
        for (int count : freq.values()){
            if(count == 6) return true;
        }
        return false;
    }

    public static boolean isStraightOfSix(int[] dice) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int d : dice) set.add(d);
        for (int val = 1; val <= 6; val++){
            if(!set.contains(val)) return false;
        }
        return true;
    }
}