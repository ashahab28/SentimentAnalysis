package tweetcrawling;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author kanya
 *
 */
public class TwitterConfiguration {
	
    private String OACK;
    private String OACS;
    private String OAAT;
    private String OAATS;
    private Twitter Twitter;

    public TwitterConfiguration() {

    }

    public TwitterConfiguration(String OACK_, String OACS_, String OAAT_, String OAATS_) {
        OACK = OACK_;
        OACS = OACS_;
        OAAT = OAAT_;
        OAATS = OAATS_;
    }

    public void configureTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(OACK)
          .setOAuthConsumerSecret(OACS)
          .setOAuthAccessToken(OAAT)
          .setOAuthAccessTokenSecret(OAATS);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter = tf.getInstance();
    }

    public Twitter getTwitter() {
            return Twitter;
    }
	
}
