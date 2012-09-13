<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<div class="full_view_div">
<div id="title_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">Title / Description</span><br/>
<%=request.getAttribute("title_block")%></div>


<div id="dates_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">Dates</span><br/>
<%=request.getAttribute("dates_block")%></div>

<div id="summary_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">Summary</span><br/>
<%=request.getAttribute("summary_block")%></div>

<div id="person_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">People</span><br/>
<%=request.getAttribute("person_block")%></div>

<div id="publication_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">Publication(s)</h1>
<%=request.getAttribute("publication_block")%></div>


<!--<div id="platform_block">
<hr class="data_set_summary_hr" />
<h1>Platform</h1>
<%=request.getAttribute("platform_block")%></div>
-->

<div id="data_file_block">
<hr class="data_set_summary_hr" />
<span class="section_heading">Data Files</span><br/>
<%=request.getAttribute("data_file_block")%></div>
<br/>
<br/>
</div>