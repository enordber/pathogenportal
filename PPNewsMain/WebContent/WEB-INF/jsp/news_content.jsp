<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

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
