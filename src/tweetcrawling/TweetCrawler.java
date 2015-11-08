/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tweetcrawling;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import twitter4j.JSONObject;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author kanya
 */
public class TweetCrawler {
    
    private String OACK = "r3If9nkNITRy9G2SBQpBbPXzL";
    private String OACS = "H9mmPgvM9g94MggFbnWhSCP7PYz49AMsQZLFk5xmDENTRafu9d";
    private String OAAT = "61975586-HLeJ8FLjfizofjfl1Bq3swbohMgShawxO1HTNY7t9";
    private String OAATS = "Si6bA4bnbEnR2sQkRyHhf2SAFclBmQ1dChahprjslDVUz";
    private final static String SOURCE = "Twitter";
    //private ResponseList<User> Queries; // List of users that wanted to be crawled
    private List<Status> Statuses; // List of status of a user
    private String[] Queries;
    private String OutputFile;
    
    public TweetCrawler() {
        Statuses = new ArrayList<Status>();
    }
    
    public TweetCrawler(String[] query, String outputfile) {
        Queries = query;
        OutputFile = outputfile;
        Statuses = new ArrayList<Status>();
    }
    
//    public ResponseList<User> getQueries() {
//        return Queries;
//    }
    
    /**
    * @return Statuses attributes of this object
    * Getter of attribute Statuses
    */
    public List<Status> getStatuses() {
        return Statuses;
    }

    /**
     * @param statuses is a list of status object
     * Setter for attribute Statuses
     */
    public void setStatuses(List<Status> statuses) {
        Statuses = statuses;
    }

    /**
     * @param statuses is a list of status that want to be added
     * Adding list of status in statuses to current attribute Statuses
     */
    public void addStatuses(List<Status> statuses){
        Statuses.addAll(statuses);
    }

    /**
     * Emptying out attribute Statuses
     */
    public void emptyStatuses() {
        List<Status> emptyStat = new ArrayList<Status>();
        setStatuses(emptyStat);
    }
    
    public void rateLimitHandler(TwitterConfiguration tc_, String endpoint) throws TwitterException, InterruptedException {
        Map<String, RateLimitStatus> rateLimitStatus = tc_.getTwitter().getRateLimitStatus();
        RateLimitStatus appRateLimit = rateLimitStatus.get(endpoint);

        System.out.printf(endpoint + ": You have %d calls remaining out of %d, Limit resets in %d seconds\n",
                                                appRateLimit.getRemaining(),
                                                appRateLimit.getLimit(),
                                                appRateLimit.getSecondsUntilReset()); // For debug purposes

        if (appRateLimit.getRemaining() < 10) {
                System.out.println("Sleeping for " + appRateLimit.getSecondsUntilReset() + " seconds due to " + endpoint + " rate limit."); // For debug purposes
                Thread.sleep((appRateLimit.getSecondsUntilReset() + 2) * 1001);
        }
    }
    
    private String getTweetUrl(String username, String id){
        return "http://twitter.com/" + username + "/status/" + id;
    }
    
    public String getData(String url){
        BufferedReader in = null;
        StringBuffer response = null;
        String USER_AGENT = "My Twitter App v1.0.23";
        String bearerToken = null;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Authorization", bearerToken);

            in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
        } catch (Exception e) {
            
        } finally{
            try {
                if(in != null)
                    in.close();
            } catch (Exception e) {
            
            }
        }

