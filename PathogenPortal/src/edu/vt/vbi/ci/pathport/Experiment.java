package edu.vt.vbi.ci.pathport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import edu.vt.vbi.ci.util.parse.XMLTreeElement;


public class Experiment extends PPDataSet implements Comparable{
	private static final String EXPERIMENT_ID = "EXPERIMENT_ID";
	private static final String PROTOCOL = "Protocol";
	private static final String BIOMATERIAL = "BioMaterial";
	private static final String WILDTYPE = "Wildtype";
	private static final String MUTANT = "Mutant";
	private static final String PLATFORM = "Platform";
	private static final String SAMPLE = "Sample";
	private static final String PUBLICATION = "Publication";
	private static final String PERSON = "Person";
	private static final String DATAFILES = "DataFiles"; 
	private static final String EXPERIMENT = "Experiment";
	private static final String FILE_URL = "File_URL";

	private static final String TITLE = "TITLE";
	private static final String EXPERIMENT_TYPE = "EXPERIMENT_TYPE";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String CREATE_DATE = "CREATE_DATE";
	private static final String DATE = "DATE";
	private static final String EXPERIMENT_DATE = "EXPERIMENT_EXECUTE_DATE";
	private static final String MODIFIED_DATE = "MODIFIED_DATE";
	private static final String SUMMARY = "SUMMARY";
	private static final String SPECIES_NAME = "SPECIES_NAME";
	private static final String PUBMED_ID = "PUBMED_ID";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String ORGANIZATION = "ORGANIZATION";
	private static final String EMAIL = "EMAIL";
	private static final String AUTHORS = "AUTHORS";
	
	//MINiML Strings - some are identical to those above, so are not repeated
	private static final String MINIML = "MINiML";
	private static final String IID = "iid";
	private static final String MI_TITLE = "Title";
	private static final String STATUS = "Status";
	private static final String SUBMISSION_DATE = "Submission-Date";
	
	private XMLTreeElement element;
	private Date dateObj;
	
	private String id;
	private String title;
	private String organisms = null;
	private String type;
	private String sampleCount;
	private String cite;
	private String date = "";
	private String quarterDate;
	private String summary;
	private String fileURL;
	private int sourceIndex;

	private String source;
	private String sourceLink;
	
	private String contactPerson;
	private String contactEmail;
	private String contactOrganization;
	
	public Experiment() {
		
	}
	
	public Experiment(XMLTreeElement experimentElement) {
		element = experimentElement;
		if(experimentElement.getType().equals(MINIML)) {
//			System.out.println("This experiment is MINiML");
			parseMINiMLElement(experimentElement);
		} else {
//			System.out.println("This experiment type is: " + experimentElement.getType());
		    parseExperimentElement(experimentElement);
		}
	}

	private void parseMINiMLElement(XMLTreeElement experimentElement) {
		XMLTreeElement platform = experimentElement.getChildOfType(PLATFORM);
		id = (String) platform.getAttribute(IID);
		type = "Microarray";
		title = (String) experimentElement.getChildOfType(MI_TITLE).getContent();
		date = (String)experimentElement.getChildOfType(STATUS).getChildOfType(SUBMISSION_DATE).getContent();
		summary = "";
	}

	private void parseExperimentElement(XMLTreeElement experiment) {
		id = (String) experiment.getAttribute(EXPERIMENT_ID);
		type = (String) experiment.getAttribute(EXPERIMENT_TYPE);
		title = (String) experiment.getAttribute(DESCRIPTION);
		date = (String) experiment.getAttribute(CREATE_DATE);
		summary = (String) experiment.getAttribute(SUMMARY);

		ArrayList speciesList = new ArrayList();
		XMLTreeElement[] biomaterials = experiment.getChildrenOfType(BIOMATERIAL);
		for(int i = 0; i < biomaterials.length; i++) {
			XMLTreeElement wildtype = biomaterials[i].getChildOfType(WILDTYPE);
			if(wildtype != XMLTreeElement.NO_SUCH_ELEMENT) {
				String speciesName = (String) wildtype.getAttribute(SPECIES_NAME);
				speciesList.add(speciesName);
			}

			XMLTreeElement mutant = biomaterials[i].getChildOfType(MUTANT);
			if(mutant != XMLTreeElement.NO_SUCH_ELEMENT) {
				String speciesName = (String) mutant.getAttribute(SPECIES_NAME);
				speciesList.add(speciesName);
			}
		}

		String[] speciesNames = new String[speciesList.size()];
		speciesList.toArray(speciesNames);

		if(speciesNames.length > 0) {
			organisms = speciesNames[0];
		}
		for(int i = 1; i < speciesNames.length; i++) {
			organisms += "; " + speciesNames[i];
		}

		XMLTreeElement pub = experiment.getChildOfType(PUBLICATION);
		if(pub == XMLTreeElement.NO_SUCH_ELEMENT) {
			cite = "";
		} else {
			cite = (String) pub.getAttribute(PUBMED_ID);
			//get publication date. for now, use this instead of create date
			String pubDate = (String) pub.getAttribute(DATE);
			if(pubDate != null) {
				date = pubDate;
			}
		}
		
		XMLTreeElement[] samples = experiment.getChildrenOfType(SAMPLE);
		sampleCount = "" + samples.length;
		
		//set data file URL 
		XMLTreeElement dataFiles = experiment.getChildOfType(DATAFILES);
		if(dataFiles != XMLTreeElement.NO_SUCH_ELEMENT) {
			fileURL = (String) dataFiles.getAttribute(FILE_URL);
		}
		
		//set contact person
		XMLTreeElement[] personElements = experiment.getChildrenOfType(PERSON);
		if(personElements != null && personElements.length > 0 && personElements[0] != XMLTreeElement.NO_SUCH_ELEMENT) {
			String lastName = (String) personElements[0].getAttribute(LAST_NAME);
			String firstName = (String) personElements[0].getAttribute(FIRST_NAME);
			String org = (String) personElements[0].getAttribute(ORGANIZATION);
			String email = (String) personElements[0].getAttribute(EMAIL);
			setContactPerson(firstName + " " + lastName);
			setContactEmail(email);
			setContactOrganization(org);
		}
	}
	
