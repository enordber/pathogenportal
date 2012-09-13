package edu.vt.vbi.ci.pathport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.vt.vbi.ci.util.parse.XMLTreeElement;

public class RSSItem extends PPDataSet implements Comparable {

	/*
	 * Category name constants
	 */
	public static final String NEWS_ANNOUNCEMENT = "News and Announcements";
	public static final String PUBS_EVENTS = "Publications and Events";
	public static final String RELEASES = "Releases";
	public static final String OTHER = "Other";
	
	private String pubDateString = "";
	private Date pubDate;
	private String source;
	private String author;
	private String title;
	private String link;
	private String description = "";
	private String content = "";
	private String imageName;
	private String brcURL;
	private String brcHoverTitle;
	private String[] tags;
	private int hashCode = -1;
	
	private String category = OTHER;

	public RSSItem() {

	}

	public RSSItem(XMLTreeElement itemElement) {
		XMLTreeElement titleElement = itemElement.getChildOfType("title");
		String title = titleElement.getContent();
		setTitle(title);

		XMLTreeElement linkElement = itemElement.getChildOfType("link");
		String link = linkElement.getContent();
		setLink(link);

		XMLTreeElement descriptionElement = itemElement.getChildOfType("description");
		if(descriptionElement != XMLTreeElement.NO_SUCH_ELEMENT) {
			String description = descriptionElement.getContent();
			setDescription(description);
		}
		XMLTreeElement contentElement = itemElement.getChildOfType("content:encoded");
		if(contentElement != XMLTreeElement.NO_SUCH_ELEMENT) {
			String content = contentElement.getContent();
			setContent(content);
		}

		XMLTreeElement pubDateElement = itemElement.getChildOfType("pubDate");
		if(pubDateElement.equals(XMLTreeElement.NO_SUCH_ELEMENT)) {
			pubDateElement = itemElement.getChildOfType("dc:date");
		}
		if(pubDateElement != XMLTreeElement.NO_SUCH_ELEMENT) {
			String pubDate = pubDateElement.getContent();
			setPubDate(pubDate);
		}

		XMLTreeElement authorElement = itemElement.getChildOfType("author");
		if(authorElement != XMLTreeElement.NO_SUCH_ELEMENT) {
			String author = authorElement.getContent();
			setAuthor(author);
		}
		
		addKeyValuePair("id", getId());
	}

	public int compareTo(Object o) {
		int r = 0;
		RSSItem otherItem = (RSSItem)o;
		if(this.pubDate != null && otherItem.pubDate != null) {
			r = this.pubDate.compareTo(otherItem.pubDate);
		} else {
			r = this.pubDateString.compareTo(otherItem.pubDateString);
		}
		r = -r;
		return r;
	}

	public boolean equals(Object o) {
		boolean r = false;
		RSSItem other = (RSSItem)o;
		r = this.pubDateString.equals(other.pubDateString) 
		    && this.title.equals(other.title)
		    && this.description.equals(other.description);
		
		return r;
	}
	
	public String getId() {
		String r = "" + hashCode();
		return r;
	}
	
	public int hashCode() {
		if(hashCode == -1) {
			hashCode = pubDateString.hashCode() 
			           + title.hashCode() 
			           + description.hashCode();
		}
		return hashCode;
	}

	/**
	 * Must be one of the three categories, "Releases",
	 * "News and Events", "Publications and Events"
	 * @param cat
	 */
	public void setCategory(String cat) {
	    if(RELEASES.equalsIgnoreCase(cat)) {
	    	category = RELEASES;
	    } else if(NEWS_ANNOUNCEMENT.equalsIgnoreCase(cat)) {
	    	category = NEWS_ANNOUNCEMENT;
	    } else if(PUBS_EVENTS.equalsIgnoreCase(cat)) {
	    	category = PUBS_EVENTS;
	    }
	    
	    if(category != null) {
	    	addKeyValuePair("category", category);
	    }
	}
	
	public String getPubDate() {
		String r = null;
		if(pubDate != null) {
			r = "" + (pubDate.getMonth()+1) + "/" + pubDate.getDate() + "/" 
			+ (1900+pubDate.getYear());
			SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
			r = sdf.format(pubDate);
		} else {
			r = pubDateString;
		}
		return r;
	}

