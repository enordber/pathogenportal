package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import edu.vt.vbi.ci.pathport.ExtendedBitSet;
import edu.vt.vbi.ci.pathport.FacetCutter;
import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;
import edu.vt.vbi.ci.pathport.RSSItem;

public class PPNewsMain extends GenericPortlet{
	private static final String NEWS_JSP = "/WEB-INF/jsp/news.jsp";
	private static final String NEWS_CONTENT_JSP = "/WEB-INF/jsp/news_content.jsp";
	private static final String NEWS_DETAILS_JSP = "/WEB-INF/jsp/news_details.jsp";

	/**
	 * ajax action constants
	 */
	private static final String FILTER = "filter";
	private static final String DETAILS = "details";

	private RSSItem[] allNewsItems;
	private HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap;

	public PPNewsMain() {
//		System.out.println("PPNewsMain constructor");
//		updateNewsItems();
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		//updating news items each time is not necessary, but doing it now until I can
		//implement method of updating only when things have changed.
		updateNewsItems();

		// set return content type
		response.setContentType("text/html;charset=utf-8");

//		printJunk();
		FacetCutter.getSelectedFacetValues(request);
		setContent(request, null);
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(NEWS_JSP);
		reqDispatcher.include(request, response);
	}

	private void printJunk() {
		System.out.println("PPNewsMain.printJunk()");
		for(int i = 0; i < allNewsItems.length; i++) {
			System.out.println(allNewsItems[i].getFacetJSON());
			System.out.println(allNewsItems[i].getJSONString());
		}
	}
	