        if(response != null){
            return response.toString();
        }
        else{
            return null;
        }
    }
    
    private String getUserGender(String user) {
        String gender = "unknown";
        String apiUrl = "http://api.namsor.com/onomastics/api/json/gendre/";
        String namesPath = "";

        if(user.contains(" ")) {
            String[] firstTwoParts = user.split(" ");

            if(firstTwoParts.length == 1){
                String name = firstTwoParts[0].replaceAll("[^A-Za-z0-9]", "");

                if(!name.isEmpty()){
                        namesPath = name + "/" + name;
                }
            }else{
                String name1 = firstTwoParts[0].replaceAll("[^A-Za-z0-9]", "");
                String name2 = firstTwoParts[1].replaceAll("[^A-Za-z0-9]", "");

                if(name1 != null && name2 != null && !name1.isEmpty() && !name2.isEmpty()){
                        namesPath = name1 + "/" + name2;
                }else if(name1 != null && !name1.isEmpty() && (name2 == null || name2.isEmpty())){
                        namesPath = name1 + "/" + name1;
                }else if((name1 == null || name1.isEmpty()) && !name2.isEmpty() && name2 != null){
                        namesPath = name2 + "/" + name2;
                }
            }
        } else {
            String name = user.replaceAll("[^A-Za-z0-9]", "");
            namesPath = name + "/" + name;
        }

        namesPath = namesPath.trim();

        if(!namesPath.isEmpty() && namesPath != "/") {
                try{
                    String data = getData(apiUrl + namesPath);
                    JSONObject obj = new JSONObject(data);
                    gender = obj.getString("gender");
                } catch(Exception e) {
                    return "unknown";
                }
        }

        return gender;
    }
    
    private String getDateCrawler(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public void getTweets(TwitterConfiguration tc_) throws IOException, InterruptedException {
        
        try {

            for (String query_ : Queries) {
                
                // Ngambil tweet dari tiap page lalu disimpan di Statuses
                int maxTweetCrawled = 3240; // This is the number of the latest tweets that we can crawl, specified by Twitter
                
                    Query query = new Query(query_);
                    query.setLang("id");
                    QueryResult result;
                    do {
                        rateLimitHandler(tc_, "/search/tweets"); // Check rate limit first
                        //System.out.println("kanya sini");
                        result = tc_.getTwitter().search(query);
                        List<Status> tweets = result.getTweets();
                        for (Status tweet : tweets) {
                            ArrayList<String> ValToWrite = getValueToWrite(tweet);
                            writeValue(ValToWrite, OutputFile);
                            System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText().replace("\n", " "));
                        }
                        addStatuses(tweets);
                        
                    } while ((query = result.nextQuery()) != null);
                    
                //printTweets(OutputFile); // Printing out crawling result per page of this keywords
                //emptyStatuses(); // Empty out the current attribute Statuses so that it can be used for other keywords    
                    
                    
                
            }

        } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                if (te.exceededRateLimitation()) {
                        System.out.println("Rate limit status: " + te.getRateLimitStatus());
                }
                System.exit(-1);
        }
	 
    }
    
    public ArrayList<String> getValueToWrite(Status status) {
        
        // Getting the value to be written
            
        Long tid = status.getId();
        String tweetid = tid.toString();

        ArrayList<String> valueToWrite = new ArrayList<String>();

        User user = status.getUser();
        String screenname = user.getScreenName();
        String name = user.getName();
        String url= getTweetUrl(screenname, tweetid);
        
        String body = status.getText().replace("\n", " ");

        valueToWrite.add(body.replace(",", " ")); // element: body
        valueToWrite.add(url); // element: id
        valueToWrite.add(screenname); // element: userid
        valueToWrite.add(name); // element: user

        // element: gender
        if(name != null && !name.isEmpty()) {
            valueToWrite.add(getUserGender(name));
        } else if(screenname != null && !screenname.isEmpty()) {
            valueToWrite.add(getUserGender(screenname));
        } else {
            valueToWrite.add("");
        }

        valueToWrite.add(user.getLocation()); // element: location
        valueToWrite.add("" + user.getFollowersCount()); // element: followercount
        valueToWrite.add("" + user.getFriendsCount()); // element: friendscount
        valueToWrite.add("" + user.getStatusesCount()); // element: statuscount

        try {
            List<String> coor = new ArrayList<String>();
            String latitude = "" + status.getGeoLocation().getLatitude();
            String longitude = "" + status.getGeoLocation().getLongitude();
            coor.add(latitude);
            coor.add(longitude);

            if(coor != null && !coor.isEmpty() && coor.size() > 0){
                    valueToWrite.add(coor.get(0)); // element: latitude
                    valueToWrite.add(coor.get(1)); // element: longitude
                    valueToWrite.add(coor.get(0) + "," + coor.get(1));
            }else{
                    valueToWrite.add(null);
                    valueToWrite.add(null);
                    valueToWrite.add(null);
            }
        } catch(Exception e) {
                valueToWrite.add(null);
                valueToWrite.add(null);
                valueToWrite.add(null);
        }

        try{

            String geoname = status.getPlace().getName();
            String country = status.getPlace().getCountry();

            if(geoname != null){
                    valueToWrite.add(geoname);
            }else{
                    valueToWrite.add(null);
            }

            if(country != null){
                    valueToWrite.add(country);
            }else{
                    valueToWrite.add(null);
            }

        } catch(Exception e) {
                valueToWrite.add(null);
                valueToWrite.add(null);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = status.getCreatedAt();
        valueToWrite.add(dateFormat.format(date)); // element: date
        valueToWrite.add(getDateCrawler()); // element: datecrawler
        valueToWrite.add(status.getInReplyToScreenName()); // element: replyto

        String tooltip = "";
        try {

            String geoname = status.getPlace().getName();
            if(geoname != null){
                    tooltip = "user = " + name + ", geoname = " + geoname;
            }

        } catch(Exception e) {
                tooltip = "";			
        }
        valueToWrite.add(tooltip.trim()); // element: tooltip
        valueToWrite.add(SOURCE); // element: source
        
        return valueToWrite;
    }
    
    public void writeValue(ArrayList<String> valueToWrite, String csvOut) throws IOException, TwitterException {
        // Write valueToWrite to csv external file
            
            FileWriter fw = new FileWriter(csvOut, true);
            PrintWriter pw = new PrintWriter(fw);
            
            String content = "'";
            
            
            for (int i = 0; i < valueToWrite.size()-1; i++) {
                //content += valueToWrite.get(i).replace(",", " ") + "','";
                content += valueToWrite.get(i) + "','";
            }
            
            content += valueToWrite.get(valueToWrite.size() - 1) + "'";
            
            pw.println(content);
            
            pw.flush();
            pw.close();
            fw.close();
    }
    
    public void printTweets(String csvOut) throws IOException, TwitterException {
    
        
        for (Status status : getStatuses()) {
            
            // Getting the value to be written
            
            Long tid = status.getId();
            String tweetid = tid.toString();

            ArrayList<String> valueToWrite = new ArrayList<String>();

            User user = status.getUser();
            String screenname = user.getScreenName();
            String name = user.getName();
            String url= getTweetUrl(screenname, tweetid);

            valueToWrite.add(url); // element: id
            valueToWrite.add(screenname); // element: userid
            valueToWrite.add(name); // element: user

            // element: gender
            if(name != null && !name.isEmpty()) {
                valueToWrite.add(getUserGender(name));
            } else if(screenname != null && !screenname.isEmpty()) {
                valueToWrite.add(getUserGender(screenname));
            } else {
                valueToWrite.add("");
            }

            valueToWrite.add(user.getLocation()); // element: location
            valueToWrite.add("" + user.getFollowersCount()); // element: followercount
            valueToWrite.add("" + user.getFriendsCount()); // element: friendscount
            valueToWrite.add("" + user.getStatusesCount()); // element: statuscount

            try {
                List<String> coor = new ArrayList<String>();
                String latitude = "" + status.getGeoLocation().getLatitude();
                String longitude = "" + status.getGeoLocation().getLongitude();
                coor.add(latitude);
                coor.add(longitude);

                if(coor != null && !coor.isEmpty() && coor.size() > 0){
                        valueToWrite.add(coor.get(0)); // element: latitude
                        valueToWrite.add(coor.get(1)); // element: longitude
                        valueToWrite.add(coor.get(0) + "," + coor.get(1));
                }else{
                        valueToWrite.add(null);
                        valueToWrite.add(null);
                        valueToWrite.add(null);
                }
            } catch(Exception e) {
                    valueToWrite.add(null);
                    valueToWrite.add(null);
                    valueToWrite.add(null);
            }

            try{
                
                String geoname = status.getPlace().getName();
                String country = status.getPlace().getCountry();

                if(geoname != null){
                        valueToWrite.add(geoname);
                }else{
                        valueToWrite.add(null);
                }

                if(country != null){
                        valueToWrite.add(country);
                }else{
                        valueToWrite.add(null);
                }
                
            } catch(Exception e) {
                    valueToWrite.add(null);
                    valueToWrite.add(null);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = status.getCreatedAt();
            valueToWrite.add(dateFormat.format(date)); // element: date
            valueToWrite.add(getDateCrawler()); // element: datecrawler
            valueToWrite.add(status.getText()); // element: body
            valueToWrite.add(status.getInReplyToScreenName()); // element: replyto

            String tooltip = "";
            try {
                
                String geoname = status.getPlace().getName();
                if(geoname != null){
                        tooltip = "user = " + name + ", geoname = " + geoname;
                }

            } catch(Exception e) {
                    tooltip = "";			
            }
            valueToWrite.add(tooltip.trim()); // element: tooltip
            valueToWrite.add(SOURCE); // element: source

            // Write valueToWrite to csv external file
            
            FileWriter fw = new FileWriter(csvOut, true);
            PrintWriter pw = new PrintWriter(fw);
            
            String content = "'";
            
            for (int i = 0; i < valueToWrite.size()-1; i++) {
                content += valueToWrite.get(i) + "','";
            }
            
            content += valueToWrite.get(valueToWrite.size()) + "'";
            
            pw.print(content);
            
            pw.flush();
            pw.close();
            fw.close();
            
        }
    }

}
