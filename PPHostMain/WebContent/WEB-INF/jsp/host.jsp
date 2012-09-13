<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<span id="triangle_sidebar_region">
<span id="dt_image_area">
    <img
	src="/portal-core/themes/pathport/images/host_triangle.png"
	alt="Disease Triangle emphasizing Host" usemap="#hostdtmap" />
	<br/>
    <span id="host_triangle_links"> 
        <img id="explore_arrow" src="/portal-core/themes/pathport/images/small_right_arrow.png" /> 
        <span id="mini_nav_triangle_links_title">Continue Exploring</span>
        <br/>
    	<span class="triangle_link"><a href="Host">Host</a></span>
	    <span class="triangle_link"><a href="Pathogen">Pathogen</a></span>
	    <span class="triangle_link"><a href="Environment">Environment/Vector</a></span>
    </span>
</span>
</span>

<span id="host_science_region" class="triangle_science_region">
    	<span class="page_main_title">Host</span>
	<span id="host_intro_blurb"> 
        Explore data and tools for host response to pathogens spanning the BRCs.<br/>
        </span>

	<span id="host_data_analyze_region" class="triangle_data_analyze_region">

		<span id="host_data_area" class="triangle_data_area">
		<span class="triangle_data_heading page_subtitle">Data<br/></span>
			<span id="host_data_content" class="triangle_data_content">
  
      <span id="data_transcriptome_area">
             <span class="data_content_area">
		        <img src="/portal-core/pp/images/Transcription_label_en_small.jpg"/ alt="image representing transcriptome">
		        <span class="section_heading">
                  <a href="/portal/portal/PathPort/ADB/ADB?action=a&windowstate=normal&c=hrds">View our Directory of Host Response Data Sets</a>
                </span>
            </span>
      </span>
   <!--           
            <span class="genome_data_area">
		    <a href="Genome?filter=host">
			    <span class="host_data_ome_area"> 
			        <img src="/portal-core/pp/images/Quarter_of_Arthrobacter_arilaitensis_Re117_genome_small.png"/ alt="image representing genome">
			        <span class="host_data_ome_content">
				        <span class="triangle_data_ome_heading">Genome<br/></span>
				        Access our collection of Host-related Genome resources.<br/>
					</span>
			    </span>
            </a>
            </span>
            
			<a href="Transcriptome?filter=host">
			    <span class="host_data_ome_area"> 
			        <img src="/portal-core/pp/images/Transcription_label_en_small.jpg"/ alt="image representing transcriptome">
			        <span class="host_data_ome_content">
			        <span class="triangle_data_ome_heading">Transcriptome<br/></span> 
			        Access our collection of Host-related Transcriptome resources.<br/>
					</span>
			    </span>
			</a>
			<a href="Proteome?filter=host">
			    <span class="host_data_ome_area"> 
			        <img src="/portal-core/pp/images/PDB_1tlt_EBI_small.jpg"/ alt="image representing proteome">
			        <span class="host_data_ome_content">
			        	<span class="triangle_data_ome_heading">Proteome</span> <br/>
			        	Access our collection of Host-related Proteome resources.<br/>
					</span>
			    </span>
            </a>
			</span>
		</span>
-->
		<span id="host_analyze_area" class="triangle_data_area">
		<span class="triangle_analyze_heading page_subtitle">Analyze</span>
			<span id="analyze_rnaseq_region">
	<span class="page_subtitle">
	<a href="http://rnaseq.pathogenportal.org">RNA-Seq Pipeline</a><br/>
	</span>
	    <span id="analyze_rnaseq_content_area" class="analyze_content_area">
<dl>
    <dt>
    The <a href="http://rnaseq.pathogenportal.org">RNA-Seq Pipeline</a> based on 
    the Galaxy platform created at Penn State and Emory University and modified by VBI's Pathogen Portal Team, allows you to:<br/>
       <dd>
           -Align your Illumina fastQ reads <strong>(gzipped fastQ files accepted)</strong> against any sequenced genome from<br/> 
                <a href="http://www.eupathdb.org/">EuPathDB</a>,<br/> 
                <a href="http://www.patricbrc.org/">PATRIC</a>,<br/> 
                and <a href="http://www.vectorbase.org/">VectorBase</a>.<br/> 
      </dd>
      <br/>
      <dd>
       -Align your Illumina fastQ reads <strong>(gzipped fastQ files accepted)</strong> against reference genomes for<br/>
                <strong><em>Homo sapiens</em></strong><br/>
                <strong><em>Mus musculus</em></strong><br/>
                <strong><em>Rattus norvegicus</em></strong><br/>
      </dd>
      <br/>
      <dd>
            -View a <a href="http://rnaseq.pathogenportal.org/static/organisms.html">list of supported genomes.</a><br />
      </dd>
    </dt>
    <br/>
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

		</span>
	</span>
 </span>