<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<%
	String wName = renderRequest.getWindowID();
	int index = wName.indexOf('/');
	wName = wName.substring(wName.lastIndexOf('/') + 1);

	String updateFilterMethodName = "MainWindow_updateFilterMethod";
	String getDetailsMethodName = wName + "_getDetailsMethod";
	String contentDivName = "MainWindow_page_content";
	String loadMethodName = wName + "_loadMethod";

			String gaugeScript = "<script>\n" 
			+ "var " + updateFilterMethodName
			+ " = function() { \n"  
		    +  "    new Ajax.Request('"
			+ renderResponse.createResourceURL() + "', \n"
			+ "             {method: 'GET', \n"
			+ "             parameters:getFilterStatus(), \n" 
			+ "             onSuccess:function(transport) { " 
		    + " filterRequestComplete(transport);}"  
			+ "});\n"
			+ "}\n"
			
			+ "var " + getDetailsMethodName
			+ " = function(id) { \n"  
		    +  "    new Ajax.Request('"
			+ renderResponse.createResourceURL() + "', \n"
			+ "             {method: 'GET', \n"
			+ "             parameters:{\"action\":\"details\", \"id\":id}, \n" 
			+ "             onSuccess:function(transport) { " 
		    + " detailsRequestComplete(transport);}"
			+ "});\n"
			+ "}\n"			+ ""
			+ ""
			+""
			+""
			+ "</script>\n";

%>
<div class="full_view_div">
<div class="news_gauge"><%= gaugeScript %>
<div id="<%=contentDivName %>">

<span id="triangle_sidebar_region">
<span id="dt_image_area">
    <img
	src="/portal-core/themes/pathport/images/mini_nav_triangle.png"
	alt="Disease Triangle diagram" usemap="#mininavdtmap" />
	<br/>
    <span id="mini_nav_triangle_links"> 
        <img id="explore_arrow" src="/portal-core/themes/pathport/images/small_right_arrow.png" /> 
        <span id="mini_nav_triangle_links_title">Continue Exploring</span>
        <br/>
    	<span class="triangle_link"><a href="Host">Host</a></span>
	    <span class="triangle_link"><a href="Pathogen">Pathogen</a></span>
	    <span class="triangle_link"><a href="Environment">Environment/Vector</a></span>
    </span>
</span>

<span id="facet_filter_region">
<%=request.getAttribute("facets") %>
</span>
</span>

<%=request.getAttribute("selected") %>
<span id="news_region">
<%=request.getAttribute("news") %>
</span>

</div>
</div>
</div>


