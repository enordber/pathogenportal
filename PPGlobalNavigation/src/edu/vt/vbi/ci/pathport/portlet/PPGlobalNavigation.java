package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.vt.vbi.ci.util.PathPortUtilities;

public class PPGlobalNavigation extends GenericPortlet {
	private static String GLOBAL_NAVIGATION_JSP = "/WEB-INF/jsp/global_navigation.jsp";
	
	//Page name constants
	private static final String HOME_PAGE = "Home";
	private static final String SEARCH_PAGE = "Search";
	private static final String ANALYZE_PAGE = "Analyze";
	private static final String DATA_PAGE = "Data";
	private static final String NEWS_PAGE = "News and Announcements";
	private static final String ABOUT_PAGE = "About";
	
	//selected image names
	private static final String HOME_NAV_BUTTON_SELECTED = "home_nav_button_selected.png";
	private static final String ANALYZE_NAV_BUTTON_SELECTED = "analyze_nav_button_selected.png";
	private static final String DATA_NAV_BUTTON_SELECTED = "data_nav_button_selected.png";
	private static final String ABOUT_NAV_BUTTON_SELECTED = "about_nav_button_selected.png";
	private static final String NEWS_NAV_BUTTON_SELECTED = "news_nav_button_selected.png";
	
	//unselected image names
	private static final String HOME_NAV_BUTTON_UNSELECTED = "home_nav_button_unselected.png";
	private static final String ANALYZE_NAV_BUTTON_UNSELECTED = "analyze_nav_button_unselected.png";
	private static final String DATA_NAV_BUTTON_UNSELECTED = "data_nav_button_unselected.png";
	private static final String ABOUT_NAV_BUTTON_UNSELECTED = "about_nav_button_unselected.png";
	private static final String NEWS_NAV_BUTTON_UNSELECTED = "news_nav_button_unselected.png";
	
	private static final HashMap<String,String> pageToSelectedImageName = new HashMap<String, String>(){
		{
			put(HOME_PAGE, HOME_NAV_BUTTON_SELECTED);
			put(ANALYZE_PAGE, ANALYZE_NAV_BUTTON_SELECTED);
			put(DATA_PAGE, DATA_NAV_BUTTON_SELECTED);
			put(ABOUT_PAGE, ABOUT_NAV_BUTTON_SELECTED);
			put(NEWS_PAGE, NEWS_NAV_BUTTON_SELECTED);
		}
	};
	private static final HashMap<String, String> pageToUnselectedImageName = new HashMap<String, String>(){
		{
			put(HOME_PAGE, HOME_NAV_BUTTON_UNSELECTED);
			put(ANALYZE_PAGE, ANALYZE_NAV_BUTTON_UNSELECTED);
			put(DATA_PAGE, DATA_NAV_BUTTON_UNSELECTED);
			put(ABOUT_PAGE, ABOUT_NAV_BUTTON_UNSELECTED);
			put(NEWS_PAGE, NEWS_NAV_BUTTON_UNSELECTED);
		}
	};
	public PPGlobalNavigation() {
	}
	
	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		String pageName = PathPortUtilities.getPageName(request);
		setNavElementClasses(request, pageName);
		
		// set return content type
		response.setContentType("text/html;charset=utf-8");
        PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(GLOBAL_NAVIGATION_JSP);
		reqDispatcher.include(request, response);
	}
	
	private void setNavElementClasses(RenderRequest request, String pageName) {
		String currentClass = "nav_bar_current_page";
		String otherClass = "nav_bar_link";

		//each link defaults to nav_bar_link
		request.setAttribute(HOME_PAGE, otherClass);
		request.setAttribute(ANALYZE_PAGE, otherClass);
		request.setAttribute(DATA_PAGE, otherClass);
		request.setAttribute(NEWS_PAGE, otherClass);
		request.setAttribute(ABOUT_PAGE, otherClass);
		
		//current page is set to nav_bar_current_page
		request.setAttribute(pageName, currentClass);
		
	}

}
