package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class PPEnvironmentMain extends GenericPortlet{
	private static final String ENVIRONMENT_JSP = "/WEB-INF/jsp/environment.jsp";

	public PPEnvironmentMain() {
		System.out.println("PPDataMain constructor");
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(ENVIRONMENT_JSP);
		reqDispatcher.include(request, response);
	}	
}
