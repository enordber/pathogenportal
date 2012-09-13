package edu.vt.vbi.ci.pathport;

import java.util.Comparator;

public class PPGenomeInfo extends PPDataSet{
	//class and id names for html
	private static final String GENOME_INFO = "genome_info";
	private static final String GENOME_NAME = "genome_name";
	private static final String ORGANISM_TYPE = "organism_type";
	private static final String TAXON_ID = "genome_taxon_id";
	private static final String GENUS = "genome_genus";
	private static final String SPECIES = "genome_species";
	private static final String SEQUENCING_STATUS = "sequencing_status";
	private static final String STRAIN = "genome_strain";
	private static final String LENGTH = "genome_length";
	private static final String GENOME_PAGE_LINK = "genome_page_link";
	
	public static final String NOT_PROVIDED = "-";
	
	private String gid = NOT_PROVIDED;
	private String genus = NOT_PROVIDED;
	private String species = NOT_PROVIDED;
	private String strain = NOT_PROVIDED;
	private String genomePageURL = NOT_PROVIDED;
	private String fastaURL = NOT_PROVIDED;
	private String gffURL = NOT_PROVIDED;
	private String genomeVersion = NOT_PROVIDED;
	private int length = 0;
	private int geneCount = 0;
	private String brc = NOT_PROVIDED;
	private String html = null;
	private String brcURL = NOT_PROVIDED;
	
	//common subset of fields from GSCIDs
	private String genomeName = NOT_PROVIDED;
	private String organismType = NOT_PROVIDED;
	private String taxId = NOT_PROVIDED;
	private String status = NOT_PROVIDED;
	private String sampleAcquisitionDate = NOT_PROVIDED;
    private String sequencingCenter = NOT_PROVIDED;	

	public PPGenomeInfo() {
		
	}
	
	public String getHTML() {
		if(html == null) {
		    StringBuffer sb = new StringBuffer();
            sb.append("<span class=\">");
            sb.append(GENOME_INFO);
            sb.append("\">");
            
            sb.append("<span class=\"");
            sb.append(ORGANISM_TYPE);
            sb.append("\">");
            sb.append(getOrganismType());
            sb.append("</span>"); //close organism_type
            
            sb.append("<span class=\"");
            sb.append(GENOME_NAME);
            sb.append("\">");
            sb.append(getGenomeName());
            sb.append("</span>"); //close genome_name
            
            sb.append("<span class=\"");
            sb.append(GENUS);
            sb.append("\">");
            sb.append(getGenus());
            sb.append("</span>"); //close genome_genus

            sb.append("<span class=\"");            
            sb.append(SPECIES);
            sb.append("\">");
            sb.append(getSpecies());
            sb.append("</span>"); //close genome_species

            sb.append("<span class=\"");
            sb.append(STRAIN);
            sb.append("\">");
            sb.append(getStrain());
            sb.append("</span>"); //close genome_strain

            sb.append("<span class=\"");
            sb.append(TAXON_ID);
            sb.append("\">");
            sb.append(getTaxId());
            sb.append("</span>"); //close genome_taxon_id

            sb.append("<span class=\"");
            sb.append(SEQUENCING_STATUS);
            sb.append("\">");
            sb.append(getStatus());
            sb.append("</span>"); //close sequencing_status
            
            sb.append("<span class=\"");
            sb.append(LENGTH);
            sb.append("\">");
            sb.append(getLength());
            sb.append("</span>"); //close genome_length
            
            sb.append("<span class=\"");
            sb.append(GENOME_PAGE_LINK);
            sb.append("\">");
            sb.append(getGenomePageURL());
            sb.append("</span>"); //close genome_page_link
            
            sb.append("");
            
            sb.append("</span>"); //close genome_info
		    html = sb.toString();
		}
		return html;
	}

	public String getGenomeName() {
		return genomeName;
	}

	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public String getFastaURL() {
		return fastaURL;
	}

	public void setFastaURL(String fastaURL) {
		this.fastaURL = fastaURL;
	}

	public String getGffURL() {
		return gffURL;
	}

	public void setGffURL(String gffURL) {
		this.gffURL = gffURL;
	}

	public String getGenomeVersion() {
		return genomeVersion;
	}

