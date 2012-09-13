<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<span id="analyze_mini_nav_region"> 
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
</span>
</span>
<span id="analyze_main_region">
    <span class="page_main_title">Analyze</span>
	<span id="analyze_intro_blurb"> 
        Explore and use resources for analyzing host response to infectious disease.<br/>
    </span>
	
	<span id="analyze_rnaseq_region">
	<span class="page_subtitle">
	<a href="http://rnaseq.pathogenportal.org">RNA-Seq Pipeline</a><br/>
	</span>
	    <span id="analyze_rnaseq_content_area" class="analyze_content_area">
<dl>
    <dt>
       Map your RNA-Seq Reads to Reference Genomes<br/>
       <dd>
           -Align your Illumina fastQ reads <strong>(gzipped fastQ files accepted)</strong> against any sequenced genome from<br/> 
                <a href="http://www.eupathdb.org/">EuPathDB</a>,<br/> 
                <a href="http://www.patricbrc.org/">PATRIC</a>,<br/> 
                and <a href="http://www.vectorbase.org/">VectorBase</a>.<br/> 
      </dd>
      <br/>
      <dd>
            -View a <a href="http://rnaseq.pathogenportal.org/static/organisms.html">list of supported genomes.</a><br />
      </dd>
    </dt>
    <dt>
        Estimate Gene Expression Values<br/>
      <dd>
            -Obtain BAM files for the resulting alignments and FPKM expression values for annotated genes and novel transcripts.<br />
	  </dd>
	</dt>
</dl>
	    </span>
	</span>
	<br/><br/>
	<span id="analyze_mg_region">
	<span class="page_subtitle">
	    <a href="/portal/portal/PathPort/ADB/ADB?action=2&&c=mmg">Mouse Model Selection Guide
	    <img alt="image of a mouse" src="/portal-core/pp/images/mouse_left.jpg"/></a>
	    </span> 
	    <span id="analyze_mg_content_area" class="analyze_content_area">
	    <a href="http://www.jax.org"><img alt="Jackson Labs Logo" src="/portal-core/pp/images/jax.jpg"/></a>
	    The <a href="/portal/portal/PathPort/ADB/ADB?action=2&&c=mmg">Mouse Model Strain Selection Guide</a> was developed by the Pathogen Portal Team 
	    in collaboration with The Jackson Laboratory. 
	    The Guide lists pathogens along with mouse strains that have been found
	    to be either susceptible or resistant to infection with the pathogen. 
	    Links are provided to more information about the mouse strains, including 
	    ordering information. Links are also provided to publications documenting 
	    susceptibility or resistance of the mouse strains to specific pathogens.
	    </span>
	</span>
	<br/><br/>
	<span id="analyze_dl_region">
	<span class="page_subtitle">Software</span>
	    <span id="analyze_dl_blurb_area" class="analyze_blurb_area">
	    </span>
	    <span id="analyze_dl_content_area" class="analyze_content_area">
	    </span>
	</span>
</span>