	public void setPubDate(String pubDate) {
		this.pubDateString = pubDate;
		String[] dateFormats = new String[]{
				"yyyy-MM-dd'T'hh:mm:ss'Z'",
				"yyyy-MM-dd' 'hh:mm:ss:S"
		};
		try {
			this.pubDate = new Date(pubDateString);
		} catch(IllegalArgumentException iae) {
		}
		if(this.pubDate == null) {
			for(int i = 0; i < dateFormats.length && this.pubDate==null; i++) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormats[i]);
					this.pubDate = sdf.parse(pubDateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			addKeyValuePair("pubDate", getPubDate());
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		addKeyValuePair("source", source);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
		addKeyValuePair("author", author);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		addKeyValuePair("title", title);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
		addKeyValuePair("link", link);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		//description may be in a non-compatible charset
		
		//description may contain quotes, that need to be escaped.
		addKeyValuePair("description", this.description);
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setBRCURL(String brcURL) {
		this.brcURL = brcURL;
		addKeyValuePair("brc_url", brcURL);
	}

	public String getBRCURL() {
		return brcURL;
	}

	public String getBRCHoverTitle() {
		return brcHoverTitle;
	}

	public void setBRCHoverTitle(String brcHoverTitle) {
		this.brcHoverTitle = brcHoverTitle;
		addKeyValuePair("brc_hover_title", brcHoverTitle);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		addKeyValuePair("content", content);
	}

	public String toString() {
		String r = null;

		StringBuffer sb = new StringBuffer();
		String title = getTitle();
		String author = getAuthor();
		String link = getLink();
		String description = getDescription();
		String pubDate = getPubDate();
		String image = getImageName();
		String brcURL = getBRCURL();
		String brcHoverTitle = getBRCHoverTitle();
		String content = getContent();

		sb.append("\t");
		sb.append(title);
		sb.append("\n");
		
		sb.append("\t");
		sb.append(pubDate);
		sb.append("\n");
		
		r = sb.toString();
		return r;
	}
	
	public String getHTML() {
		String r = null;
		    StringBuffer sb = new StringBuffer();
			String title = getTitle();
			String author = getAuthor();
			String link = getLink();
			String description = getDescription();
			String pubDate = getPubDate();
			String image = getImageName();
			String brcURL = getBRCURL();
			String brcHoverTitle = getBRCHoverTitle();
			String content = getContent();
			String id = getId();

			sb.append("<div class=\"news\">");
			sb.append("<span class=\"news_date\">");
			sb.append(pubDate);
			sb.append("</span>"); //close news_date
			sb.append("<span class=\"news_date_provider_separator\">-</span>");
			sb.append("<span class=\"news_provider\">");
//			sb.append("<a href=\"");
//			sb.append(link);
//			sb.append("\">");
			sb.append(getProvider());
//			sb.append("</a>");
			sb.append("");
			sb.append("</span>"); //close news_provider
			sb.append("<span class=\"news_provider_title_separator\"></span>");
			sb.append("<span id=\"");
			sb.append(id + "_title");
			sb.append("\" class=\"news_title\">");
			sb.append("<span class=\"news_title_text\">");
			sb.append(title);
			sb.append("</span>");
			sb.append("</span>"); //close news_title
			sb.append("<span id=\"");
			sb.append(id + "_details\"");
			sb.append("class=\"news_details\"");
			sb.append(">");
			sb.append("</span>");
			sb.append("");
			sb.append("</div>"); //close news_item
			
			r = sb.toString();
			return r;
	}
	
	public String getHTMLWithDetails() {
		String r = null;
	    StringBuffer sb = new StringBuffer();
		String title = getTitle();
		String author = getAuthor();
		String link = getLink();
		String description = getDescription();
		String pubDate = getPubDate();
		String image = getImageName();
		String brcURL = getBRCURL();
		String brcHoverTitle = getBRCHoverTitle();
		String content = getContent();
		String id = getId();

		sb.append("<div class=\"news\">");
		sb.append("<span class=\"news_date\">");
		sb.append(pubDate);
		sb.append("</span>"); //close news_date
		sb.append("<span class=\"news_date_provider_separator\">-</span>");
		sb.append("<span class=\"news_provider\">");
//		sb.append("<a href=\"");
//		sb.append(link);
//		sb.append("\">");
		sb.append(getProvider());
//		sb.append("</a>");
		sb.append("");
		sb.append("</span>"); //close news_provider
		sb.append("<span class=\"news_provider_title_separator\"></span>");
		sb.append("<span id=\"");
		sb.append(id + "_title");
		sb.append("\" class=\"news_title\">");
		sb.append("<span class=\"news_title_text\">");
		sb.append(title);
		sb.append("</span>");
		sb.append("</span>"); //close news_title
		sb.append("<span id=\"");
		sb.append(id + "_details\"");
		sb.append("class=\"news_details\"");
		sb.append(">");
		sb.append(getDetails());
		sb.append("</span>");
		sb.append("");
		sb.append("</div>"); //close news_item
		
		r = sb.toString();
		return r;
}
	
	/**
	 * Returns a String combining the Description and Content fields.
	 * This should be the entire body of the news item.
	 * 
	 * @return
	 */
	public String getDetails() {
		String r = "";
		String desc = getDescription();
		String cont = getContent();
		if(desc != null) {
			r = r + desc;
		}
		if(cont != null) {
			r = r + " " + cont;
		}
		r = r.trim();
		return r;
	}
	
	/**
	 * Associates the given tag with this RSSItem.
	 * 
	 * @param tag
	 */
	public void addTag(String tag) {
		if(tags == null) {
			tags = new String[0];
		}
		
		String[] newTags = new String[tags.length+1];
		System.arraycopy(tags, 0, newTags, 0, tags.length);
		newTags[tags.length] = tag;
		tags = newTags;
	}
	
	/**
	 * Returns true if the specified tag is associated with this RSSItem.
	 * 
	 * @param tag
	 * @return
	 */
//	public boolean hasTag(String tag) {
//		boolean r = false;
//		for(int i = 0; !r && i < tags.length; i++) {
//			r = tags[i].equals(tag);
//		}
//		return r;
//	}

	/**
	 * Returns true is this item has content that contains the query String.
	 * If ignoreCase is true, assumes that query is already toLowerCase().
	 * 
	 * @param query
	 * @return
	 */
	public boolean matches(String query, boolean ignoreCase) {
		boolean r = false;

		String target = title;
		if(target != null) {
			if(ignoreCase) {
				target = target.toLowerCase();
			}

			if(target.contains(query)) {
				r = true;
			}
		}

		if(!r) {
			target = description;
			if(target != null) {
				if(ignoreCase) {
					target = target.toLowerCase();
				}

				if(target.contains(query)) {
					r = true;
				}
			}
		}

		if(!r) {
			target = content;
			if(target != null) {
				if(ignoreCase) {
					target = target.toLowerCase();
				}

				if(target.contains(query)) {
					r = true;
				}
			}
		}


		return r;
	}
}
