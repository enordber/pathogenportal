package edu.vt.vbi.ci.pathport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.vt.vbi.ci.util.PathPortUtilities;
import edu.vt.vbi.ci.util.parse.XMLTreeElement;

/**
 * Handles RSS feeds for Pathogen Portal. This includes fetching the content
 * and parsing it. This also includes caching content, as appropriate.
 * 
 * @author enordber
 *
 */
public class PPRSS extends PPDataSourceLoader {
	
	private static final String RSS = "rss";
	/*
	 * PATRIC RSS categories
	 */
	private static final String NEWS = "News & Events";
	private static final String PUBLICATIONS = "Publications & Presentations";
	private static final String PRESENTATIONS = "Presentations";
	private static final String RELEASES =      "Releases & Updates";

	private static final String CATEGORY = "category";
	private static final String CATEGORY_FACET = "Category"; 
	private static final String SOURCE_FACET = "Source";

	/*
	 * number of milliseconds old a feed is allowed to become before it
	 * is reloaded.
	 */
	private static final long STALE_TIME = 7200000; //2 hours

	public PPRSS() {
	}
	
	public void loadItemsFromSource(PPDataSource dataSource) {
		//make sure its an rss data source
		if(dataSource.isType(RSS)) {
			String[] urls = dataSource.getLocationURLs();
			for(int i = 0; i < urls.length; i++) {
				System.out.println("RSS url: " + urls[i]);
				String feedContent = 
					PathPortUtilities.getContentsOfURL(urls[i]);
//				System.out.println("feedContent:");
//				System.out.println(feedContent);

				/////fix problem parsing patric feed.////// 
				String find = "CDATA\\[\\]";
				String replace = "CDATA\\[ \\]";
				feedContent = feedContent.replaceAll(find, replace);
				//////////////////////////////////////////

				XMLTreeElement rssElement = 
					new XMLTreeElement(feedContent);

				XMLTreeElement[] items = 
					rssElement.getDescendantsOfType("item");

				String feedCategory = "";
				if(dataSource.hasTag(PathPortData.PRESENTATIONS)) {
					feedCategory = PRESENTATIONS;
				} else if(dataSource.hasTag(PathPortData.PUBLICATIONS)) {
					feedCategory = PUBLICATIONS;
				} else if(dataSource.hasTag(PathPortData.NEWS)) {
					feedCategory = NEWS;
				} else if(dataSource.hasTag(PathPortData.RELEASES)) {
					feedCategory = RELEASES;
				}
				String source = "";
				if(dataSource.hasTag(PathPortData.EUPATHDB)) {
					source = "EuPathDB";
				} else if(dataSource.hasTag(PathPortData.IRD)) {
					source = "IRD";
				} else if(dataSource.hasTag(PathPortData.PATHOGENPORTAL)) {
					source = "Pathogen Portal";
				} else if(dataSource.hasTag(PathPortData.PATRIC)) {
					source = "PATRIC";
				} else if(dataSource.hasTag(PathPortData.VECTORBASE)){
					source = "VectorBase";
				} else if(dataSource.hasTag(PathPortData.VIPR)) {
					source = "ViPR";
				}
				
				ArrayList<PPDataSet> itemList = new ArrayList<PPDataSet>(items.length);

				for(int k = 0; k < items.length; k++) {
					boolean skip = false;
					RSSItem item = new RSSItem(items[k]);
					item.setDataSource(dataSource);
					item.setTags(dataSource.getTags());
					item.setProvider(dataSource.getProvider());
					
					item.addFacetValue(CATEGORY_FACET, feedCategory);
					item.addFacetValue(SOURCE_FACET, source);

					if(dataSource.hasTag(PathPortData.PATRIC)) { 
						//PATRIC items are combined in a single feed, and are 
						//distinguished by a CATEGORY tag
						skip = true;
						XMLTreeElement categoryElement = items[k].getChildOfType(CATEGORY);
						String category = categoryElement.getContent();
						if(category.equalsIgnoreCase(PathPortData.NEWS) && dataSource.hasTag(PathPortData.NEWS)) {
							skip = false;
						}
						if(category.equalsIgnoreCase("Data Releases & Updates") && dataSource.hasTag(PathPortData.RELEASES)) {
							skip = false;
						}
						if((category.equalsIgnoreCase(PathPortData.PUBLICATIONS) || category.equalsIgnoreCase(PathPortData.PRESENTATIONS)) 
								&& dataSource.hasTag(PathPortData.PUBLICATIONS)) {
							skip = false;
						}
					}
					if(!skip) {
//						item.setFacetValue(CATEGORY, category);
						itemList.add(item);
					}
				}
//				System.out.println("items loaded from this source: " + itemList.size());
				PPDataSet[] dataSets = new PPDataSet[itemList.size()];
				itemList.toArray(dataSets);
				dataSource.setDataSets(dataSets);
			}
		}
	}
}
