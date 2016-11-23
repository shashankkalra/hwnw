package com.skapps.knowMore;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class RssService extends IntentService {

    private Map<String,String> urlMap = new LinkedHashMap<String,String>();

    public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";

	public RssService() {
		super("RssService");

        urlMap.put("city_news","https://news.google.co.in/news?pz=1&cf=all&ned=in&hl=en&q=<city>+<state>&output=rss");
        urlMap.put("indian_cricket","http://www.espncricinfo.com/rss/content/story/feeds/6.xml");
        urlMap.put("college_news","https://news.google.co.in/news/section?pz=1&cf=all&ned=in&hl=en&q=<college_name>&t=news&output=rss");
        urlMap.put("crop_tips","https://news.google.co.in/news/section?pz=1&cf=all&ned=in&hl=en&q=<crop>+farming+india&output=rss");
        urlMap.put("bollywood_news","https://news.google.co.in/news?pz=1&cf=all&ned=in&hl=en&q=Bollywood&output=rss");
        urlMap.put("health_tips","http://doctor.ndtv.com/rssnews/ndtv/cat/healthtips/rss.html");
        urlMap.put("english_tips","http://englishonlinelearning.in/learn-through-indian-languaes.feed");
        urlMap.put("personal_finance","http://economictimes.indiatimes.com/rssfeeds/837555174.cms");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Timber.d(R.string.app_name + "Service started");
		List<RssItem> rssItems = new ArrayList<RssItem>();
        List<RssItem> tempRssItems = new ArrayList<RssItem>();
			for(String urlKey : urlMap.keySet()) {
                try {
                String newsHeadingAndURL = isURLApplicable(urlKey);
                if(newsHeadingAndURL != null) {
                    if (rssItems.size() != 0)
                        rssItems.add(new RssItem("", null));

                    String[] newsHeadingAndURLArray = newsHeadingAndURL.split(":::");
                    rssItems.add(new RssItem(newsHeadingAndURLArray[0],null));

                    RssParser parser = new RssParser();
                    tempRssItems = parser.parse(getInputStream(newsHeadingAndURLArray[1]));

                    if("city_news".equals(urlKey) || "health_tips".equals(urlKey) || "indian_cricket".equals(urlKey) || "bollywood_news".equals(urlKey) ||
                            "personal_finance".equals(urlKey)){
                        tempRssItems.remove(1);
                        tempRssItems.remove(0);
                    } else if("college_news".equals(urlKey)
                            || "crop_tips".equals(urlKey) || "english_tips".equals(urlKey) || "hometown_city_news".equals(urlKey)) {
                        tempRssItems.remove(0);
                    }
                    rssItems.addAll(tempRssItems);
                }

            } catch (XmlPullParserException e) {
                Timber.w(e.getMessage(), e);
            } catch (IOException e) {
                Timber.w(e.getMessage(), e);
                rssItems.add(new RssItem("Could not connect to internet. Connect to wifi or mobile data and click refresh icon above",null));
            }
            }

		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) rssItems);
		ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
		receiver.send(0, bundle);
	}

    private String isURLApplicable(String urlKey) {

        String headingAndURL = null;
        UserProfileProvider.SharedPreferencesWrapper prefs = UserProfileProvider.getDefaultSharedPreferences(getBaseContext());

        //Map<String, ?> prefMap = prefs.getAll();
        Timber.d("pref map url -- " + prefs.toString());

        Boolean mandatoryInputsNotGiven = prefs.getBoolean("mandatoryInputsNotGiven",true);

        if(mandatoryInputsNotGiven)
            return null;


        String gender = prefs.getString("gender",null);

        String current_state =  prefs.getString("current_state",null);
        String current_city =  prefs.getString("current_city",null);

        Boolean farmerOrNot = prefs.getBoolean("farmer",false);
        String crop = prefs.getString("crop","");

        Boolean studentOrNot = prefs.getBoolean("student",false);
        String degree = prefs.getString("degree","");
        String subject = prefs.getString("subject","");
        String college = prefs.getString("college","");

        Boolean indian_cricket = prefs.getBoolean("indian_cricket",false);
        Boolean bollywood_stars = prefs.getBoolean("bollywood_stars",false);
        Boolean learn_english = prefs.getBoolean("learn_english",false);
        Boolean health = prefs.getBoolean("health",true);
        Boolean finance = prefs.getBoolean("finance",true);

        switch(urlKey){
            case "city_news" : {
                headingAndURL = current_city + " " + current_state + " news";
                String url = urlMap.get(urlKey);
                url = url.replace("<city>",current_city);
                url = url.replace("<state>",current_state);
                url = url.replace(" ", "+");
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }
            case "indian_cricket" : {
                if(!indian_cricket)
                    return null;
                headingAndURL = "Indian Cricket" + " news";
                String url = urlMap.get(urlKey);
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }
            case "college_news" : {
                if(college == null || "".equals(college))
                    return null;
                headingAndURL = "Your college \'" + college + "\' news";
                String url = urlMap.get(urlKey);
                url = url.replace("<college_name>",college);
                url = url.replace(" ", "+");
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }

            case "crop_tips": {
                if(crop == null || "".equals(crop))
                    return null;
                headingAndURL = "Tips for growing \'" + crop;
                String url = urlMap.get(urlKey);
                url = url.replace("<crop>",crop);
                url = url.replace(" ", "+");
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }

            case "bollywood_news": {
                if(!bollywood_stars)
                    return null;
                headingAndURL = "Bollywood News";
                String url = urlMap.get(urlKey);
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }

            case "health_tips": {
                if(!health)
                    return null;
                headingAndURL = "Health Tips";
                String url = urlMap.get(urlKey);
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }
            case "english_tips": {
                if(!learn_english)
                    return null;
                headingAndURL = "Here is English Learning Information";
                String url = urlMap.get(urlKey);
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }
            case "personal_finance": {
                if(!finance)
                    return null;
                headingAndURL = "Financial Tips";
                String url = urlMap.get(urlKey);
                headingAndURL = headingAndURL + ":::" + url;
                break;
            }

        }

        return headingAndURL;
    }

    public InputStream getInputStream(String link) throws IOException {
		try {
			URL url = new URL(link);
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			Timber.w(R.string.app_name + "Exception while retrieving the input stream", e);
            throw e;
		}
	}
}
