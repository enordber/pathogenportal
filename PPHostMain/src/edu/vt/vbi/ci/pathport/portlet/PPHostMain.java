package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;

public class PPHostMain extends GenericPortlet{
	private static final String HOST_JSP = "/WEB-INF/jsp/host.jsp";

	public PPHostMain() {
		System.out.println("PPHostMain constructor");
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		getNews();
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(HOST_JSP);
		reqDispatcher.include(request, response);
	}

	private void getNews() {
		PPDataSet[] announcements = PathPortData.getDataOfTypeWithTag(PPDataSet.RSS_DATA, PathPortData.HOST);
		System.out.println("rss data sets: " + announcements.length);
		for(int i = 0; i < announcements.length; i++) {
			System.out.println(announcements[i].getHTML());
		}
	}	
}
