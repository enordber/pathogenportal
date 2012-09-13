package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;
import edu.vt.vbi.ci.pathport.RSSItem;

public class PPHomeMain extends GenericPortlet{
	private static final String HOME_JSP = "/WEB-INF/jsp/home.jsp";
	private static final String FEATURE = "feature";
	private static final String NEWS = "news";

	public PPHomeMain() {
		System.out.println("PPHomeMain constructor");
		PathPortData.init();
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		PathPortData.init();
		//see if it's a reload action. If so, reload, then move on
		String reload = request.getParameter("reload");
		System.out.println("reload: " + reload);
		if(reload != null && reload.equalsIgnoreCase("true")) {
			System.out.println("trigger data source reload");
			PathPortData.reloadDataSources();
		}
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		setNewsItems(request);
		//request.setAttribute(FEATURE, getFeaturedNewsItem());
		//request.setAttribute(NEWS, getNewsItems());
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(HOME_JSP);
		reqDispatcher.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response)
	throws PortletException, IOException  {
		//see if it's a reload action. If so, reload, then move on
		String reload = request.getParameter("reload");
		System.out.println("reload: " + reload);
		if(reload != null && reload.equalsIgnoreCase("true")) {
			System.out.println("trigger data source reload");
			PathPortData.reloadDataSources();
		}

		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		writer.write("reloaded all data sources");
		writer.flush();
		writer.close();

	}

	private void setNewsItems(RenderRequest request) {
		String news = null;
		news = "There are no available news items.";

		String feature = "There is no item to feature.";

		//get single most recent news item for each BRC
		ArrayList<PPDataSet> recentItemList = new ArrayList<PPDataSet>(6);
		PPDataSet[] itemHolder = null;

		//eupath
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.EUPATHDB);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//ird
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.IRD);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//pathogenportal
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.PATHOGENPORTAL);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//patric
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.PATRIC);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//vectorbase
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.VECTORBASE);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//vipr
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.VIPR);
		if(itemHolder != null && itemHolder.length > 0) {
			//			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		itemHolder = null;

		PPDataSet[] newsItems = new PPDataSet[recentItemList.size()];
		recentItemList.toArray(newsItems);
		//        System.out.println("recent news items collected: " + newsItems.length);
		if(newsItems != null) {
			Arrays.sort(newsItems);
			//newsItems[0] is the most recent, and will be the featured item
			int featureIndex = 0; //should be 0 usually - this is for testing
			if(newsItems.length > 0) {
				feature = formatFeaturedItem((RSSItem) newsItems[featureIndex]);
				//the others will be added to the headlines area
				StringBuffer sb = new StringBuffer();
				for(int i = 0;  i< newsItems.length; i++) {
					if(i != featureIndex) {
						sb.append(newsItems[i].getHTML());
					}
				}

				news = sb.toString(); 
			}
		}
		
		request.setAttribute("feature", feature);
		request.setAttribute("news", news);
	}

	private String formatFeaturedItem(RSSItem newsItem) {
		String r = null;
		
		String headline = newsItem.getTitle();
		String pubDate = newsItem.getPubDate();
		String provider = newsItem.getProvider();
		String description = newsItem.getDescription();
		String id = newsItem.getId();
		String trimDescription = description + " "; //add space at end to make sure
		                                        //finding the first space after 
		                                     //roughLimit actually finds a space
		
		Document descDoc = Jsoup.parse(description);
		trimDescription = descDoc.text() + " ";
        System.out.println("trimDescription: " + trimDescription);
		int roughLimit = 150;
		if(trimDescription.length() > roughLimit) {
			trimDescription = trimDescription.substring(0, 
					trimDescription.indexOf(" ", roughLimit)) 
					+ "<span id=\"" + id +"_explink\" class=\"news_title\">... click to see more</span>";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"feature\">");
		sb.append("<span id=\"");
		sb.append(id + "_title");
		sb.append("\" class=\"feature news_title\">");
		sb.append("<span class=\"news_title_text\">");
		sb.append(headline);
		sb.append("<br/></span>");
		sb.append("</span>");
		sb.append("<br/>");
		sb.append("<span class=\"feature news_date\">");
		sb.append(pubDate);
		sb.append("</span>");
		sb.append("<span class=\"news_date_provider_separator\">");
		sb.append("-");
		sb.append("</span>");
		sb.append("<span class=\"feature news_provider\">");
		sb.append(provider);
		sb.append("</span>");
		sb.append("<br/><br/>");
		sb.append("<span id=\"");
		sb.append(id + "_description");
		sb.append("\" class=\"news_description feature description\">");
		sb.append(trimDescription);
		sb.append("</span>");
		sb.append("<span id=\"");
		sb.append(id + "_details\"");
		sb.append("class=\"news_details\"");
		sb.append(">");
		sb.append("</span>");
		sb.append("");
		sb.append("");
		sb.append("<br/></div>"); //close .news .feature span
		
		r = sb.toString();
		return r;
	}

	private String getFeaturedNewsItem() {
		String r = null;
		r = "It's the super awesome Featured News Item.";
		return r;
	}

	private String getNewsItems() {
		String r = null;
		r = "There are no available news items.";

		//get single most recent news item for each BRC
		ArrayList<PPDataSet> recentItemList = new ArrayList<PPDataSet>(6);
		PPDataSet[] itemHolder = null;

		//eupath
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.EUPATHDB);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//ird
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.IRD);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//pathogenportal
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.PATHOGENPORTAL);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//patric
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.PATRIC);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//vectorbase
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.VECTORBASE);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		//vipr
		itemHolder = PathPortData.getDataOfTypeWithTag(PathPortData.RSS, PathPortData.VIPR);
		if(itemHolder != null && itemHolder.length > 0) {
			System.out.println("adding item: " + itemHolder[0]);
			recentItemList.add(itemHolder[0]);
		}
		itemHolder = null;

		PPDataSet[] newsItems = new PPDataSet[recentItemList.size()];
		recentItemList.toArray(newsItems);
		System.out.println("recent news items collected: " + newsItems.length);
		if(newsItems != null) {
			Arrays.sort(newsItems);
			StringBuffer sb = new StringBuffer();
			for(int i = 0;  i< newsItems.length; i++) {
				sb.append(newsItems[i].getHTML());
			}

			r = sb.toString();
		}

		return r;
	}
}
