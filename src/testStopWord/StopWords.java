/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testStopWord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Windy Amelia
 */
public class StopWords {
    //menghasilkan list kata-kata yang tidak penting yang akan dihapus
    public static ArrayList<String> getStopWordFromFile (String fileName) throws FileNotFoundException {
        Scanner file = new Scanner (new File(fileName));
        ArrayList<String> stopwords = new ArrayList<String>();
        while (file.hasNext()) {
            stopwords.add(file.next());
        }
        file.close();
        return stopwords;
    }
    
    //menghasilkan list tanda baca yang akan dihapus
    public static ArrayList<String> getPunctuation (String fileName) throws FileNotFoundException {
        Scanner file = new Scanner (new File(fileName));
        ArrayList<String> punctuation = new ArrayList<String>();
        while (file.hasNext()) {
            punctuation.add(file.next());
        }
        file.close();
        return punctuation;
    }
    
    //menghasilkan kata-kata seperti http, @
    public static ArrayList<String> getUnimportantWords (String fileName) throws FileNotFoundException {
        Scanner file = new Scanner (new File(fileName));
        ArrayList<String> unimportantWords = new ArrayList<String>();
        while (file.hasNext()) {
            unimportantWords.add(file.next());
        }
        file.close();
        return unimportantWords;
    }
    
    //mengecek sebuah kata apakah stopword atau bukan
    public static Boolean isStopWord (String word, ArrayList<String> stopWords) {
        boolean found = false;
        for (int i=0; i<stopWords.size(); i++) {
            if (word.equals(stopWords.get(i))) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    //mengecek sebuah kata termasuk unimportantword atau bukan
    //sebuah kata dikatakan unimportant word jika terkandung unimportant word dalam kata tersebut, misalkan http, @
    public static Boolean isUnimportantWord (String word, ArrayList<String> unimportantWords) {
        boolean found = false;
        for (int i=0; i<unimportantWords.size(); i++) {
            if (word.contains(unimportantWords.get(i))) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    //menghasilkan kata-kata dari sebuah kalimat
    public static ArrayList<String> splitedWords (String sentence) {
        ArrayList<String> wordList = new ArrayList<String>();
        String[] splited = sentence.split(" ");
        for (String word : splited) {
            wordList.add(word);
        }
        return wordList;
    }
    
    // menghapus kata-kata yang merupakan unimportant words
    public static void removeUnimportantWords(ArrayList<String> wordList, ArrayList<String> unimportantWords) {
        int i = 0;
        while (i < wordList.size()) {
            //System.out.println(wordList);
            if (isUnimportantWord(wordList.get(i), unimportantWords)) {
                wordList.remove(i);
            } else {
                i++;
            }
        }
        //return wordList;
    }
    
    // menghapus tanda baca pada list kata
    public static void removePunctuation(ArrayList<String> wordList, ArrayList<String> punctuation) {
        for (int i=0; i<wordList.size(); i++) {
            for (int idxPunc=0; idxPunc<punctuation.size(); idxPunc++) {
                if (wordList.get(i).contains(punctuation.get(idxPunc))) {
                    String tempWord = wordList.get(i).replace(punctuation.get(idxPunc), "");
                    wordList.set(i, tempWord);
                }
            }
            //System.out.println("kata: " + word);
            if (wordList.get(i).trim().length() == 0) {
                wordList.add(wordList.remove(i));
            }
        }
    }
    
    //menghasilkan kalimat baru yang mana kalimat tersebut sudah dihapus stop word, punctuation, dan unimportant words
    public static String removeStopWords (String sentence, ArrayList<String> stopWords, ArrayList<String> punctuation, ArrayList<String> unimportantWords) throws FileNotFoundException {
        ArrayList<String> wordList = splitedWords(sentence);
        
        removeUnimportantWords(wordList, unimportantWords);
        //System.out.println(wordList);
        removePunctuation(wordList, punctuation);
        int idxWordList = 0;
        while (idxWordList < wordList.size()) {
            if (isStopWord(wordList.get(idxWordList).toLowerCase(), stopWords)) {
                wordList.remove(idxWordList);
            } else {
                idxWordList++;
            }
        }
        
        String newSentence = "";
        for(int i=0; i<wordList.size(); i++) {
            newSentence = newSentence.concat(wordList.get(i) + " ");
        }
        return newSentence;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        String input = "!! RT @Philoartspace: @pradewitch: Gak ada yang bandingin bagaimana Prabowo dan Jokowi memperlakukan pekerjanya?";
        System.out.println(input);
        ArrayList<String> stopWords = getStopWordFromFile("C:/Users/Windy Amelia/Documents/NetBeansProjects/tesInaNLP/src/testStopWord/stopwords_id.txt");
        ArrayList<String> punctuation = getPunctuation("C:/Users/Windy Amelia/Documents/NetBeansProjects/tesInaNLP/src/testStopWord/punctuation.txt");
        ArrayList<String> unimportantWords = getUnimportantWords("C:/Users/Windy Amelia/Documents/NetBeansProjects/tesInaNLP/src/testStopWord/unimportantWords.txt");
        System.out.println(removeStopWords(input, stopWords, punctuation, unimportantWords));
    }
}
