package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;

/**
 * This is the portlet for the main content on the "About" page at
 * Pathogen Portal.
 * 
 * It provides static content.
 * 
 * @author enordber
 *
 */
public class PPAboutMain extends GenericPortlet{
	
	private static final String ABOUT_JSP = "/WEB-INF/jsp/about.jsp";
	private static final String NIAID_RESOURCES = "Related NIAID-Funded Resources";
	private static final String ABOUT_PORTAL = "About Pathogen Portal";
	private static final String ABOUT_BRCS = "About BRCs";
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	/*
	 * Attribute constants
	 */
	private static final String ABOUT_PORTAL_ATT = "about_portal";
	private static final String ABOUT_BRCS_ATT = "about_brcs";
	private static final String ABOUT_NIAID_ATT = "about_niaid";
	private static final String ABOUT_NON_NIAID_ATT = "about_non_niaid";
	
	
	private String aboutPortal;
	private String aboutNiaid;
	private String aboutNonNiaid;
	private String aboutBRCs;

	public PPAboutMain() {
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		response.setContentType(CONTENT_TYPE);

		/*
		 * pullInData() is getting called for each page view, so we know the
		 * data are always current with PathPortData. The data are cached in 
		 * PathPortData, so this doesn't require fetching the content from
		 * remote locations.
		 */
		pullInData();

		request.setAttribute(ABOUT_PORTAL_ATT, getAboutPortal());
		request.setAttribute(ABOUT_BRCS_ATT, getAboutBRC());
		request.setAttribute(ABOUT_NIAID_ATT, getAboutNiaidFriends());
		request.setAttribute(ABOUT_NON_NIAID_ATT, getAboutNonNiaidFriends());
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(ABOUT_JSP);
		reqDispatcher.include(request, response);
	}

	private String getAboutNonNiaidFriends() {
		return aboutNonNiaid;
	}

	private String getAboutNiaidFriends() {
		return aboutNiaid;
	}

	private String getAboutBRC() {
		return aboutBRCs;
	}

	private String getAboutPortal() {
		return aboutPortal;
	}
	
	private void pullInData() {
		PPDataSet[] dataSets = PathPortData.getDataOfType(PathPortData.GOOGLE_SITES);
		for(int i = 0; i < dataSets.length; i++) {
			PPDataSet dataSet = dataSets[i];
			String sourceName = dataSet.getDataSource().getName();
			if(sourceName.equalsIgnoreCase(NIAID_RESOURCES)) {
				aboutNiaid = dataSet.getHTML();
			} else if(sourceName.equalsIgnoreCase(ABOUT_PORTAL)) {
				aboutPortal = dataSet.getHTML();
			} else if(sourceName.equalsIgnoreCase(ABOUT_BRCS)) {
				aboutBRCs = dataSet.getHTML();
			}
		}
	}
}
