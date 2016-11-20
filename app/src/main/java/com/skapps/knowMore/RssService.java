package com.skapps.knowMore;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RssService extends IntentService {

    private Map<String,String> urlMap = new LinkedHashMap<String,String>();

    public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";

	public RssService() {
		super("RssService");

        //urlMap.put("city_news","http://www.socialmention.com/search?q=<city>+<state>&t=news&f=rss");
        urlMap.put("city_news","https://news.google.co.in/news?pz=1&cf=all&ned=in&hl=en&q=<city>+<state>&output=rss");
        urlMap.put("indian_cricket","http://www.espncricinfo.com/rss/content/story/feeds/6.xml");
        urlMap.put("college_news","http://www.socialmention.com/search?q=<college_name>&t=news&f=rss");
        urlMap.put("crop_tips","http://api2.socialmention.com/search?q=<crop>+farming+india&t=news&f=rss");
        urlMap.put("bollywood_news","https://news.google.co.in/news?pz=1&cf=all&ned=in&hl=en&q=Bollywood&output=rss");
        urlMap.put("health_tips","http://doctor.ndtv.com/rssnews/ndtv/cat/healthtips/rss.html");
        urlMap.put("english_tips","http://englishonlinelearning.in/learn-through-indian-languaes.feed");
        urlMap.put("personal_finance","http://economictimes.indiatimes.com/rssfeeds/837555174.cms");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("Know More", "Service started");
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
                Log.w(e.getMessage(), e);
            } catch (IOException e) {
                Log.w(e.getMessage(), e);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Map<String, ?> prefMap = prefs.getAll();
        Log.d("pref map url -- " , prefMap.toString());

        Boolean mandatoryInputsNotGiven = (Boolean) prefMap.get("mandatoryInputsNotGiven");

        if(mandatoryInputsNotGiven)
            return null;


        String gender = (String) prefMap.get("gender");

        String current_state =  (String) prefMap.get("current_state");
        String current_city =  (String) prefMap.get("current_city");

        Boolean farmerOrNot = (Boolean) prefMap.get("farmer")== null ? false :(Boolean) prefMap.get("farmer");
        String crop = (String) prefMap.get("crop")== null ? "" : (String) prefMap.get("crop");

        Boolean studentOrNot = (Boolean) prefMap.get("student") == null ? false: (Boolean) prefMap.get("student");
        String degree = (String) prefMap.get("degree")== null ? "" :(String) prefMap.get("degree");
        String subject = (String) prefMap.get("subject")== null ? "" :(String) prefMap.get("subject");
        String college = (String) prefMap.get("college")== null ? "" :(String) prefMap.get("college");

        Boolean indian_cricket = (Boolean) prefMap.get("indian_cricket")== null ? false:(Boolean) prefMap.get("indian_cricket");
        Boolean bollywood_stars = (Boolean) prefMap.get("bollywood_stars")== null ? false:(Boolean) prefMap.get("bollywood_stars");
        Boolean learn_english = (Boolean) prefMap.get("learn_english")== null ? false:(Boolean) prefMap.get("learn_english");
        Boolean health = (Boolean) prefMap.get("health")== null ? true:(Boolean) prefMap.get("health");
        Boolean finance = (Boolean) prefMap.get("finance")== null ? true:(Boolean) prefMap.get("finance");

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
			Log.w("Know More", "Exception while retrieving the input stream", e);
            throw e;
		}
	}
}
