<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<span id="global_navigation_area">

<span id="main_content_nav_area">
    <span class="<%=request.getAttribute("Home") %>">
        <a href="/portal/portal/PathPort/Home">
    <span id="home_nav_button_area">
            <img src="/portal-core/themes/pathport/images/mini_triangle_crop_29_25.png" alt="small image representing disease triangle"/>
            Home
    </span>
        </a>
    </span>

<span class="<%=request.getAttribute("Data") %>">
<a href="/portal/portal/PathPort/Data">
<span id="data_nav_button_area" class="nav_bar_link">
            <img src="/portal-core/themes/pathport/images/data_icon.png" alt="small database icon"/>
    Data
</span>
</a>
</span>

<span class="<%=request.getAttribute("Analyze") %>">
<a href="/portal/portal/PathPort/Analyze">
<span id="analyze_nav_button_area">
            <img src="/portal-core/themes/pathport/images/analyze_icon.png" alt="small monkey wrench icon"/>
    Analyze
</span>
</a>
</span>


</span>

<span id="admin_nav_area">
    <span class="<%=request.getAttribute("About") %>">
        <a href="/portal/portal/PathPort/About">
            <span id="about_nav_button_area">
                About
            </span>
        </a>
    </span>

    <span class="<%=request.getAttribute("News and Announcements") %>">
        <a href="/portal/portal/PathPort/News+and+Announcements">
            <span id="news_nav_button_area">
               News and Announcements
            </span>
        </a>
    </span>

</span>
</span>