	public void setGenomeVersion(String genomeVersion) {
		this.genomeVersion = genomeVersion;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getGenomePageURL() {
		return genomePageURL;
	}

	public void setGenomePageURL(String genomePageURL) {
		this.genomePageURL = genomePageURL;
	}

	public String getOrganismType() {
		return organismType;
	}

	public void setOrganismType(String organismType) {
		if(organismType == null || organismType.length() == 0) {
			organismType = "unknown"; 
		}
		this.organismType = organismType.trim();
		//organism type could be a facet
		setFacetValue("Organism Type", this.organismType);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if(status == null || status.length() == 0) {
			status = "N/A";
		}
		this.status = status;
		//status could be a facet
		setFacetValue("Status", status);
	}

	public String getSampleAcquisitionDate() {
		return sampleAcquisitionDate;
	}

	public void setSampleAcquisitionDate(String sampleAcquisitionDate) {
		this.sampleAcquisitionDate = sampleAcquisitionDate;
	}

	public String getSequencingCenter() {
		return sequencingCenter;
	}

	public void setSequencingCenter(String sequencingCenter) {
		this.sequencingCenter = sequencingCenter;
		//sequencing center could be a facet
		setFacetValue("Sequencing Center", sequencingCenter);
	}

	public void setBRC(String brc) {
		if(brc == null || brc.length() == 0 || brc.equals("-")) {
			this.brc = "NA";
		} else {
		    this.brc = brc;
		}
		
		//BRC could be a facet
		setFacetValue("BRC", this.brc);
	}
	
	public String getBRC() {
		return this.brc;
	}

	public int getGeneCount() {
		return geneCount;
	}

	public void setGeneCount(int geneCount) {
		this.geneCount = geneCount;
	}

	/**
	 * Adds data from another PPGenomeInfo Object that is representing the
	 * same genome, but may have information from a different source that
	 * is missing in this PPGenomeInfo Object.
	 * 
	 * @param patricGI
	 */
	public void addDataFrom(PPGenomeInfo other) {
		if(this.gid == NOT_PROVIDED) {
			this.setGid(other.gid);
		}
		if(this.genus == NOT_PROVIDED) {
			this.setGenus(other.genus);
		}
		if(this.species == NOT_PROVIDED) {
			this.setSpecies(other.species);
		}
		if(this.strain == NOT_PROVIDED) {
			this.setStrain(other.strain);
		}
		if(this.genomePageURL == NOT_PROVIDED) {
			this.setGenomePageURL(other.genomePageURL);
		}
		if(this.fastaURL == NOT_PROVIDED) {
			this.setFastaURL(other.fastaURL);
		}
		if(this.gffURL == NOT_PROVIDED) {
			this.setGffURL(other.gffURL);
		}
		if(this.genomeVersion == NOT_PROVIDED) {
			this.setGenomeVersion(other.genomeVersion);
		}
		if(this.length == 0) {
			this.setLength(other.length);
		}
		if(this.geneCount == 0) {
			this.setGeneCount(other.geneCount);
		}
		if(this.brc == NOT_PROVIDED) {
			this.setBRC(other.brc);
		}
		if(this.brcURL == NOT_PROVIDED) {
			this.brcURL = other.brcURL;
		}
		if(this.genomeName == NOT_PROVIDED) {
			this.setGenomeName(other.genomeName);
		}
		if(this.organismType == NOT_PROVIDED) {
			this.setOrganismType(other.organismType);
		}
		if(this.taxId == NOT_PROVIDED) {
			this.setTaxId(other.taxId);
		}
		if(this.status == NOT_PROVIDED) {
			this.setStatus(other.status);
		}
		if(this.sampleAcquisitionDate == NOT_PROVIDED) {
			this.setSampleAcquisitionDate(other.sampleAcquisitionDate);
		}
		if(this.sequencingCenter == NOT_PROVIDED) {
			this.setSequencingCenter(other.sequencingCenter);
		}
	}
	
	public void setBRCURL(String url) {
		brcURL = url;
	}

	public String getBRCURL() {
		return brcURL;
	}

	public static class GenomeNameComparator implements Comparator {
		
		public int compare(Object arg0, Object arg1) {
			int r = 0;
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			r = ppgi0.getGenomeName().compareTo(ppgi1.getGenomeName());
			return r;
		}
	}

	public static class BRCComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getBRC().compareTo(ppgi1.getBRC());
		}
	}

	public static class SequencingCenterComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getSequencingCenter().compareTo(ppgi1.getSequencingCenter());
		}
	}

	public static class OrganismTypeComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getOrganismType().compareTo(ppgi1.getOrganismType());
		}
	}

	public static class TaxonIdComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getTaxId().compareTo(ppgi1.getTaxId());
		}
	}

	public static class StatusComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getStatus().compareTo(ppgi1.getStatus());
		}
	}

	public static class LengthComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getLength() - ppgi1.getLength();
		}
	}

	public static class GeneCountComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			PPGenomeInfo ppgi0 = (PPGenomeInfo)arg0;
			PPGenomeInfo ppgi1 = (PPGenomeInfo)arg1;
			
			return ppgi0.getGeneCount() - ppgi1.getGeneCount();
		}
	}

}
