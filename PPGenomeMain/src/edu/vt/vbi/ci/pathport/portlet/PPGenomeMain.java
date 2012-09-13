package edu.vt.vbi.ci.pathport.portlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import edu.vt.vbi.ci.pathport.ExtendedBitSet;
import edu.vt.vbi.ci.pathport.FacetCutter;
import edu.vt.vbi.ci.pathport.PPDataSet;
import edu.vt.vbi.ci.pathport.PPGenomeInfo;
import edu.vt.vbi.ci.pathport.PathPortData;
import edu.vt.vbi.ci.util.ReverseComparator;

public class PPGenomeMain extends GenericPortlet{
	private static final String GENOME_JSP = "/WEB-INF/jsp/genome.jsp";
	private static final String GENOME_CONTENT_JSP = "/WEB-INF/jsp/genome_content.jsp";
	private static final String FILTER = "filter";

	//Attribute constants
	private static final String GENOME_DATA_ATT = "genome_data";
	private static final String FACET_ATT = "facets";
	private static final String SELECTED_COUNT_ATT = "selected_count";

	//Table Column Ids
	private static final String GENOME_NAME_COL = "genome_name_col";
	private static final String GENOME_BRC_COL = "genome_brc_col";
	private static final String GENOME_GSC_COL = "genome_gsc_col";
	private static final String GENOME_TYPE_COL = "genome_type_col";
	private static final String GENOME_TAXON_ID_COL = "genome_tax_col";
	private static final String GENOME_STATUS_COL = "genome_status_col";
	private static final String GENOME_DATE_COL = "genome_date_col";
	private static final String GENOME_LENGTH_COL = "genome_length_col";
	private static final String GENOME_GENE_COL = "genome_gene_col";

	//Sorted css classes
	private static final String SORTED_ASCENDING = "sorted_ascending";
	private static final String SORTED_DESCENDING = "sorted_descending";

	//Parameter values
	private static final String SORT_ASCENDING = "asc";
	private static final String SORT_DESCENDING = "desc";

	private static final String NCBI_TAXONOMY_URL_PREFIX = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=";

	private PPGenomeInfo[] genomeDataSets;
	private HashMap<String, Comparator> columnComparatorMap;

	private HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap;

	/*
	 * To support the alphabetical paging, this maps from the first
	 * letter of the GenomeName to the start and end indices of the 
	 *  PPGenomeInfo objects representing those genomes.
	 */
	private HashMap<String, int[]> genomeNameABToGenomeInfo;

	public PPGenomeMain() {
		columnComparatorMap = new HashMap<String, Comparator>();
		columnComparatorMap.put(GENOME_NAME_COL, new PPGenomeInfo.GenomeNameComparator());
		columnComparatorMap.put(GENOME_BRC_COL, new PPGenomeInfo.BRCComparator());
		columnComparatorMap.put(GENOME_GSC_COL, new PPGenomeInfo.SequencingCenterComparator());
		columnComparatorMap.put(GENOME_TYPE_COL, new PPGenomeInfo.OrganismTypeComparator());
		columnComparatorMap.put(GENOME_STATUS_COL, new PPGenomeInfo.StatusComparator());
		columnComparatorMap.put(GENOME_TAXON_ID_COL, new PPGenomeInfo.TaxonIdComparator());
		columnComparatorMap.put(GENOME_LENGTH_COL, new PPGenomeInfo.LengthComparator());
		columnComparatorMap.put(GENOME_GENE_COL, new PPGenomeInfo.GeneCountComparator());
	}

	public void doView(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(GENOME_JSP);
		loadContent(request);
		reqDispatcher.include(request, response);
	}	

