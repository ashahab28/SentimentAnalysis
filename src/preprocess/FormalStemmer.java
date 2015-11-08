/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import IndonesianNLP.IndonesianSentenceFormalization;
import IndonesianNLP.IndonesianStemmer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static testStopWord.StopWords.getUnimportantWords;

/**
 *
 * @author kanya
 */
public class FormalStemmer {
    
    private ArrayList<String[]> csvList = new ArrayList();
    
    public ArrayList<String[]> getCSVList() {
        return csvList;
    }
    
    public void readCSV(String csvIn) {

        BufferedReader br = null;
        String line = "";
        String splitBy = ",";
        
        try {
            
            br = new BufferedReader(new FileReader(csvIn));
            int i = 0;
            
            while ((line = br.readLine()) != null) {
                
                String[] tweetInstance = line.split(splitBy);
                csvList.add(tweetInstance);
                
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FormalStemmer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FormalStemmer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public String removeSpecialCharacter(String input){
        // Remove link Remove RT Remove @
        if(input.contains("RT")){
            input = input.replace("RT", " ");
        } 
        input = input.replaceAll("http.*\\s?", " ");
        input = input.replaceAll("@.*?\\s", " ");
        return input;
    }
    
    public void process() throws FileNotFoundException {
        
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        IndonesianStemmer stemmer = new IndonesianStemmer();
        
        for (int i = 0; i < csvList.size(); i++) {
            
            // Debug
            System.out.println(csvList.get(i)[0]);
            
            String rawContent = csvList.get(i)[0];
            rawContent = removeSpecialCharacter(rawContent);
            String formalizedContent = formalizer.formalizeSentence(rawContent);
            String stemmedContent = stemmer.stemSentence(formalizedContent);
            
            csvList.get(i)[0] = stemmedContent;
            
            // Debug
            System.out.println(csvList.get(i)[0]);
            System.out.println();
            System.out.println();
        }
    }
    
    public void writeCSV(String csvOut) throws IOException {
        
        FileWriter fw = new FileWriter(csvOut);
        PrintWriter pw = new PrintWriter(fw);
        
        String content = "";
        
        //System.out.println(csvList.size());
        for (int i = 0; i < csvList.size(); i++) {
            for (int j = 0; j < csvList.get(i).length; j++) {
                //System.out.println(csvList.get(i)[j]);
                content += csvList.get(i)[j] + ",";
            }
            content += "\n";
        }
        
        pw.print(content);
        
        pw.flush();
        pw.close();
        fw.close();
    }
    
}
