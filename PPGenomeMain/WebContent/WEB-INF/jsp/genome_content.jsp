<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<span id="genome_triangle_sidebar_region">
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
  <span id="genome_facet_filter_region">
    <%=request.getAttribute("facets") %>
  </span>

</span>

<span>
    <span class="page_main_title">Genomes</span>
	<span id="data_intro_blurb"> 
	This page summarizes information about genome sequence data being generated by the 
	<a href="http://www.niaid.nih.gov/labsandresources/resources/dmid/gsc/Pages/default.aspx">
	Genome Sequencing Centers for Infectious Disease</a><br/>
	</span>
</span>

<span id="genome_chart_region">
</span>
<span id="genome_table_region" class="">
<br/>
<span id="jump_to_alpha_link_area">
<a href="#A">A</a> - <a href="#B">B</a> - <a href="#C">C</a> - <a href="#D">D</a> - 
<a href="#E">E</a> - <a href="#F">F</a> - <a href="#G">G</a> - <a href="#H">H</a> - 
<a href="#I">I</a> - <a href="#J">J</a> - <a href="#K">K</a> - <a href="#L">L</a> - 
<a href="#M">M</a> - <a href="#N">N</a> - <a href="#O">O</a> - <a href="#P">P</a> - 
<a href="#Q">Q</a> - <a href="#R">R</a> - <a href="#S">S</a> - <a href="#T">T</a> - 
<a href="#U">U</a> - <a href="#V">V</a> - <a href="#W">W</a> - <a href="#X">X</a> - 
<a href="#Y">Y</a> - <a href="#Z">Z</a>
</span>
<br/>
<span id="selected_count_area">
   Showing <%=request.getAttribute("selected_count") %> selected items.<br/>
</span>
<%= request.getAttribute("genome_data") %>
</span>