package com.skapps.knowMore;

/**
 * A representation of an rss item from the list.
 * 
 * @author Veaceslav Grec
 * 
 */
public class RssItem {

	private final String title;
	private final String link;
    private final boolean isHeading;

	public RssItem(String title, String link) {
		this.title = title;
		this.link = link;
        this.isHeading = ((link == null ) && (title != null) && (!"".equals(title) ));
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

    public boolean isHeading() {
        return isHeading;
    }
}
