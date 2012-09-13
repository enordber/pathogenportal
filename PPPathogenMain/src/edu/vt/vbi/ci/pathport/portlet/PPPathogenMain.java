package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;

public class PPPathogenMain extends GenericPortlet{
	private static final String PATHOGEN_JSP = "/WEB-INF/jsp/pathogen.jsp";

	public PPPathogenMain() {
		System.out.println("PPPathogenMain constructor");
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		request.setAttribute("news", getNews());
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(PATHOGEN_JSP);
		reqDispatcher.include(request, response);
	}	
	
	private String getNews() {
		String r = null;
		StringBuffer sb = new StringBuffer();
		PPDataSet[] announcements = PathPortData.getDataOfTypeWithTag(PPDataSet.RSS_DATA, PathPortData.PATHOGEN);
		System.out.println("rss data sets: " + announcements.length);
		for(int i = 0; i < announcements.length; i++) {
			String html = announcements[i].getHTML();
			System.out.println(html);
			sb.append(html);
			sb.append("\n");
		}
		
		r = sb.toString();
		return r;
	}	

}
