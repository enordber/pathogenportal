package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class PPAnalyzeMain extends GenericPortlet {
	private static final String ANALYZE_JSP = "/WEB-INF/jsp/analyze.jsp";

	public PPAnalyzeMain() {
		System.out.println("PPAnalyzeMain constructor");
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(ANALYZE_JSP);
		reqDispatcher.include(request, response);
	}

}