	public int compareTo(Object o) {
		int r = 0; 
		Experiment other = (Experiment)o;
		if(this.date == null) {
			System.out.println("Experiment has null date " + title);
		}
		if(other.date == null) {
			System.out.println("other Experiment is null: " + title);
		}
		r = this.date.compareTo(other.date);
		return r;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getOrganisms() {
		return organisms;
	}

	public String getType() {
		return type;
	}

	public String getSampleCount() {
		return sampleCount;
	}

	public String getCite() {
		return cite;
	}

	public String getDate() {
		return date;
	}
	
	private void determineQuarterDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dateObj = sdf.parse(date);
			int month = dateObj.getMonth();
			int quarter = month /3;
			int quarterEndMonth = quarter*3 + 2;
			dateObj.setMonth(quarterEndMonth);
			sdf.applyPattern("yyyy-MM");
			quarterDate = sdf.format(dateObj);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public XMLTreeElement getElement() {
		return element;
	}
	
	public String getQuarterDate() {
		if(quarterDate == null) {
			if(getDate() != null) {
			    determineQuarterDate(getDate());
			}
		}
		return quarterDate;
	}

	public String getSummary() {
		return summary;
	}

	public String getFileURL() {
		return fileURL;
	}
	
	public static class ExperimentDateComparator implements Comparator{
		private boolean ascending;

		public ExperimentDateComparator(boolean ascending) {
		    this.ascending = ascending;
		}
		
		public int compare(Object o1, Object o2) {
			int r = 0;
			Experiment e1 = (Experiment) o1;
			Experiment e2 = (Experiment) o2;
			if(ascending) {
			    r = e1.date.compareTo(e2.date);
			} else {
				r = e2.date.compareTo(e1.date);
			}
			return r;
		}
		
	}

	public static class ExperimentOrganismComparator implements Comparator{
		private boolean ascending;

		public ExperimentOrganismComparator(boolean ascending) {
		    this.ascending = ascending;
		}
		
		public int compare(Object o1, Object o2) {
			int r = 0;
			Experiment e1 = (Experiment) o1;
			Experiment e2 = (Experiment) o2;
			if(ascending) {
			    r = e1.organisms.compareTo(e2.organisms);
			} else {
				r = e2.organisms.compareTo(e1.organisms);
			}
			return r;
		}
		
	}
	
	public static class ExperimentDataTypeComparator implements Comparator{
		private boolean ascending;

		public ExperimentDataTypeComparator(boolean ascending) {
		    this.ascending = ascending;
		}
		
		public int compare(Object o1, Object o2) {
			int r = 0;
			Experiment e1 = (Experiment) o1;
			Experiment e2 = (Experiment) o2;
			if(ascending) {
			    r = e1.type.compareTo(e2.type);
			} else {
				r = e2.type.compareTo(e1.type);
			}
			return r;
		}
		
	}

	
	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
		addKeyValuePair("source_index", ""+sourceIndex);
	}

	public Date getDateObj() {
		return dateObj;
	}

	public void setDateObj(Date dateObj) {
		this.dateObj = dateObj;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		addKeyValuePair("source", source);
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String sourceLink) {
		this.sourceLink = sourceLink;
		addKeyValuePair("source_link", sourceLink);
	}

	public void setElement(XMLTreeElement element) {
		this.element = element;
	}

	public void setId(String id) {
		this.id = id;
		addKeyValuePair("id", id);
	}

	public void setTitle(String title) {
		this.title = title;
		addKeyValuePair("title", title);
	}

	public void setOrganisms(String organisms) {
		this.organisms = organisms;
		addKeyValuePair("organisms", organisms);
	}

	public void setType(String type) {
		this.type = type;
		addKeyValuePair("type", type);
	}

	public void setSampleCount(String sampleCount) {
		this.sampleCount = sampleCount;
		addKeyValuePair("sample_count", sampleCount);
	}

	public void setCite(String cite) {
		this.cite = cite;
		addKeyValuePair("cite", cite);
	}

	public void setDate(String date) {
		this.date = date;
		addKeyValuePair("date", date);
	}

	public void setQuarterDate(String quarterDate) {
		this.quarterDate = quarterDate;
		addKeyValuePair("quarter_date", quarterDate);
	}

	public void setSummary(String summary) {
		this.summary = summary;
		addKeyValuePair("summary", summary);
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
		addKeyValuePair("file_url", fileURL);
	}
	
	public String toString() {
		String r = null;
	    r = getTitle();
		return r;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactOrganization() {
		return contactOrganization;
	}

	public void setContactOrganization(String contactOrganization) {
		this.contactOrganization = contactOrganization;
	}
	
	
	
	
}