	public void serveResource(ResourceRequest request, ResourceResponse response) 
	throws PortletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PortletRequestDispatcher reqDispatcher = 
			getPortletContext().getRequestDispatcher(GENOME_CONTENT_JSP);
		loadContent(request);
		reqDispatcher.include(request, response);
	}

	private void loadContent(PortletRequest request) {
		//don't really need to load data each time, but doing it now until I can 
		//implement a good way of updating only when necessary.
		loadGenomeData();

		String sortBy = request.getParameter("sortby");

		boolean sortAscending = true;
		String sortOrder = request.getParameter("sortorder");
		if(SORT_DESCENDING.equalsIgnoreCase(sortOrder)) {
			sortAscending = false;
		}

		ExtendedBitSet selectedGenomeIndices = new ExtendedBitSet();
		String[][] selectedFacetNameValuePairs = FacetCutter.getSelectedFacetValues(request);
		if(selectedFacetNameValuePairs == null || selectedFacetNameValuePairs.length == 0) {
			selectedGenomeIndices.set(0, genomeDataSets.length);
			request.setAttribute(FACET_ATT, getFacetHTML(genomeDataSets, fullFacetMap));
		} else {
			HashMap<String, ExtendedBitSet> selectedFacetSets = FacetCutter.getSelectedFacetSets(fullFacetMap, selectedFacetNameValuePairs, genomeDataSets.length);
			ExtendedBitSet[] selectedSets = new ExtendedBitSet[selectedFacetSets.size()];
			selectedFacetSets.values().toArray(selectedSets);
			selectedGenomeIndices = ExtendedBitSet.and(selectedSets);
			HashSet<String> selectedCheckboxIds = getSelectedCheckboxIds(selectedFacetNameValuePairs);
			request.setAttribute(FACET_ATT, getFacetHTML(genomeDataSets, fullFacetMap, selectedFacetSets, selectedCheckboxIds));
		}
		request.setAttribute(GENOME_DATA_ATT, getGenomeTable(selectedGenomeIndices, sortBy, sortAscending));
		request.setAttribute(SELECTED_COUNT_ATT, selectedGenomeIndices.cardinality());
	}

	private String getGenomeTable(ExtendedBitSet selectedGenomesIndices, String sortBy, boolean sortAscending) {
		String r = null;
		int[] selectedIndices = selectedGenomesIndices.getSetValues();

		PPGenomeInfo[] tableGenomes = new PPGenomeInfo[selectedIndices.length];
		for(int i = 0; i < tableGenomes.length; i++) {
			tableGenomes[i] = genomeDataSets[selectedIndices[i]];
		}

		Comparator comp = columnComparatorMap.get(sortBy);

		String sortedClass = SORTED_ASCENDING;
		if(!sortAscending) {
			sortedClass = SORTED_DESCENDING;
			comp = new ReverseComparator(comp);
		}

		if(comp != null) {
			Arrays.sort(tableGenomes, comp);
		}

		boolean showDates = false;
		boolean includeTaxonLinks = true;
		boolean includeBRCLinks = true;
		boolean includeLength = false;
		boolean includeGeneCount = false;

		StringBuffer sb = new StringBuffer();
		sb.append("<span id=\"gsc_genome_table_container\" class=\"pp_table_wrapper full_table\">");
		sb.append("<span class=\"table_header_container\">");
		sb.append("<table id=\"gsc_genome_table_header\">");
		sb.append("<tr>");

		sb.append("<th id=\"" + GENOME_NAME_COL);
		sb.append("\" class=\"gsc_genome_name_col sortable ");
		if(GENOME_NAME_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Genome Name\">");
		sb.append("Genome Name");
		sb.append("</th>");

		sb.append("<th id=\"" + GENOME_BRC_COL);
		sb.append("\" class=\"gsc_genome_brc_col sortable ");
		if(GENOME_BRC_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Bioinformatics Resource Center Name\">");
		sb.append("BRC");
		sb.append("</th>");

		sb.append("<th id=\"" + GENOME_GSC_COL);
		sb.append("\" class=\"gsc_genome_gsc_col sortable ");
		if(GENOME_GSC_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Genome Sequencing Center Name\">");
		sb.append("GSC");
		sb.append("</th>");

		sb.append("<th id=\"" + GENOME_TYPE_COL);
		sb.append("\" class=\"gsc_genome_type_col sortable ");
		if(GENOME_TYPE_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Organism Type\">");
		sb.append("Organism Type");
		sb.append("</th>");

		sb.append("<th id=\"" + GENOME_TAXON_ID_COL);
		sb.append("\" class=\"gsc_genome_tax_col sortable ");
		if(GENOME_TAXON_ID_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Taxon Id\">");
		sb.append("Taxon Id");
		sb.append("</th>");

		sb.append("<th id=\"" + GENOME_STATUS_COL);
		sb.append("\" class=\"gsc_genome_status_col sortable ");
		if(GENOME_STATUS_COL.equals(sortBy)) {
			sb.append(sortedClass);
		} 
		sb.append("\" title=\"Click to sort by Status\">");
		sb.append("Status");
		sb.append("</th>");

		if(showDates) {
			sb.append("<th id=\"" + GENOME_DATE_COL);
			sb.append("\" class=\"gsc_genome_date_col sortable ");
			if(GENOME_DATE_COL.equals(sortBy)) {
				sb.append(sortedClass);
			} 
			sb.append("\">");
			sb.append("Sample Acquisition Date");
			sb.append("</th>");
		}

		if(includeLength) {
			sb.append("<th id=\"" + GENOME_LENGTH_COL);
			sb.append("\" class=\"gsc_genome_length_col sortable ");
			if(GENOME_LENGTH_COL.equals(sortBy)) {
				sb.append(sortedClass);
			} 
			sb.append("\" title=\"Click to sort by Genome Length\">");
			sb.append("Length");
			sb.append("</th>");
		}

		if(includeGeneCount) {
			sb.append("<th id=\"" + GENOME_GENE_COL);
			sb.append("\" class=\"gsc_genome_gene_col sortable ");
			if(GENOME_GENE_COL.equals(sortBy)) {
				sb.append(sortedClass);
			} 
			sb.append("\" title=\"Click to sort by Gene Count\">");
			sb.append("Gene Count");
			sb.append("</th>");
		}

		sb.append("<th class=\"spacer\"></th>");

		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</span>");

		sb.append("<span class=\"table_body_container ajax_waiting_hide_area\">");
		boolean useNew = true;
		if(useNew){
			int blocks = 200;
			int rowsPerBlock = (tableGenomes.length/blocks) +1;
			for(int i = 0; i < blocks; i++){
				int start = i*rowsPerBlock;
				int end = start + rowsPerBlock;
				//				System.out.println("PPGenomeMain.getTableRows() block " + i + " start " + start + " end: " + end);
				sb.append(getTableRows(tableGenomes,start, end));
			}
		} else{
			sb.append("<table id=\"gsc_genome_table_body\">");
			for(int i = 0; i < tableGenomes.length; i++) {
				PPGenomeInfo genome = tableGenomes[i];
				if((i & 1) != 0) {
					//this is an odd row (1 bit is not set)
					//odd rows get an extra css class added, so they can
					//be rendered a bit differently to make it easier
					//to distinguish between rows.
					sb.append("<tr class=\"oddrow\">");
				} else {
					sb.append("<tr>");
				}
				sb.append("<td class=\"gsc_genome_name_col\">");
				sb.append("<a name=\"some_anchor\">");
				sb.append(genome.getGenomeName());
				sb.append("</td>");

				sb.append("<td class=\"gsc_genome_brc_col\">");
				String brcURL = genome.getBRCURL();
				if(includeBRCLinks && brcURL != null && brcURL != PPGenomeInfo.NOT_PROVIDED) {
					sb.append("<a href=\"" + brcURL + "\">");
					sb.append(genome.getBRC());
					sb.append("</a>");
				} else {
					sb.append(genome.getBRC());
				}
				sb.append("</td>");

				sb.append("<td class=\"gsc_genome_gsc_col\">");
				sb.append(genome.getSequencingCenter());
				sb.append("</td>");

				sb.append("<td class=\"gsc_genome_type_col\">");
				sb.append(genome.getOrganismType());
				sb.append("</td>");

				sb.append("<td  class=\"gsc_genome_tax_col\">");
				if(includeTaxonLinks) {
					sb.append("<a href=\"" + NCBI_TAXONOMY_URL_PREFIX + genome.getTaxId() + "\">");
					sb.append(genome.getTaxId());
					sb.append("</a>");
				} else {
					sb.append(genome.getTaxId());
				}
				sb.append("</td>");

				sb.append("<td class=\"gsc_genome_status_col\">");
				sb.append(genome.getStatus());
				sb.append("</td>");

				if(showDates) {
					sb.append("<td class=\"gsc_genome_date_col\">");
					sb.append(genome.getSampleAcquisitionDate());
					sb.append("</td>");
				}

				sb.append("<td class=\"gsc_genome_length_col\">");
				int genomeLength = genome.getLength();
				if(genomeLength > 0) {
					sb.append(genome.getLength());
				} else {
					sb.append("-");
				}
				sb.append("</td>");

				sb.append("<td class=\"gsc_genome_gene_col\">");
				int geneCount = genome.getGeneCount();
				if(geneCount > 0) {
					sb.append(genome.getGeneCount());
				} else {
					sb.append("-");
				}
				sb.append("</td>");

				sb.append("</tr>");
			}
			sb.append("");
			sb.append("</table>");
		}

		sb.append("</span>");
		sb.append("</span>");

		r = sb.toString();
		return r;
	}

	/**
	 * Returns an HTML Table, with only the rows specified.
	 * The provided tableGenomes array must already be properly sorted.
	 * This returns the table with the table rows only, without a table header row.
	 * @param tableGenomes
	 * @param beginIndex the beginning index, inclusive
	 * @param endIndex the ending index, exclusive
	 * @return
	 */
	private String getTableRows(PPGenomeInfo[] tableGenomes, int beginIndex, int endIndex) {
		String r = null;
		boolean includeTaxonLinks = true;
		boolean showDates = false;
		boolean includeBRCLinks = true;
		boolean includeLength = false;
		boolean includeGeneCount = false;

		beginIndex = Math.max(beginIndex, 0);
		endIndex = Math.min(endIndex, tableGenomes.length);
		String previousInitial = null;

		StringBuffer sb = new StringBuffer();
		sb.append("<table id=\"gsc_genome_table_body\">");
		for(int i = beginIndex; i < endIndex; i++) {
			PPGenomeInfo genome = tableGenomes[i];
			String initial = genome.getGenomeName().substring(0, 1);
			
			if((i & 1) != 0) {
				//this is an odd row (1 bit is not set)
				//odd rows get an extra css class added, so they can
				//be rendered a bit differently to make it easier
				//to distinguish between rows.
				sb.append("<tr class=\"oddrow\">");
			} else {
				sb.append("<tr>");
			}
			sb.append("<td class=\"gsc_genome_name_col\">");
			if(!initial.equals(previousInitial)) {
				sb.append("<a name=\"");
				sb.append(initial);
				sb.append("\"></a>");
			}
			sb.append(genome.getGenomeName());
			sb.append("</td>");

			sb.append("<td class=\"gsc_genome_brc_col\">");
			String brcURL = genome.getBRCURL();
			if(includeBRCLinks && brcURL != null && brcURL != PPGenomeInfo.NOT_PROVIDED) {
				sb.append("<a href=\"" + brcURL + "\">");
				sb.append(genome.getBRC());
				sb.append("</a>");
			} else {
				sb.append(genome.getBRC());
			}
			sb.append("</td>");

			sb.append("<td class=\"gsc_genome_gsc_col\">");
			sb.append(genome.getSequencingCenter());
			sb.append("</td>");

			sb.append("<td class=\"gsc_genome_type_col\">");
			sb.append(genome.getOrganismType());
			sb.append("</td>");

			sb.append("<td  class=\"gsc_genome_tax_col\">");
			if(includeTaxonLinks) {
				sb.append("<a href=\"" + NCBI_TAXONOMY_URL_PREFIX + genome.getTaxId() + "\">");
				sb.append(genome.getTaxId());
				sb.append("</a>");
			} else {
				sb.append(genome.getTaxId());
			}
			sb.append("</td>");

			sb.append("<td class=\"gsc_genome_status_col\">");
			sb.append(genome.getStatus());
			sb.append("</td>");

			if(showDates) {
				sb.append("<td class=\"gsc_genome_date_col\">");
				sb.append(genome.getSampleAcquisitionDate());
				sb.append("</td>");
			}

			if(includeLength) {
				sb.append("<td class=\"gsc_genome_length_col\">");
				int genomeLength = genome.getLength();
				if(genomeLength > 0) {
					sb.append(genome.getLength());
				} else {
					sb.append("-");
				}
				sb.append("</td>");
			}

			if(includeGeneCount) {
				sb.append("<td class=\"gsc_genome_gene_col\">");
				int geneCount = genome.getGeneCount();
				if(geneCount > 0) {
					sb.append(genome.getGeneCount());
				} else {
					sb.append("-");
				}
				sb.append("</td>");
			}

			sb.append("</tr>");
		}
		sb.append("");
		sb.append("</table>");

		r = sb.toString();
		return r;
	}

	private HashSet<String> getSelectedCheckboxIds(String[][] selectedFacetNameValuePairs) {
		HashSet<String > r = new HashSet<String>();

		HashMap<String, ExtendedBitSet> selectedFacetSets = new HashMap<String, ExtendedBitSet>();
		//populate selectedFacetSets with facetName keys, and empty BitSets
		String facetName;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			if(!selectedFacetSets.containsKey(facetName)) {
				selectedFacetSets.put(facetName, new ExtendedBitSet());
			}
		}
		//for each facet name-value pair, get the BitSet for the members from the
		//fullFacetMap. OR each of these to the facet BitSet in selectedFacetSets
		String facetValue;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			facetValue = selectedFacetNameValuePairs[i][1];
			ExtendedBitSet facetValueMembers = 
				fullFacetMap.get(facetName).get(facetValue);

			selectedFacetSets.get(facetName).or(facetValueMembers);
			String checkboxId = facetName + "--" + facetValue;
			r.add(checkboxId);
		}

		return r;
	}

	private void loadGenomeData() {
		PPDataSet[] dataSets = PathPortData.getDataWithAllTags(new String[]{"gscid", "genome"});
		if(dataSets != null) {
			genomeDataSets = new PPGenomeInfo[dataSets.length];
			for(int i = 0; i < genomeDataSets.length; i++) {
				genomeDataSets[i] = (PPGenomeInfo) dataSets[i];
			}
			Arrays.sort(genomeDataSets, new PPGenomeInfo.GenomeNameComparator());
			fullFacetMap = FacetCutter.getFacetMap(genomeDataSets);
			
			//prepare for alphabetical paging
			genomeNameABToGenomeInfo = new HashMap<String, int[]>();
			String previousLetter = "A";
			int[] startEnd = new int[]{0,0};
			
			for(int i = 0; i < genomeDataSets.length; i++) {
				String currentLetter = genomeDataSets[i].getGenomeName().substring(0, 1);
			}
		}
	}

	private static String getFacetHTML(PPDataSet[] dataSets, HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap, HashMap<String, ExtendedBitSet> selectedFacetSets, HashSet<String> selectedCheckboxIds) {
		//		System.out.println("PPGenomeMain.getFacetHTML()");
		String r;
		StringBuffer sb = new StringBuffer();
		String[] facetNames = new String[fullFacetMap.size()];
		fullFacetMap.keySet().toArray(facetNames);
		Arrays.sort(facetNames, new FacetCutter.FacetValueComparator(fullFacetMap));
		ExtendedBitSet[] facetSets = new ExtendedBitSet[facetNames.length];
		for(int i = 0; i < facetNames.length; i++) {
			facetSets[i] = selectedFacetSets.get(facetNames[i]);
			if(facetSets[i] == null) {
				//this happens when no values for this facet are selected, 
				//so the facetName is not in the request parameter, so no
				//BitSet entry is created for the facet. The convention with 
				//faceted search is that no selected values means the same as 
				//every value for the facet being selected (I'm not making that
				// up. That's really the convention). So we will follow that
				//convention here and create a BitSet representing all items
				//for this facet
				facetSets[i] = new ExtendedBitSet();
				facetSets[i].set(0, dataSets.length);

				//the alternative way to handle no selections is to really treat
				//it as no selections, and show no resulting items. This would
				//be accomplished by removing the line above that sets the bits
				//in facetSets[i].
			}
		}

		for(int i = 0; i < facetNames.length; i++) {
			HashMap<String, ExtendedBitSet> facetValueMap = fullFacetMap.get(facetNames[i]);
			String[] facetValues = new String[facetValueMap.size()];
			facetValueMap.keySet().toArray(facetValues);
			Arrays.sort(facetValues);
			sb.append("<span class=\"facet\">");
			sb.append("<span class=\"facet_name\">");
			sb.append(facetNames[i]);
			sb.append("<br/>");
			sb.append("</span>");
			sb.append("<span class=\"facet_values\">");
			for(int j = 0; j < facetValues.length; j++) {
				String facetNameValueId = facetNames[i] + "--" + facetValues[j];
				//determine the number of additional items that would be selected 
				//by selecting this facetValue. This is determined by counting the
				//number of set bits for this facetValue that are set by 
				//all of the other combined facet selection sets
				ExtendedBitSet facetValueSet = facetValueMap.get(facetValues[j]);
				ExtendedBitSet setByOtherFacets = new ExtendedBitSet();
				setByOtherFacets.set(0, dataSets.length);
				for(int k = 0; k < facetNames.length; k++) {
					if(k != i) { //don't check versus the same facet that this 
						//facet value belongs to
						setByOtherFacets.and(facetSets[k]);
					}
				}
				facetValueSet = facetValueSet.getAnd(setByOtherFacets);

				sb.append("<span class=\"facet_value\">");
				sb.append("<label>");
				sb.append("<input type=\"checkbox\" id=\""); 
				sb.append(facetNameValueId);
				sb.append("\"");
				sb.append(" class=\"facet_filter_checkbox\""); 
				if(selectedCheckboxIds.contains(facetNameValueId)) {
					sb.append(" checked=\"checked\"");
				} 
				sb.append(" />");
				sb.append(facetValues[j]);
				sb.append("<span class=\"facet_value_count\">");
				sb.append("(");
				sb.append(facetValueSet.cardinality());
				sb.append(")");
				sb.append("<br/>");
				sb.append("</span>");
				sb.append("</label>");
				sb.append("</span>");
			}
			sb.append("</span>"); //close facet_values
			sb.append("<br/>");
			sb.append("</span>"); //close facet
		}
		r = sb.toString();
		return r;
	}

	private static String getFacetHTML(PPDataSet[] newsDataSets, HashMap<String, HashMap<String, ExtendedBitSet>> fullFacetMap) {
		//		System.out.println("PPGenomeMain.getFacetHTML()");
		String r;
		StringBuffer sb = new StringBuffer();
		String[] facetNames = new String[fullFacetMap.size()];
		fullFacetMap.keySet().toArray(facetNames);
		Arrays.sort(facetNames, new FacetCutter.FacetValueComparator(fullFacetMap));
		for(int i = 0; i < facetNames.length; i++) {
			HashMap<String, ExtendedBitSet> facetValueMap = fullFacetMap.get(facetNames[i]);
			String[] facetValues = new String[facetValueMap.size()];
			facetValueMap.keySet().toArray(facetValues);
			Arrays.sort(facetValues);
			sb.append("<span class=\"facet\">");
			sb.append("<span class=\"facet_name\">");
			sb.append(facetNames[i]);
			sb.append("<br/>");
			sb.append("</span>");
			sb.append("<span class=\"facet_values\">");
			for(int j = 0; j < facetValues.length; j++) {
				sb.append("<span class=\"facet_value\">");
				sb.append("<label>");
				sb.append("<input type=\"checkbox\" id=\""); 
				sb.append(facetNames[i] + "--" + facetValues[j]);
				sb.append("\"");
				sb.append(" class=\"facet_filter_checkbox\"");
//				sb.append(" checked=\"checked\"");
				sb.append(" />");
				sb.append(facetValues[j]);
				sb.append("<span class=\"facet_value_count\">");
				sb.append("(");
				sb.append(facetValueMap.get(facetValues[j]).cardinality());
				sb.append(")");
				sb.append("<br/>");
				sb.append("</span>");
				sb.append("</label>");
				sb.append("</span>");
			}
			sb.append("</span>"); //close facet_values
			sb.append("<br/>");
			sb.append("</span>"); //close facet
		}
		r = sb.toString();
		return r;
	}

}
