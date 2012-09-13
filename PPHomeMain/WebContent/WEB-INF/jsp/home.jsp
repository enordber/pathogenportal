<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<div id="home_science"><span id="home_sidebar_area"> 
<div id="home_sidebar_content">
<span id="home_sidebar_title">Exploring Infectious Disease </span>
<span id="home_sidebar_subtitle">...via the Disease Triangle</span>
<span id="home_sidebar_blurb">
        <span id="home_sidebar_blurb_a">
Pathogen Portal's Infectious Disease Triangle perspective supports an integrative 
understanding of human disease through the study of multiple levels of interactions 
between a susceptible Host, an infectious Pathogen, and a conducive (to disease) Environment.  
Originating from plant pathology, this ecological approach is increasingly relevant to health sciences.  
Because some diseases include invertebrate Vectors as part of their ecology, the 
Disease Triangle broadly considers Environment to include Vectors.
        </span>
        <br/>
        <span id="home_sidebar_blurb_b">
        New data and analysis tools will be added regularly to Pathogen Portal.
        </span>
        <br/>
</span>
<br/>
<br/>

<span id="home_triangle_links_area">
    <span id="home_triangle_links_title">Start Exploring</span><br/>
	<span class="triangle_link"><a href="Host">Host (Human and Model Organisms)</a></span><br/>
	<span class="triangle_link"><a href="Pathogen">Pathogen</a></span><br/>
	<span class="triangle_link"><a href="Environment">Environment/Vector</a></span><br/>
</span>

</div> 

</span> 

<span id="home_triangle_area"> <img
	src="/portal-core/themes/pathport/images/home_triangle.png"
	alt="Disease Triangle" usemap="#dtmap" /> </span>
</div>

<div id="region_separator"> </div>
<div id="home_admin">
<span id="home_about_area">
<div id="home_about_content">
<span class="section_heading underline_bar"><a href="/portal/portal/PathPort/About">About the Pathogen Portal</a></span>
<br/>
        <span id="home_about_pathport_diagram_area"><embed id="home_about_pathport_embed" src="/portal-core/layouts/pathport/about_pathport.svg"/></span>
 
    <span id="home_about_blurb">
The Pathogen Portal is a web-based information system supporting and linking to five Bioinformatics Resource Centers (BRCs). 
Each BRC specializes in a different group of pathogens, generally including, but not limited to, those in the NIAID Category 
A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases.  While 
individual BRCs provide a pathogen community-centered focus of the sponsoring NIAID program, the Portal is an entryway to 
all BRCs, providing information, links, summary data, and supporting tools.  Additionally, the Portal scientific focus is 
on integration of host response data that span pathogens supported by the BRCs, thereby moving towards an integrative view 
of Host-Pathogen-Environment interactions (also known as Disease Triangle).  The Pathogen Portal is developed and maintained 
by the Cyberinfrastructure Division at the Virginia Bioinformatics Institute at Virginia Tech.
     </span>       

</div>
</span>

<span id="home_admin_separator"><img src="/portal-core/themes/pathport/images/home_admin_separator.png" alt=""></span>

<span id="home_news_area">
<div id="home_news_content">
<span class="section_heading underline_bar"><a href="/portal/portal/PathPort/News+and+Announcements">News & Announcements from the BRCs</a></span>
<br/>
    <span id="home_news_feature_area"> 
        <%= request.getAttribute("feature")%>
    </span>
    <span class="news_hr"></span>
    <span class="news_hr"> </span>
    <span id="home_recent_news"><%=request.getAttribute("news") %></span>
</div>
</span>
</div>
