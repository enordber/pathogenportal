package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import edu.vt.vbi.ci.pathport.Experiment;
import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PathPortData;
import edu.vt.vbi.ci.util.HandyConstants;
import edu.vt.vbi.ci.util.PathPortUtilities;
import edu.vt.vbi.ci.util.applet.AppletUtilities;
import edu.vt.vbi.ci.util.parse.XMLTreeElement;

/**
 * This is the portlet for everything that doesn't fit nicely somewhere else.
 *  Host Response Data Sets
 *  Mouse Model Strain Selection Guide
 *  Related NIAID-Funded Resources
 *
 * Legacy data:
 * 	PFGRC Software
 * 	PRGRC Array Designs
 * 	PFGRC Microarray Protocols
 * 	PRC Experiment Data
 *  
 */
public class PPADBMain extends GenericPortlet {
	private static final String ADB_JSP = "/WEB-INF/jsp/adb.jsp";
	private static final String ADB_EXP_DETAIL_JSP = "/WEB-INF/jsp/adb_exp_details.jsp";
	private static final String SUMMARY_TABLE = "summary_table";
	private static final String CONTENT_PARAM = "c";
	private static final String PERSON = "Person";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String ORGANIZATION = "ORGANIZATION";
	private static final String EMAIL = "EMAIL";
	private static final String PUBLICATION = "Publication";
	private static final String TITLE = "TITLE";
	private static final String AUTHORS = "AUTHORS";
	private static final String PUBMED_ID = "PUBMED_ID";
	private static final String DATE = "DATE";

	private static final int ID_INDEX = 			0;
	private static final int TITLE_INDEX =	 		1;
	private static final int ORGANISMS_INDEX = 		2;
	private static final int TYPE_INDEX = 			3;
	private static final int SAMPLE_COUNT_INDEX = 	4;
	private static final int CITE_INDEX = 			5;
	private static final int DATE_INDEX = 			6;
	private static final String TAXONID = "taxonid";
	private static final String ORGANISM = "organism";
	private static final String EXPID = "expid";
	private static final String EXPTYPE = "exptype";
	private static final String EMPTY = "EMPTY";
	private static final String CLONE = "Clone";
	private static final String MASS_SPEC = "Mass spectrometry";
	private static final String MICROARRAY = "Microarray";
	private static final String PPI = "Protein interaction";
	private static final String STRUCTURE = "Structure";
	private static final String SORTBY = "sortby";
	private static final String CONTENT_ATT = "content";
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	private static final String DATA_SET_SUMMARY_PAGE_NAME = "Data Set Summary";

	private static final String PUBMED_BASE_URL = 
		"http://www.ncbi.nlm.nih.gov/sites/entrez?Db=Pubmed&term=";

	private String jspToUse = ADB_JSP;

	private Experiment[] prcDataSets;
	private Experiment[] hrDataSets;

	private HashMap<String, String> paramToSourceName = new HashMap<String,String>()
	{
		{
			put("mmg","Mouse Model Strain Selection Guide");
			put("pfgrcs","PFGRC Software");
			put("pfgrcad","PFGRC Array Designs");
			put("pfgrcmp","PFGRC Microarray Protocols");
			put("rnfr","Related NIAID-Funded Resources");
			put("hrds","Host Response Data Sets");
			put("prct", "PRC Data Set Table");
			put("prc", "PRC Data Sets");
		}
	}; 


	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		response.setContentType(CONTENT_TYPE);

		String currentPage = PathPortUtilities.getPageName(request);
		if(currentPage.equals(DATA_SET_SUMMARY_PAGE_NAME)) {
			loadPRCTable(request);
		} else {
			jspToUse= loadContent(request, response);
		}
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(jspToUse);
		reqDispatcher.include(request, response);