	public void serveResource(ResourceRequest request, ResourceResponse response) {
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(NEWS_DETAILS_JSP);
		//updating news items each time is not necessary, but doing it now until I can
		//implement method of updating only when things have changed.
		updateNewsItems();

		String operation = request.getParameter("op");
		System.out.println("operation param: " + operation);

		if(operation != null && operation.equalsIgnoreCase(FILTER)) {
			// set return content type
			response.setContentType("text/html;charset=utf-8");

			String[][] selectedFacetNameValuePairs = FacetCutter.getSelectedFacetValues(request);
			ExtendedBitSet selectedSet= determineSelectedMembers(request, selectedFacetNameValuePairs);
			
			reqDispatcher = 
				getPortletContext().getRequestDispatcher(NEWS_CONTENT_JSP);
		} else if(operation != null && operation.equalsIgnoreCase(DETAILS)) {
			// set return content type
			response.setContentType("application/json;charset=utf-8");
			String id = request.getParameter("id");
			loadDetailsForID(request, id);
		} else if(operation != null && operation.equalsIgnoreCase("ping")) {
			response.setContentType("application/json;charset=utf-8");
			String reply = "{'msg':'here is the ping'}";
			request.setAttribute("json", reply);

		}

		try {
			reqDispatcher.include(request, response);
		} catch (PortletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getDetailsForId(String id) {
		String r = null;
		//find the newsItem with this id
		//remove any suffix that has been added to the id
		id = id.split("_")[0];
		RSSItem newsItem = getNewsItemWithId(id);
		r = newsItem.getDetails();
		
		//try to remove any onclick javascript code
		String onclickRegex = "onclick=\".*?\"";
		r = r .replaceAll(onclickRegex, "");
		int quoteIndex = r.indexOf("'");
		int smartQuoteIndex = r.indexOf("\\u201c");
		return r;
	}
	
	private void loadDetailsForID(ResourceRequest request, String id) {
		//remove any suffix that has been added to the id
		id = id.split("_")[0];

		//There have been charset troubles, so make sure the default charset is used
		String details = getDetailsForId(id);
		details = new String(details.getBytes(Charset.defaultCharset()));

		//escape quotes and backslashes 
		details = details.replaceAll("\"", "\\\"");
		details = details.replaceAll("/", "\\/");
		details = details.replaceAll("\\n", "\\\\n");

		//create JSON text with id and details.
		String json = "{'id':\'" + id + "\', 'details':\'" + details + "\'}";
		
		request.setAttribute("json", json);
	}

	/**
	 * simple linear search now. this can be sped up with a HashMap, if needed.  
	 * @param id
	 * @return
	 */
	private RSSItem getNewsItemWithId(String id) {
    	RSSItem r = null;
    	for(int i = 0; r == null && i < allNewsItems.length; i++) {
    		if(id.equals(allNewsItems[i].getId())) {
    			r = allNewsItems[i];
    		}
    	}
		return r;
	}

	private void setContent(PortletRequest request, ExtendedBitSet selectedSet) {
		if(selectedSet == null) {
			//a null value for selectedSet indicates that all items should be displayed,
			//as no filters have been applied. 
			//Create a BitSet the same length as newsDataSets with all of the values set, 
			//so every item will be included
			selectedSet = new ExtendedBitSet();
			selectedSet.set(0, allNewsItems.length);
		}
		
		//odd rows get an extra css class added, so they can
		//be rendered a bit differently to make it easier
		//to distinguish between rows.
		//RSSItems are wrapped in a div with class="news". For odd rows,
		//replace this with class="news oddrow"
		String newsClass = "class=\"news\"";
		String oddRowClass = "class=\"news oddrow\"";

		StringBuffer sb = new StringBuffer();
		String selectedHTML = "";
		if(allNewsItems != null && selectedSet != null) {
			int[] selectedIndices = selectedSet.getSetValues();
			sb.append("<span id=\"selected_count_area\">");
			sb.append("Showing " + selectedIndices.length + " selected items. Click on the headline to see details.<br/>");
			sb.append("</span>");
			selectedHTML = sb.toString();
			
			sb = new StringBuffer();

			for(int i = 0; i < selectedIndices.length; i++) {
				if(allNewsItems.length > selectedIndices[i]) {
					String itemHTML = allNewsItems[selectedIndices[i]].getHTML();
					if((i & 1) != 0) {
						//this is an odd row (1 bit is not set)
						itemHTML = itemHTML.replaceAll(newsClass, oddRowClass);
					}
					sb.append(itemHTML);
					sb.append(" ");
				}
			}
		}

		request.setAttribute("news", sb.toString());
		request.setAttribute("facets", getFacetHTML(allNewsItems, fullFacetMap));
		request.setAttribute("selected", selectedHTML);
	}

	private String getNewsContent(PortletRequest request, ExtendedBitSet selectedSet, HashSet<String> showDetails) {
		String r = null;
		if(selectedSet == null) {
			//a null value for selectedSet indicates that all items should be displayed,
			//as no filters have been applied. 
			//Create a BitSet the same length as newsDataSets with all of the values set, 
			//so every item will be included
			selectedSet = new ExtendedBitSet();
			selectedSet.set(0, allNewsItems.length);
		}

		//odd rows get an extra css class added, so they can
		//be rendered a bit differently to make it easier
		//to distinguish between rows.
		//RSSItems are wrapped in a div with class="news". For odd rows,
		//replace this with class="news oddrow"
		String newsClass = "class=\"news\"";
		String oddRowClass = "class=\"news oddrow\"";
		
		StringBuffer sb = new StringBuffer();
		String selectedHTML = "";
		if(allNewsItems != null && selectedSet != null) {
			int[] selectedIndices = selectedSet.getSetValues();
			sb.append("<span id=\"selected_count_area\">");
			sb.append("Showing " + selectedIndices.length + " selected items. Click on the headline to see details.<br/>");
			sb.append("</span>");
			selectedHTML = sb.toString();
			
			sb = new StringBuffer();

			for(int i = 0; i < selectedIndices.length; i++) {
				int index = selectedIndices[i];
				if(allNewsItems.length > index) {
					RSSItem item = allNewsItems[index];
					String itemHTML = null;
					if(showDetails.contains(item.getId())) {
						itemHTML = item.getHTMLWithDetails();
					} else {
						itemHTML = item.getHTML();
					}
					if((i & 1) != 0) {
						//this is an odd row (1 bit is not set)
						itemHTML = itemHTML.replaceAll(newsClass, oddRowClass);
					}
					sb.append(itemHTML);
					sb.append(" ");
				}
			}
		}

		request.setAttribute("selected", selectedHTML);

		r = sb.toString();
		return r;
	}

	/**
	 * Determines which news items should be displayed, and adds content
	 * to the PortletRequest.
	 * 
	 * @param selectedFacetNameValuePairs
	 */
	private  ExtendedBitSet determineSelectedMembers(PortletRequest request, String[][] selectedFacetNameValuePairs) {
		ExtendedBitSet r = null;

		//check request parameters to see which items should contain full details
		HashSet<String> detailIds = new HashSet<String>();
		String detailParam = request.getParameter("details");
		if(detailParam != null) {
			String[] detailSplit = detailParam.split(",");
			for(int i = 0; i < detailSplit.length; i++) {
				if(detailSplit[i].length() > 0) {
                    String id = detailSplit[i];
                    //remove any suffix that may be appended to the id
					id = id.split("_")[0];
					detailIds.add(id);
				}
			}
		}
		
		//within a facet, OR
		HashMap<String, ExtendedBitSet> selectedFacetSets = new HashMap<String, ExtendedBitSet>();
		//populate selectedFacetSets with facetName keys, and empty BitSets
		String facetName;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			if(!selectedFacetSets.containsKey(facetName)) {
				selectedFacetSets.put(facetName, new ExtendedBitSet());
			}
		}

		HashSet<String > selectedCheckboxIds = new HashSet<String>();

		if(selectedFacetNameValuePairs.length == 0) {
			r = new ExtendedBitSet();
			r.set(0, allNewsItems.length);
		} else {
			//for each facet name-value pair, get the BitSet for the members from the
			//fullFacetMap. OR each of these to the facet BitSet in selectedFacetSets
			String facetValue;
			for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
				facetName = selectedFacetNameValuePairs[i][0];
				facetValue = selectedFacetNameValuePairs[i][1];
				ExtendedBitSet facetValueMembers = 
					fullFacetMap.get(facetName).get(facetValue);

				selectedFacetSets.get(facetName).or(facetValueMembers);
				String checkboxId = facetName + "--" + facetValue;
				selectedCheckboxIds.add(checkboxId);
			}

			r = new ExtendedBitSet();
			String[] facetNames = new String[selectedFacetSets.size()];
			selectedFacetSets.keySet().toArray(facetNames);

			//then do an AND of the facets
			ExtendedBitSet[] facetSets = new ExtendedBitSet[selectedFacetSets.size()];
			selectedFacetSets.values().toArray(facetSets);
			if(facetSets.length > 0) {
				//start off by setting the members that selected by the first facet
				r.or(facetSets[0]);
			}
			//AND with the rest of the facet selection sets
			for(int i = 1; i < facetSets.length; i++) {
				r.and(facetSets[i]);
			}
		}

		request.setAttribute("news", getNewsContent(request, r, detailIds));
		request.setAttribute("facets", getFacetHTML(fullFacetMap, selectedFacetSets, selectedCheckboxIds, getAllNewsItems().length));

		return r;
	}

