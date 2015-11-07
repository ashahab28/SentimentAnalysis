/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testStopWord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Windy Amelia
 */
public class StopWords {
    //menghasilkan list kata-kata yang tidak penting yang akan dihapus
    public static ArrayList<String> getStopWordFromFile (String fileName) throws FileNotFoundException {
        ArrayList<String> stopwords;
        try (Scanner file = new Scanner (new File(fileName))) {
            stopwords = new ArrayList<>();
            while (file.hasNext()) {
                stopwords.add(file.next());
            }
        }
        return stopwords;
    }
    
    //menghasilkan list tanda baca yang akan dihapus
    public static ArrayList<String> getPunctuation (String fileName) throws FileNotFoundException {
        ArrayList<String> punctuation;
        try (Scanner file = new Scanner (new File(fileName))) {
            punctuation = new ArrayList<>();
            while (file.hasNext()) {
                punctuation.add(file.next());
            }
        }
        return punctuation;
    }
    
    //menghasilkan kata-kata seperti http, @
    public static ArrayList<String> getUnimportantWords (String fileName) throws FileNotFoundException {
        ArrayList<String> unimportantWords;
        try (Scanner file = new Scanner (new File(fileName))) {
            unimportantWords = new ArrayList<>();
            while (file.hasNext()) {
                unimportantWords.add(file.next());
            }
        }
        return unimportantWords;
    }
    
    //mengecek sebuah kata apakah stopword atau bukan
    public static Boolean isStopWord (String word, ArrayList<String> stopWords) {
        boolean found = false;
        for (String stopWord : stopWords) {
            if (word.equals(stopWord)) {
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
        for (String unimportantWord : unimportantWords) {
            if (word.contains(unimportantWord)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    //menghasilkan kata-kata dari sebuah kalimat
    public static ArrayList<String> splitedWords (String sentence) {
        ArrayList<String> wordList = new ArrayList<>();
        String[] splited = sentence.split(" ");
        wordList.addAll(Arrays.asList(splited));
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
            for (String punctuation1 : punctuation) {
                if (wordList.get(i).contains(punctuation1)) {
                    String tempWord = wordList.get(i).replace(punctuation1, "");
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
        for (String wordList1 : wordList) {
            newSentence = newSentence.concat(wordList1 + " ");
        }
        return newSentence;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String input = "!! RT @Philoartspace: @pradewitch: Gak ada yang bandingin bagaimana Prabowo dan Jokowi memperlakukan pekerjanya?";
        System.out.println(input);
        // Classic Stopwords
        ArrayList<String> stopWords = getStopWordFromFile("src/testStopWord/stopwords_id.txt");
        ArrayList<String> punctuation = getPunctuation("src/testStopWord/punctuation.txt");
        ArrayList<String> unimportantWords = getUnimportantWords("src/testStopWord/unimportantWords.txt");
        System.out.println(removeStopWords(input, stopWords, punctuation, unimportantWords));
        
        // Create a hashmap to save word occurence
        Map<String, Integer> wordCounter = new HashMap<>();
        
        String csvFile = "Original Data/jokowi_sort_uniq.csv";
	BufferedReader br = null;
	String line;
	String cvsSplitBy = ",";
        String fileContent = "";
	try {
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
		        // use comma as separator
			String[] contents = line.split(cvsSplitBy);
			String content = contents[0];
                        content = removeStopWords(content, stopWords, punctuation, unimportantWords);
                        //System.out.println(content);
                        // Nanti diconcat sesuai dengan konteks dari textnya
                        for(String word: content.split(" ")){
                            if(wordCounter.containsKey(word)){
                                Integer val = wordCounter.get(word) + 1;
                                wordCounter.put(word, val);
                            } else{
                                wordCounter.put(word, 1);
                            }
                        }
                        fileContent += content + "\n";
		}
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}

        // Write to file with first type
        File file = new File("Filtered Data/Stopwords/classic_jokowi_filtered.txt");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(fileContent);
        }
        
        // Remove singleton var
          ArrayList<String> singleTonWords = new ArrayList<>();
//        for(Map.Entry<String, Integer> entry : wordCounter.entrySet()){
//            System.out.printf("Key : %s and Value: %d %n", entry.getKey(), entry.getValue()); 
//        }

        Iterator it = wordCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Integer)pair.getValue() == 1){
                System.out.println((String)pair.getKey());
                singleTonWords.add((String)pair.getKey());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        fileContent = "";
	try {
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
		        // use comma as separator
			String[] contents = line.split(cvsSplitBy);
			String content = contents[0];
                        content = removeStopWords(content, singleTonWords, punctuation, unimportantWords);
                        //System.out.println(content);
                        // Nanti diconcat sesuai dengan konteks dari textnya
                        for(String word: content.split("")){
                            if(wordCounter.containsKey(word)){
                                Integer val = wordCounter.get(word) + 1;
                                wordCounter.put(word, val);
                            } else{
                                wordCounter.put(word, 0);
                            }
                        }
                        fileContent += content + "\n";
		}
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}
        
        // Write to file with second type
        file = new File("Filtered Data/Stopwords/TF1_jokowi_filtered.txt");
        fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(fileContent);
        }
        
	System.out.println("Done");
    }
}