		//reset jspToUse to default
		jspToUse = ADB_JSP;
	}

	/*
	 * Actually just loads a refresh to the current location for the PRC table
	 */
	private void loadPRCTable(RenderRequest request)  {
		
		String content = "" +
     		"<HTML>" +
		"<HEAD>" +
		"<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;URL=/portal/portal/PathPort/ADB/ADB?action=2&&c=prc\">" +
		"</HEAD>" +
		"<BODY>" +
		"</BODY>" +
		"</HTML>";

		request.setAttribute("content", content);
	}
	/**
	 * Loads the appropriate data in the RenderRequest. Returns name of the jsp
	 * to be used to render.
	 */
	private String loadContent(RenderRequest request, RenderResponse response) {
		String r = ADB_JSP;
		String contentParam = request.getParameter(CONTENT_PARAM);
		String sourceName = paramToSourceName.get(contentParam);

		if(sourceName != null){
			String content = "no content";
			if(sourceName.equals("PRC Data Sets")) {
				String id = request.getParameter(EXPID);
				if(id == null || id.equalsIgnoreCase("all")) {
					String sortBy = request.getParameter(SORTBY);
					String ascParam = request.getParameter("asc");
					boolean ascending = ascParam != null && 
					ascParam.equalsIgnoreCase("true");
					content = createExperimentSummaryTable(getPRCDataSets(), response.createRenderURL(), sortBy, ascending);
				} else {
					r = ADB_EXP_DETAIL_JSP;
					loadExperimentData(id, request);
				}
			} else if(sourceName.equals("Host Response Data Sets")) {
				String id = request.getParameter(EXPID);
				if(id == null || id.equalsIgnoreCase("all")) {
					String sortBy = request.getParameter(SORTBY);
					String ascParam = request.getParameter("asc");
					boolean ascending = ascParam != null && 
					ascParam.equalsIgnoreCase("true");
					content = createHRExperimentSummaryTable(getHRDataSets(), response.createRenderURL(), sortBy, ascending);
				} else {
					r = ADB_EXP_DETAIL_JSP;
					loadExperimentData(id, request);
				}
			} else {
				PPDataSet[] dataSets = PathPortData.getDataFromSource(sourceName);
				if(dataSets != null && dataSets.length > 0) {
					content = dataSets[0].getHTML();
				} else {
					content = getDefaultContent();
				}
			}
			request.setAttribute(CONTENT_ATT, content);
		}
		return r;
	}
	
	private Experiment[] getHRDataSets() {
		if(hrDataSets == null) {
			PPDataSet[] ds = PathPortData.getDataFromSource("Host Response Data Sets");
			if(ds != null) {
				hrDataSets = new Experiment[ds.length];
				for(int i = 0; i < hrDataSets.length; i++) {
					hrDataSets[i] = (Experiment)ds[i];
				}
			}
		}
		return hrDataSets;
	}
	
	private String createExperimentSummaryTable(Experiment[] experiments, 
			PortletURL renderURL, String sortBy, boolean ascending) {
		String r = null;
		renderURL.setParameter("c", "prc");

		String orgAsc = "true";
		String typeAsc = "true";
		String dateAsc = "true";

		Comparator comp = null;
		if(sortBy == null || sortBy.equals("null") || sortBy.equalsIgnoreCase(DATE)) {
			comp = new Experiment.ExperimentDateComparator(ascending);
			if(ascending) {
				dateAsc = "false";
			}
		} else if(sortBy.equalsIgnoreCase(ORGANISM)) {
			comp = new Experiment.ExperimentOrganismComparator(ascending);
			if(ascending) {
				orgAsc = "false";
			}
		} else if(sortBy.equalsIgnoreCase(EXPTYPE)) {
			comp = new Experiment.ExperimentDataTypeComparator(ascending);
			if(ascending) {
				typeAsc = "false";
			}
		}

		Arrays.sort(experiments, comp);
		StringBuffer tableBuffer = new StringBuffer();
		String[] colWidths = new String[]{
				"width=\"0%\"",
				"width=\"45%\"",
				"width=\"17%\"",
				"width=\"10%\"",
				"width=\"5%\"",
				"width=\"8%\"",
				"width=\"10%\""
		};

		tableBuffer.append("<span class=\"pp_table_wrapper\">");
		tableBuffer.append("<table class=\"data_set_summary_table\">");

		StringBuffer headerBuffer = new StringBuffer();
		headerBuffer.append("<tr class=\"data_set_summary_table\" style=\"font-weight:bold\">");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[TITLE_INDEX] +">Title</td>");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[ORGANISMS_INDEX] +
				"><a href=\"" + renderURL + "&sortby=" + ORGANISM
				+"&asc=" + orgAsc + "\">Organism</a></td>");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[TYPE_INDEX] +
				"><a href=\"" + renderURL + "&sortby=" + EXPTYPE
				+"&asc=" + typeAsc + "\">Data Type</a></td>");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[SAMPLE_COUNT_INDEX] +">Samples</td>");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[CITE_INDEX] +">Citation</td>");
		headerBuffer.append("<td class=\"data_set_summary_table\" " + colWidths[DATE_INDEX] +
				"><a href=\"" + renderURL + "&sortby=" + DATE
				+"&asc=" + dateAsc + "\">Date</a></td>");
		headerBuffer.append("</tr>");

		tableBuffer.append(headerBuffer.toString());
		for(int i = 0; i < experiments.length; i++) {
			String[] rowData = getRowData(experiments[i]);

			//create table row for this data
			StringBuffer rowBuffer = new StringBuffer();
			renderURL.setParameter(EXPID, rowData[ID_INDEX]);
			
			//wrap title with href linking to detail page
			rowData[1] = "<a href=\"" + renderURL.toString() + "\">"
			+ rowData[1] + "</a>";

			//wrap citation with href linking to pubmed
			if(rowData[CITE_INDEX] == null || rowData[CITE_INDEX].length() == 0 
					||rowData[CITE_INDEX].equalsIgnoreCase("null")) {
				rowData[CITE_INDEX] = "NA";
			} else {
				rowData[CITE_INDEX] = "<a href=\""+ PUBMED_BASE_URL + rowData[CITE_INDEX] +
				"\">" + rowData[CITE_INDEX] + "</a>";
			}

			//reformat date 
			if(rowData[DATE_INDEX] != null && rowData[DATE_INDEX].length() >0) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(rowData[DATE_INDEX]);
					rowData[DATE_INDEX] = sdf.format(date);
				} catch (ParseException e) {
					System.out.println("Parse exception trying to parse date: "
							+ rowData[DATE_INDEX]);
				}
			}
			
			for(int j = 1; j < rowData.length; j++) {
				rowBuffer.append("<td class=\"data_set_summary_table\"" + colWidths[j] + ">");
				rowBuffer.append(rowData[j]);
				rowBuffer.append("</td>");
			}
			rowBuffer.append("</tr>");
			tableBuffer.append(rowBuffer.toString());
		}
		tableBuffer.append("</table>");
		tableBuffer.append("</span>");

		r = tableBuffer.toString();
		return r;
	}

	private String createHRExperimentSummaryTable(Experiment[] experiments, 
			PortletURL renderURL, String sortBy, boolean ascending) {
		String r = null;
		renderURL.setParameter("c", "hrds");

		String orgAsc = "true";
		String typeAsc = "true";
		String dateAsc = "true";

		Comparator comp = null;
		if(sortBy == null || sortBy.equals("null") || sortBy.equalsIgnoreCase(DATE)) {
			comp = new Experiment.ExperimentDateComparator(ascending);
			if(ascending) {
				dateAsc = "false";
			}
		} else if(sortBy.equalsIgnoreCase(ORGANISM)) {
			comp = new Experiment.ExperimentOrganismComparator(ascending);
			if(ascending) {
				orgAsc = "false";
			}
		} else if(sortBy.equalsIgnoreCase(EXPTYPE)) {
			comp = new Experiment.ExperimentDataTypeComparator(ascending);
			if(ascending) {
				typeAsc = "false";
			}
		}

		StringBuffer tableBuffer = new StringBuffer();
		tableBuffer.append("<span class=\"pp_table_wrapper\">");
		tableBuffer.append("<table>");

		StringBuffer headerBuffer = new StringBuffer();
		headerBuffer.append("<tr>");
		headerBuffer.append("<th id=\"hr_title_th\">Title</th>");
		headerBuffer.append("<th>Description</th>");
        headerBuffer.append("</tr>");
        
		String id;
		String title;
		String sourceLink;
		String summary;
		String contactPerson;
		String contactEmail;
		String contactOrg;
		String cite;
		
		tableBuffer.append(headerBuffer.toString());
		for(int i = 0; i < experiments.length; i++) {
			id = experiments[i].getId();
			title = experiments[i].getTitle();
			sourceLink = experiments[i].getSourceLink();
			summary = experiments[i].getSummary();
			cite = experiments[i].getCite();
			
			StringBuffer rowBuffer = new StringBuffer();
			rowBuffer.append("<tr>");
			rowBuffer.append("<td>");
			rowBuffer.append("<a href=\"");
			rowBuffer.append(sourceLink);
			rowBuffer.append("\">");
			rowBuffer.append(title);
			rowBuffer.append("</a>");
			rowBuffer.append("");
			rowBuffer.append("</td>");
			rowBuffer.append("<td>");
			rowBuffer.append(summary);
			rowBuffer.append("</td>");
			rowBuffer.append("");
			rowBuffer.append("</tr>");
			
			tableBuffer.append(rowBuffer.toString());
		}
		tableBuffer.append("</table>");
		tableBuffer.append("</span>");

		r = tableBuffer.toString();
		return r;
	}

	
	private String[] getRowData(Experiment experiment) {
		String[] r = new String[]{
				experiment.getId(),
				experiment.getTitle(),
				experiment.getOrganisms(),
				experiment.getType(),
				experiment.getSampleCount(),
				experiment.getCite(),
				experiment.getDate()
		};

		return r;
	}


	private Experiment[] getPRCDataSets() {
		if(prcDataSets == null) {
			PPDataSet[] ds = PathPortData.getDataFromSource("PRC Data Sets");
			if(ds != null) {
				prcDataSets = new Experiment[ds.length];
				for(int i = 0; i < prcDataSets.length; i++) {
					prcDataSets[i] = (Experiment)ds[i];
				}
			}
		}
		return prcDataSets;
	}

	/**
	 * Returns whatever should be the page content when there is no other
	 * content to display. Currently, it's just an empty String, but
	 * it might be good to have something else for default content. 
	 * @return
	 */
	private String getDefaultContent() {
		String r = "";
		return r;
	}	

	private void loadExperimentData(String experimentId, PortletRequest request) {
		//make sure data sets are loaded
		getPRCDataSets();

		//get experiment for the given id. 
		Experiment experiment = null;
		String r = "No data available for experiment id " + experimentId;

		for(int i = 0; experiment == null && i < prcDataSets.length; i++) {
			if(prcDataSets[i].getId().equalsIgnoreCase(experimentId)) {
				experiment = prcDataSets[i];
			}
		}
		
		if(experiment == null) {
			//check host response data sets
			getHRDataSets();
			for(int i = 0; experiment == null && i < hrDataSets.length; i++) {
				if(hrDataSets[i].getId().equalsIgnoreCase(experimentId)){
					experiment = hrDataSets[i];
				}
			}
		}

		if(experiment != null) {
			System.out.println("found experiment for id " + experimentId);
		}

		if(experiment == null) {
			System.out.println("no experiment found for id " + experimentId);
		} else {
			//title block
			String title = experiment.getTitle();
			request.setAttribute("title_block", title);
			System.out.println("title: " + title);

			//statistics block 
			String statistics = "No statistics are currently available for this Experiment";
			request.setAttribute("statistics_block", statistics);

			//summary block
			String summary = experiment.getSummary();
			if(summary == null) {
				summary = "No Summary is currently available for this Experiment";
			}
			request.setAttribute("summary_block", summary);
			System.out.println("summary: " + summary);

			//date block
			String createDate = experiment.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'hh:mm:ss");
			try {
				sdf.setLenient(true);
				Date d = sdf.parse(createDate);
				sdf.applyPattern("yyyy-MM-dd");
				createDate = sdf.format(d);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(createDate == null) {
				createDate = "";
			}

			String experimentDate = null;
			if(experimentDate == null) {
				experimentDate = "";
			}

			String modifiedDate = null;
			if(modifiedDate == null) {
				modifiedDate = "";
			}			

			request.setAttribute("dates_block", createDate);

			//people block
			String contactPerson = experiment.getContactPerson();
			String contactEmail = experiment.getContactEmail();
			String contactOrganization = experiment.getContactOrganization();

			StringBuffer personBuffer = new StringBuffer();
			personBuffer.append("<p>");
			personBuffer.append("<table>");
			personBuffer.append("<tr>");
			personBuffer.append("<td class=\"data_set_subhead\">");
			personBuffer.append("Name: ");
			personBuffer.append("</td>");
			personBuffer.append("<td>");
			personBuffer.append(contactPerson);
			personBuffer.append("</td>");
			personBuffer.append("</tr>");
			personBuffer.append("<tr>");
			personBuffer.append("<td class=\"data_set_subhead\">");
			personBuffer.append("Organization: ");
			personBuffer.append("</td>");
			personBuffer.append("<td>");
			personBuffer.append(contactOrganization);
			personBuffer.append("</td>");
			personBuffer.append("</tr>");
			personBuffer.append("<tr>");
			personBuffer.append("<td class=\"data_set_subhead\">");
			personBuffer.append("Email: ");
			personBuffer.append("</td>");
			personBuffer.append("<td>");
			personBuffer.append(contactEmail);
			personBuffer.append("</td>");
			personBuffer.append("</tr>");
			personBuffer.append("</table>");
			personBuffer.append("</p>");
			//			}

			request.setAttribute("person_block", personBuffer.toString());


			//publication block
			XMLTreeElement[] pubElements = experiment.getElement().getChildrenOfType(PUBLICATION);
			StringBuffer pubBuffer = new StringBuffer();
			for(int i = 0; i < pubElements.length; i++) {
				String pubTitle = (String) pubElements[i].getAttribute(TITLE);
				String authors = (String) pubElements[i].getAttribute(AUTHORS);
				String pmid = (String) pubElements[i].getAttribute(PUBMED_ID);
				String pubDate = (String) pubElements[i].getAttribute(DATE);

				pubBuffer.append("<p>");
				pubBuffer.append("<table>");
				pubBuffer.append("<tr>");
				pubBuffer.append("<td>");
				pubBuffer.append("Title: ");
				pubBuffer.append("</td>");
				pubBuffer.append("<td>");
				pubBuffer.append(pubTitle);
				pubBuffer.append("</td>");
				pubBuffer.append("</tr>");

				pubBuffer.append("<tr>");
				pubBuffer.append("<td>");
				pubBuffer.append("Authors: ");
				pubBuffer.append("</td>");
				pubBuffer.append("<td>");
				pubBuffer.append(authors);
				pubBuffer.append("</td>");
				pubBuffer.append("</tr>");

				pubBuffer.append("<tr>");
				pubBuffer.append("<td>");
				pubBuffer.append("Date: ");
				pubBuffer.append("</td>");
				pubBuffer.append("<td>");
				pubBuffer.append(pubDate);
				pubBuffer.append("</td>");
				pubBuffer.append("</tr>");

				pubBuffer.append("<tr>");
				pubBuffer.append("<td>");
				pubBuffer.append("PMID: ");
				pubBuffer.append("</td>");
				pubBuffer.append("<td>");
				pubBuffer.append("<a href=\"" + 
						PUBMED_BASE_URL + pmid + "\">" + pmid + "</a>");
				pubBuffer.append("</td>");
				pubBuffer.append("</tr>");

				pubBuffer.append("</table>");
				pubBuffer.append("</p>");
			}

			request.setAttribute("publication_block", pubBuffer.toString());

			//Data file block
			String fileURL = experiment.getFileURL();
			StringBuffer fileBlock = new StringBuffer();
			if(fileURL == null) {
				fileBlock.append("There are no data files available for this Experiment.");
			} else {
				fileBlock.append("<a href=\"");
				fileBlock.append(fileURL);
				fileBlock.append("\">Download Data File(s)</a>");
				String fileURLContents = AppletUtilities.getContentsOfURL(fileURL);
				int linesInDownloadPage = fileURLContents.split("\n").length;
				fileBlock.append("<iframe src=\"");
				fileBlock.append(fileURL);
				fileBlock.append("\" width=\"100%\" height=\"");
				fileBlock.append("" + linesInDownloadPage*25 + "px\">");
				fileBlock.append("</iframe>");
			}
			request.setAttribute("data_file_block", fileBlock.toString());

		}
	}
}
