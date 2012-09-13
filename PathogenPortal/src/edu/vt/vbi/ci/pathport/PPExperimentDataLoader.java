package edu.vt.vbi.ci.pathport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import edu.vt.vbi.ci.pathport.Experiment;
import edu.vt.vbi.ci.util.PathPortUtilities;
import edu.vt.vbi.ci.util.parse.XMLTreeElement;

public class PPExperimentDataLoader extends PPDataSourceLoader {

	private static final String EXPERIMENT = "Experiment";
	private static final String PRC_DATA_SETS = "PRC Data Sets";
	private static final String MICROARRY = "Microarray";
	private static final String MASS_SPEC = "Mas spectrometry";
	private static final String STRUCTURE = "Structure";
	private static final String PROTEIN_INTERACTION = "Protein interaction";
	private static final String CLONE = "Clone";
	private static final String HOST_RESPONSE = "Host Response Data Sets";
	
	public PPExperimentDataLoader() {
		
	}

	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
        String name = dataSource.getName();
//        System.out.println("Loading Experiment Data Set: " + name);
//        System.out.println("DataSource tags: ");
//        String[] tags = dataSource.getTags();
//        for(int i = 0; i < tags.length; i++) {
//        	System.out.println(tags[i]);
//        }
        if(name.equals(PRC_DATA_SETS)) {
//        	System.out.println("PPExperimentLoader loading PRC data");
        	loadPRCData(dataSource);
        } else if(name.equals(HOST_RESPONSE)) {
//        	System.out.println("PPExperimentLoader loading Host Response data");
        	if(dataSource.getLocationType().equals("xls url")) {
        		loadXLS(dataSource);
        	} else {
        		loadCSVAsMultiple(dataSource);
        	}
        }
	}

	private void loadXLS(PPDataSource dataSource) {
		System.out.println("PPExperimentDataLoader.loadXLS(): " + dataSource.getName());
		
		//columns:
//		Col 0: Project ID
//		Col 1: Assay ID
//		Col 2: Assay Type
//		Col 3: Assay Subtype
//		Col 4: Assay Description
//		Col 5: Pathogen Genus
//		Col 6: Pathogen Species
//		Col 7: Pathogen Strain
//		Col 8: Pathogen Naming Authority
//		Col 9: Pathogen Naming Authority Identifier
//		Col 10: Host Genus
//		Col 11: Host Species
//		Col 12: Host Sublclassification
//		Col 13: Host Naming Authority
//		Col 14: Host Naming Authority Identifier
//		Col 15: Cell line/Tissue
//		Col 16: Sample Source Type
//		Col 17: Sample Cell Type
//		Col 18: Release Date

		int idIndex = 0;
		int titleIndex = 1;
		int sourceLinkIndex = 2;
		int summaryIndex = 9;
		int contactOrganizationIndex = 5;
		int contactPersonIndex = 6;
		int contactEmailIndex = 7;
		int citeIndex = 8;
		int keywordIndex = 10;
		int tagsIndex = 12;

		
		String url = dataSource.getLocationURLs()[0];

		try {
			File tempFile = File.createTempFile("ppdata_", ".xls");
			String tempFileName = tempFile.getAbsolutePath();
			System.out.println("writing experiment data to local file: " + tempFileName);
			PathPortUtilities.downloadDataURLToFile(url, tempFileName);
			
			InputStream inp = new FileInputStream(tempFileName);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			HSSFSheet sheet =  wb.getSheetAt(0);
			int lastRow = sheet.getLastRowNum();
			lastRow++;
			HSSFRow header = sheet.getRow(0);
			int cellCount = header.getPhysicalNumberOfCells();
			
			ArrayList<Experiment> expList = new ArrayList<Experiment>();
			Experiment exp;
			
			for(int i = 0; i < lastRow; i++) {
				exp = new Experiment();
				HSSFRow row = sheet.getRow(i);
				String id = "";
				String title = "";
				String sourceLink = "";
				String summary = "";
				String contactPerson = "";
				String contactEmail = "";
				String contactOrg = "";
				String cite = "";
				String keywords = "";
				String tags = "";
				
				Cell cell;
				cell = row.getCell(idIndex); 
				if(cell != null) {
					id = cell.toString().trim();
				}
				cell = row.getCell(titleIndex); 
				if(cell != null) {
					title = cell.toString().trim();
				}
				cell = row.getCell(sourceLinkIndex); 
				if(cell != null) {
					sourceLink = cell.toString().trim();
				}
				cell = row.getCell(summaryIndex); 
				if(cell != null) {
					summary = cell.toString().trim();
				}
				cell = row.getCell(contactPersonIndex); 
				if(cell != null) {
					contactPerson = cell.toString().trim();
				}
				cell = row.getCell(contactEmailIndex); 
				if(cell != null) {
					contactEmail = cell.toString().trim();
				}
				cell = row.getCell(contactOrganizationIndex); 
				if(cell != null) {
					contactOrg = cell.toString().trim();
				}
				cell = row.getCell(citeIndex); 
				if(cell != null) {
					cite = cell.toString().trim();
				}
				
				//currently combining keywords and tags, considering them all
				//as tags
				ArrayList<String> tagsAndKeywordsList = new ArrayList<String>();
				cell = row.getCell(keywordIndex); 
				if(cell != null) {
					keywords = cell.toString();
					String[] keywordFields = keywords.split(",");
					for(int j = 0; j < keywordFields.length; j++) {
						tagsAndKeywordsList.add(keywordFields[j].trim());
					}
				}
				cell = row.getCell(tagsIndex); 
				if(cell != null) {
					tags = cell.toString();
					String[] tagFields = tags.split(",");
					for(int j = 0; j < tagFields.length; j++) {
						tagsAndKeywordsList.add(tagFields[j].trim());
					}
				}
				String[] tagsAndKeywords = new String[tagsAndKeywordsList.size()];
				tagsAndKeywordsList.toArray(tagsAndKeywords);
				exp.setTags(tagsAndKeywords);
				
				exp.setId(id);
				exp.setTitle(title);
				exp.setSourceLink(sourceLink);
				exp.setSummary(summary);
				exp.setContactPerson(contactPerson);
				exp.setContactEmail(contactEmail);
				exp.setContactOrganization(contactOrg);
				exp.setCite(cite);
				
				expList.add(exp);
			}
			
			
			Experiment[] experiments = new Experiment[expList.size()];
			expList.toArray(experiments);
			
			dataSource.setDataSets(experiments);
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void loadPRCData(PPDataSource dataSource) {
		String[] xmlLocs = dataSource.getLocationURLs();
		ArrayList<PPDataSet> dataSetList = new ArrayList<PPDataSet>();
	    for(int i = 0; i < xmlLocs.length; i++) {
//	    	System.out.println("get doc from " + xmlLocs[i].trim());
	    	String dataDoc = PathPortUtilities.getContentsOfURL(xmlLocs[i].trim());
//	    	System.out.println("dataDoc: " + dataDoc);
			//parse XML dataDoc and get the Experiments
			XMLTreeElement full = new XMLTreeElement(dataDoc);
			XMLTreeElement[] elements = full.getChildrenOfType(EXPERIMENT);

			ArrayList<String> tagList = new ArrayList<String>();
//			System.out.println("PPExperimentDataLoader.loadPRCData() experiments: " + elements.length);
			for(int j = 0; j < elements.length; j++) {
				tagList.clear();
				tagList.add(PathPortData.LEGACY); //all PRC data is Leagacy
				Experiment exp = new Experiment(elements[j]);
				exp.setDataSource(dataSource);
				String type = exp.getType();
//				System.out.println("experiment type: " + exp.getType());
				if(type.equalsIgnoreCase(MICROARRY)) {
					tagList.add(PathPortData.TRANSCRIPTOME);
				} else if (type.equalsIgnoreCase(MASS_SPEC)) {
					tagList.add(PathPortData.PROTEOME);
				} else if(type.equalsIgnoreCase(STRUCTURE))  {
					tagList.add(PathPortData.STRUCTURE);
				} else if(type.equalsIgnoreCase(PROTEIN_INTERACTION)) {
					tagList.add(PathPortData.PROTEOME);
					tagList.add(PathPortData.INTERACTOME);
				} else if(type.equalsIgnoreCase(CLONE)) {
					tagList.add(PathPortData.REAGENT);
					tagList.add(PathPortData.CLONE);
				}
				
				String orgs = exp.getOrganisms();
//				System.out.println("Organisms for this experiment: " + orgs); 
				if(orgs.matches(".*Homo sapiens.*") || orgs.matches(".*Mus musculus.*")) {
//					System.out.println("this is a host data set");
					tagList.add(PathPortData.HOST);
				} else {
					tagList.add(PathPortData.PATHOGEN);
				}
				
				String[] experimentTags = new String[tagList.size()];
				tagList.toArray(experimentTags);
				exp.setTags(experimentTags);
				
				dataSetList.add(exp);
			}	
	    }
		
		PPDataSet[] dataSets = new PPDataSet[dataSetList.size()];
		dataSetList.toArray(dataSets);
		dataSource.setDataSets(dataSets);

	}
	

	private void loadCSVAsMultiple(PPDataSource dataSource) {
//		System.out.println("PPExperimentDataLoader.loadCSVAsMultiple(): " + dataSource.getName());
		String csv = PathPortUtilities.getContentsOfURL(dataSource.getLocationURLs()[0]);
//		System.out.println("csv:");
//		System.out.println(csv);
		//parse csv file and create html table

		int idIndex = 0;
		int titleIndex = 1;
		int sourceLinkIndex = 2;
		int summaryIndex = 9;
		int contactOrganizationIndex = 5;
		int contactPersonIndex = 6;
		int contactEmailIndex = 7;
		int citeIndex = 8;
		int dateIndex;
		
		ArrayList<Experiment> expList = new ArrayList<Experiment>();
		Experiment exp;
		String[] rows = csv.split("\n");
//		System.out.println("data rows: " + (rows.length-1));
		String[] cells;
		//first row has column titles. I am assuming a particular order, because this is just 
		//a short term solution. So I won't bother with lots of fancy options
		if(rows.length > 0) {
			String[] headings = rows[0].split("\t"); 
			int columnCount = headings.length;
//			System.out.println("columns: " + columnCount);
//			System.out.println("id: " + headings[idIndex]);
//			System.out.println("title: " + headings[titleIndex]);
//			System.out.println("source link: " + headings[sourceLinkIndex]);
//			System.out.println("summary: " + headings[summaryIndex]);
//			System.out.println("organization: " + headings[contactOrganizationIndex]);
//			System.out.println("person: " + headings[contactPersonIndex]);
//			System.out.println("email: " + headings[contactEmailIndex]);
//			System.out.println("cite: " + headings[citeIndex]);
			
			for(int i = 1; i < rows.length; i++) {
				cells = rows[i].split("\t");
				if(cells.length != columnCount) {
					System.out.println("there are " + columnCount + 
							" columns, but row " + i + " has " + cells.length);
					for(int j = 0; j < cells.length; j++) {
						System.out.println("\t" + j + ": " + cells[j]);
					}
				} else {
					exp = new Experiment();
					exp.setId(cells[idIndex]);
					exp.setTitle(cells[titleIndex]);
					exp.setSourceLink(cells[sourceLinkIndex]);
					exp.setSummary(cells[summaryIndex]);
					exp.setContactPerson(cells[contactPersonIndex]);
					exp.setContactEmail(cells[contactEmailIndex]);
					exp.setContactOrganization(cells[contactOrganizationIndex]);
					exp.setCite(cells[citeIndex]);
					
					expList.add(exp);
//					System.out.println("exp.json: " + exp.getJSONString());
//					System.out.println(cells[idIndex]);
//					System.out.println(exp.getId());
//					System.out.println(cells[titleIndex]);
//					System.out.println(exp.getTitle());
//					System.out.println(cells[sourceLinkIndex]);
//					System.out.println(exp.getSourceLink());
//					System.out.println(cells[summaryIndex]);
//					System.out.println(exp.getSummary());
//					System.out.println(cells[contactEmailIndex]);
//					System.out.println(exp.getContactEmail());
//					System.out.println(cells[contactOrganizationIndex]);
//					System.out.println(exp.getContactOrganization());
//					System.out.println(cells[citeIndex]);
//					System.out.println(exp.getCite());
				}
			}
			
			Experiment[] experiments = new Experiment[expList.size()];
			expList.toArray(experiments);
			
			dataSource.setDataSets(experiments);
		}

	}
}
