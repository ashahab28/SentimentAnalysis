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
        String csv_in = "/Users/kanya/Dropbox/KANJEISTA/ITB/PERKULIAHAN/IF Semester 7/IF4072 NLP/Text/Tugas 3 Sentiment Analysis/Dataset & Referensi/jokowi_sort_uniq.csv";
        String csv_out = "/Users/kanya/Dropbox/KANJEISTA/ITB/PERKULIAHAN/IF Semester 7/IF4072 NLP/Text/Tugas 3 Sentiment Analysis/SentimentAnalysis/dataset.csv";
        
        FormalStemmer fst = new FormalStemmer();
        fst.readCSV(csv_in);
        fst.process();
        fst.writeCSV(csv_out);
    }
    
}
