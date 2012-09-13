package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;

public class PPDataMain extends GenericPortlet{
	private static final String DATA_JSP = "/WEB-INF/jsp/data.jsp";
	
	private static final String PFGRC_SOFTWARE = "PFGRC Software";
	private static final String PFGRC_ARRAY_DESIGNS = "PFGRC Array Designs";
	private static final String PFGRC_MA_PROTOCOLS = "PFGRC Microarray Protocols";
	private static final String PRC_DATA = "PRC Data Sets";
	
	private String pfgrc_software;
	private String pfgrc_array_designs;
	private String pfgrc_ma_protocols;
	private String prc_data;

	public PPDataMain() {
		System.out.println("PPDataMain constructor");
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		// set return content type
		response.setContentType("text/html;charset=utf-8");

		int genomeDataSetCount = getDataSetCount("genome");
		request.setAttribute("genome_count", genomeDataSetCount);
		int hostGenomeCount = getDataSetCount(new String[]{"genome", "host"});
		request.setAttribute("host_genome_count", hostGenomeCount);
		int pathogenGenomeCount = getDataSetCount(new String[]{"genome", "pathogen"});
		request.setAttribute("pathogen_genome_count", pathogenGenomeCount);
		int vectorGenomeCount = getDataSetCount(new String[]{"genome", "vector"});
		request.setAttribute("vector_genome_count", vectorGenomeCount);
		
		int transcriptomeDataSetCount = getDataSetCount("transcriptome");
		request.setAttribute("transcriptome_count", transcriptomeDataSetCount);
		int hostTranscriptomeCount = getDataSetCount(new String[]{"transcriptome", "host"});
		request.setAttribute("host_transcriptome_count", hostTranscriptomeCount);
		int pathogenTranscriptomeCount = getDataSetCount(new String[]{"transcriptome", "pathogen"});
		request.setAttribute("pathogen_transcriptome_count", pathogenTranscriptomeCount);
		int vectorTranscriptomeCount = getDataSetCount(new String[]{"transcriptome", "vector"});
		request.setAttribute("vector_transcriptome_count", vectorTranscriptomeCount);
		
		
		int proteomeDataSetCount = getDataSetCount("proteome");
		request.setAttribute("proteome_count", proteomeDataSetCount);
		int hostProteomeCount = getDataSetCount(new String[]{"proteome", "host"});
		request.setAttribute("host_proteome_count", hostProteomeCount);
		int pathogenProteomeCount = getDataSetCount(new String[]{"proteome", "pathogen"});
		request.setAttribute("pathogen_proteome_count", pathogenProteomeCount);
		int vectorProteomeCount = getDataSetCount(new String[]{"proteome", "vector"});
		request.setAttribute("vector_proteome_count", vectorProteomeCount);
		
		//add legacy data content
		pullInData();
		request.setAttribute("pfgrc_software", pfgrc_software);
		request.setAttribute("pfgrc_array_designs", pfgrc_array_designs);
		request.setAttribute("pfgrc_ma_protocols", pfgrc_ma_protocols);
		request.setAttribute("prc_data", prc_data);
		
		
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(DATA_JSP);
		reqDispatcher.include(request, response);
	}

	private int getDataSetCount(String tag) {
		int r = 0;
		PPDataSet[] dataSets = PathPortData.getDataWithTag(tag);
		if(dataSets != null) {
			r = dataSets.length;
//			System.out.println("PPDataMain.getDataSetCount() for tag: " + tag);
//
//			System.out.println("data sets: " + dataSets.length);
//			for(int i = 0; i < dataSets.length; i++) {
//				System.out.println(dataSets[i].getHTML());
//				System.out.println(dataSets[i].toString());
//				System.out.println(dataSets[i].getFacetJSON());
//			}
		}
		return r;
	}

	private int getDataSetCount(String[] tags) {
		int r = 0;
		PPDataSet[] dataSets = PathPortData.getDataWithAllTags(tags);
		if(dataSets != null) {
			r = dataSets.length;
			
//			System.out.println("PPDataMain.getDataSetCount() for tags ");
//			for(int i = 0; i < tags.length; i++) {
//				System.out.println(tags[i]);
//			}
//			System.out.println("data sets: " + dataSets.length);
//			for(int i = 0; i < dataSets.length; i++) {
//				System.out.println(dataSets[i].getHTML());
//			}
		}
		return r;
	}
	
	private void pullInData() {
//		System.out.println("PPDataMain.pullInData()");
		PPDataSet[] dataSets = PathPortData.getDataOfType(PathPortData.GOOGLE_SITES);
//		System.out.println("google sites datasets: " + dataSets.length);
		for(int i = 0; i < dataSets.length; i++) {
			PPDataSet dataSet = dataSets[i];
			String sourceName = dataSet.getDataSource().getName();
//			System.out.println("dataSet "+ i + ": " + sourceName);
			if(sourceName.equalsIgnoreCase(PFGRC_SOFTWARE)) {
				pfgrc_software = dataSet.getHTML();
			} else if(sourceName.equalsIgnoreCase(PFGRC_ARRAY_DESIGNS)) {
				pfgrc_array_designs = dataSet.getHTML();
			} else if(sourceName.equalsIgnoreCase(PFGRC_MA_PROTOCOLS)) {
				pfgrc_ma_protocols = dataSet.getHTML();
			} else if(sourceName.equalsIgnoreCase(PRC_DATA)) {
				prc_data = dataSet.getHTML();
			}
		}
	}

}