	private void updateNewsItems() {
		allNewsItems = getAllNewsItems();
		Arrays.sort(allNewsItems);
		fullFacetMap = FacetCutter.getFacetMap(allNewsItems);
	}


	private static String getFacetHTML(HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap, HashMap<String, ExtendedBitSet> selectedFacetSets, HashSet<String> selectedCheckboxIds, int itemCount) {
		String r;
		StringBuffer sb = new StringBuffer();
		String[] facetNames = new String[fullFacetMap.size()];
		fullFacetMap.keySet().toArray(facetNames);
		ExtendedBitSet[] facetSets = new ExtendedBitSet[facetNames.length];
		for(int i = 0; i < facetNames.length; i++) {
			facetSets[i] = selectedFacetSets.get(facetNames[i]);
			if(facetSets[i] == null) {
				//this happens when no values for this facet are selected, 
				//so the facetName is not in the request parameter, so no
				//BitSet entry is created for the facet. The convention with 
				//faceted search is that no selected values means the same as 
				//every value for the facet being selected (I'm not making that
				// up. That's really the convention). So we will follow that
				//convention here and create a BitSet representing all items
				//for this facet
				facetSets[i] = new ExtendedBitSet();
				facetSets[i].set(0, itemCount);

				//the alternative way to handle no selections is to really treat
				//it as no selections, and show no resulting items. This would
				//be accomplished by removing the line above that sets the bits
				//in facetSets[i].
			}
		}

		for(int i = 0; i < facetNames.length; i++) {
			HashMap<String, ExtendedBitSet> facetValueMap = fullFacetMap.get(facetNames[i]);
			String[] facetValues = new String[facetValueMap.size()];
			facetValueMap.keySet().toArray(facetValues);
			Arrays.sort(facetValues);
			sb.append("<span class=\"facet\">");
			sb.append("<span class=\"facet_name\">");
			sb.append(facetNames[i]);
			sb.append("<br/>");
			sb.append("</span>");
			sb.append("<span class=\"facet_values\">");
			for(int j = 0; j < facetValues.length; j++) {
				String facetNameValueId = facetNames[i] + "--" + facetValues[j];
				//determine the number of additional items that would be selected 
				//by selecting this facetValue. This is determined by counting the
				//number of set bits far this facetValue that are set in 
				//any of the other combined facet selection sets
				ExtendedBitSet facetValueSet = facetValueMap.get(facetValues[j]);
				ExtendedBitSet setByOtherFacets = new ExtendedBitSet();
				for(int k = 0; k < facetNames.length; k++) {
					if(k != i) { //don't check versus the same facet that this 
						//facet value belongs to
						setByOtherFacets.or(facetSets[k]);
					}
				}
				facetValueSet = facetValueSet.getAnd(setByOtherFacets);

				sb.append("<span class=\"facet_value\">");
				sb.append("<label>");
				sb.append("<input type=\"checkbox\" id=\""); 
				sb.append(facetNameValueId);
				sb.append("\"");
				sb.append(" class=\"news_filter_checkbox\""); 
				if(selectedCheckboxIds.contains(facetNameValueId)) {
					sb.append(" checked=\"checked\"");
				} 
				sb.append(" />");
				sb.append(facetValues[j]);
				sb.append("<span class=\"facet_value_count\">");
				sb.append("(");
				sb.append(facetValueSet.cardinality());
				sb.append(")");
				sb.append("<br/>");
				sb.append("</span>");
				sb.append("</label>");
				sb.append("</span>");
			}
			sb.append("</span>"); //close facet_values
			sb.append("<br/>");
			sb.append("</span>"); //close facet
		}
		r = sb.toString();
		return r;
	}

