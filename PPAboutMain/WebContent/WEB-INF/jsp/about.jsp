<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<span id="about_toc_region">

    <span id="about_portal_title" class="news_title"> 
       <span class="news_title_text about_toc_area">About Pathogen Portal</span><br/>
    </span>
    <span id="about_portal_details" class="news_details">
        <%=request.getAttribute("about_portal") %>
    </span>
    
    <span id="about_brcs_title" class="news_title"> 
        <span class="news_title_text about_toc_area">About the Bioinformatics Resource Centers</span><br/>
    </span>       
    <span id="about_brcs_details" class="news_details">
        <%=request.getAttribute("about_brcs") %>
    </span>

    <span id="about_niaid_title" class="news_title"> 
       <span class="news_title_text about_toc_area">About Our NIAID-Funded Friends</span><br/>
    </span>
    <span id="about_niaid_details" class="news_details">
        <%=request.getAttribute("about_niaid") %> 
     </span>

    <span id="about_other_title" class="news_title about_toc_area">
        About Our Non-NIAID-Funded Friends<br/>
    </span>
</span>


