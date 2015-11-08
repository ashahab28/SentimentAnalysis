/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tweetcrawling;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author kanya
 */
public class Main {
    
    public static void getRateLimitStatuses(TwitterConfiguration tc_) {
        try {
//          Twitter twitter = new TwitterFactory().getInstance();
            Map<String ,RateLimitStatus> rateLimitStatus = tc_.getTwitter().getRateLimitStatus();
            for (String endpoint : rateLimitStatus.keySet()) {
                RateLimitStatus status = rateLimitStatus.get(endpoint);
                System.out.println("Endpoint: " + endpoint);
                System.out.println(" Limit: " + status.getLimit());
                System.out.println(" Remaining: " + status.getRemaining());
                System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
                System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
            }
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get rate limit status: " + te.getMessage());
            System.exit(-1);
        }
    }
    
    public static void main(String args[]) throws IOException, InterruptedException {
        String csv_out = "/Users/kanya/Dropbox/KANJEISTA/ITB/PERKULIAHAN/IF Semester 7/IF4072 NLP/Text/Tugas 3 Sentiment Analysis/SentimentAnalysis/tweettest.csv";
        String[] q = {"first media","fastnet","indihome","biznet","speedy", "innovate", "mnc play", "telkomsel flash", "bolt 4g", "indosatm2", "paketri", "xl axiata"};
        //"first media","fastnet","indihome","biznet","speedy", "innovate", "mnc play", "telkomsel flash", "bolt 4g", "indosatm2", "paketri", "xl axiata"
        
        String OACK = "r3If9nkNITRy9G2SBQpBbPXzL";
        String OACS = "H9mmPgvM9g94MggFbnWhSCP7PYz49AMsQZLFk5xmDENTRafu9d";
        String OAAT = "61975586-HLeJ8FLjfizofjfl1Bq3swbohMgShawxO1HTNY7t9";
        String OAATS = "Si6bA4bnbEnR2sQkRyHhf2SAFclBmQ1dChahprjslDVUz";

        TwitterConfiguration tc = new TwitterConfiguration(OACK, OACS, OAAT, OAATS);
        tc.configureTwitter();
        
        //getRateLimitStatuses(tc);
        
        TweetCrawler tcrawl = new TweetCrawler(q, csv_out);
        tcrawl.getTweets(tc);
    }
    
}