	private static String getFacetHTML(PPDataSet[] newsDataSets, HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap) {
		String r;
		StringBuffer sb = new StringBuffer();
		String[] facetNames = new String[fullFacetMap.size()];
		fullFacetMap.keySet().toArray(facetNames);
		for(int i = 0; i < facetNames.length; i++) {
			HashMap<String, ExtendedBitSet> facetValueMap = fullFacetMap.get(facetNames[i]);
			String[] facetValues = new String[facetValueMap.size()];
			facetValueMap.keySet().toArray(facetValues);
			Arrays.sort(facetValues);
			sb.append("<span class=\"facet\">");
			sb.append("<span class=\"facet_name\">");
			sb.append(facetNames[i]);
			sb.append("<br/>");
			sb.append("</span>");
			sb.append("<span class=\"facet_values\">");
			for(int j = 0; j < facetValues.length; j++) {
				sb.append("<span class=\"facet_value\">");
				sb.append("<label>");
				sb.append("<input type=\"checkbox\" id=\""); 
				sb.append(facetNames[i] + "--" + facetValues[j]);
				sb.append("\"");
				sb.append(" class=\"news_filter_checkbox\" checked=\"checked\"");
				sb.append(" />");
				sb.append(facetValues[j]);
				sb.append("<span class=\"facet_value_count\">");
				sb.append("(");
				sb.append(facetValueMap.get(facetValues[j]).cardinality());
				sb.append(")");
				sb.append("<br/>");
				sb.append("</span>");
				sb.append("</label>");
				sb.append("</span>");
			}
			sb.append("</span>"); //close facet_values
			sb.append("<br/>");
			sb.append("</span>"); //close facet
		}
		r = sb.toString();
		return r;
	}

	private RSSItem[] getAllNewsItems() {
		RSSItem[] r = null;
		PPDataSet[] items = PathPortData.getDataOfType(PathPortData.RSS);
		//remove any duplicate items, and sort 		
		HashSet<PPDataSet> itemSet = new HashSet<PPDataSet>();
		for(int i = 0; i < items.length; i++) {
			itemSet.add(items[i]);
		}
		
		r = new RSSItem[itemSet.size()];
		itemSet.toArray(r);
		return r;
	}


}
