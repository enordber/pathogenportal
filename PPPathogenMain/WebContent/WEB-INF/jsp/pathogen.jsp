<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<span id="triangle_sidebar_region">
<span id="dt_image_area">
    <img
	src="/portal-core/themes/pathport/images/pathogen_triangle.png"
	alt="Disease Triangle emphasizing pathogen" usemap="#pathogendtmap" />
	<br/>
    <span id="pathogen_triangle_links"> 
        <img id="explore_arrow" src="/portal-core/themes/pathport/images/small_right_arrow.png" /> 
        <span id="mini_nav_triangle_links_title">Continue Exploring<br/></span>
    	<span class="triangle_link"><a href="/portal/portal/PathPort/Host">Host<br/></a></span>
	    <span class="triangle_link"><a href="/portal/portal/PathPort/Pathogen">Pathogen<br/></a></span>
	    <span class="triangle_link"><a href="/portal/portal/PathPort/Environment">Environment/Vector<br/></a></span>
    </span>
</span>
</span>

<span id="pathogen_science_region" class="triangle_science_region">
    	<span class="page_main_title">Pathogen</span>
	<span id="pathogen_intro_blurb"> 
<!-- 	Analyze pathogen expression data with our <a href="http://rnaseq.pathogenportal.org">RNA-Seq Pipeline</a> -->
    </span>

	<span id="pathogen_data_analyze_region" class="triangle_data_analyze_region">
		<span id="pathogen_data_area" class="triangle_data_area">
		<span class="triangle_data_heading page_subtitle">Data<br/></span>
			<span id="pathogen_data_content" class="triangle_data_content">
		<span class="triangle_data_blurb_area"></span>
		
		<span class="genome_data_area">
		    <a href="/portal/portal/PathPort/Genome/GenomeMainWindow?action=a&windowstate=normal&op=filter&facet_values=BRC--PATRIC%2CBRC--EuPathDB%2CBRC--ViPR">
			    <span class="data_ome_area"> 
			        <img src="/portal-core/pp/images/Quarter_of_Arthrobacter_arilaitensis_Re117_genome_small.png"/ alt="image representing genome">
			        <span class="data_ome_content">
				        <span class="triangle_data_ome_heading">Genome<br/></span>
				        Access our collection of Pathogen-related Genome resources.<br/>
					</span>
			    </span>
            </a>
        </span>
<%if(1==2) {%>            
			<a href="Transcriptome?filter=pathogen">
			    <span class="data_ome_area"> 
			        <img src="/portal-core/pp/images/Transcription_label_en_small.jpg"/ alt="image representing transcriptome">
			        <span class="data_ome_content">
			        <span class="triangle_data_ome_heading">Transcriptome<br/></span>
			        Access our collection of Pathogen-related Transcriptome resources.<br/>
					</span>
			    </span>
			</a>
			<a href="Proteome?filter=pathogen">
			    <span class="data_ome_area"> 
			        <img src="/portal-core/pp/images/PDB_1tlt_EBI_small.jpg"/ alt="image representing proteome">
			        <span class="data_ome_content">
			        	<span class="triangle_data_ome_heading">Proteome<br/></span>
			        	Access our collection of Pathogen-related Proteome resources.<br/>
					</span>
			    </span>
            </a>
<%} %>
			</span>
		</span>

		<span id="pathogen_analyze_area" class="triangle_data_area">
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

		
	</span>