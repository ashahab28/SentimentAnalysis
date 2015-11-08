/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import java.io.IOException;

/**
 *
 * @author kanya
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        String csv_in = "Original Data/jokowi_sort_uniq.csv";
        String csv_out = "Filtered Data/Formalized&Stemmed/jokowi_sort_uniq.csv";
        
        FormalStemmer fst = new FormalStemmer();
        fst.readCSV(csv_in);
        fst.process();
        fst.writeCSV(csv_out);  
        
        System.out.println("Done");
    }
    
}
