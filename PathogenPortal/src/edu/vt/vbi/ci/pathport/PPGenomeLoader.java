package edu.vt.vbi.ci.pathport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import nanoxml.XMLParseException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.vt.vbi.ci.util.PathPortUtilities;
import edu.vt.vbi.ci.util.applet.AppletUtilities;
import edu.vt.vbi.ci.util.parse.XMLTreeElement;

public class PPGenomeLoader extends PPDataSourceLoader{
	private static final String PATRIC_DOWNLOAD_URL = 
		"http://brcdownloads.vbi.vt.edu/patric2/genomes/";
	private static final String PATRIC_GENOME_PAGE_URL = 
		"http://patricbrc.org/portal/portal/patric/Genome?cType=genome&cId=";

	private static String ncbiTaxonomyURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&mode=xml&id=";

	private static final String PATRIC_TAXONOMY_URL_PREFIX = "http://patricbrc.org/portal/portal/patric/Taxon?cType=taxon&cId=";

	//IGS details are at http://gscid.igs.umaryland.edu/generated/{wp}.xml,
	//where {wp} is the whitepaper project name, all lower case, with underscores
	//replacing spaces
	private static final String IGS_DETAIL_URL_PREFIX = "http://gscid.igs.umaryland.edu/generated/";
	private static final String IGS_DETAIL_URL_SUFFIX = ".xml";

	//Sequencing Center name constants
	private static final String IGS_SHORT = "IGS";
	private static final String IGS_FULL = "Institute for Genome Sciences";
	private static final String BROAD_SHORT = "Broad";
	private static final String BROAD_FULL = "The Broad Institute";
	private static final String JCVI_SHORT = "JCVI";
	private static final String JCVI_FULL = "J. Craig Venter Institute";

	//DataSource name constants for genome sources
	private static final String EUPATHDB_GENOME = "EuPathDB Genomes";
	private static final String IRD_GENOMES = "IRD Genomes";
	private static final String PATRIC_GENOMES = "PATRIC Genomes";
	private static final String PATRIC_GENOME_METADATA = "PATRIC Genome Metadata";
	private static final String VECTORBASE_GENOMES = "VectorBase Genomes";
	private static final String VIPR_GENOMES = "ViPR Genomes";
	private static final String IGS_GENOME_METADATA = "IGS GSCID Genome Metadata";
	private static final String BROAD_GENOME_METADATA = "Broad GSCID Genome Metadata";
	private static final String JCVI_GENOME_METADATA = "JCVI GSCID Genome Metadata";
	private static final String NCBI_TAXONOMY = "NCBI Taxonomy Map";

	//XML tag constants
	private static final String PROJECT_TAG = "project";
	private static final String WHITEPAPER_TITLE_TAG = "white_paper_title";
	private static final String RECORD_TAG = "record";
	private static final String SPECIES_NAME_TAG = "species_name";
	private static final String ORGANISM_TAG = "organism";
	private static final String ORGANISM_TYPE_TAG = "";
	private static final String TAXONOMY_TAG = "taxonomy";
	private static final String STATUS_TAG = "status";
	private static final String SEQUENCING_TAG = "sequencing";
	private static final String ACQUISITION_TAG = "aquisition";

	//Facet name and value constants
	private static final String BRC = "BRC";
	private static final String PATRIC = "PATRIC";
	private static final String EUPATHDB = "EuPathDB";
	private static final String VIPR = "ViPR";
	private static final String IRD = "IRD";
	private static final String VECTORBASE = "VectorBase";
	private static final String GSC = "GSC";
	private static final String TYPE = "Type";
	private static final String STATUS = "Status";

	//store mapping from taxon Id to Lineage Strings
	private static HashMap<String, String> taxonIdToLineage;

	private HashMap<String,String> typeToBRC;
	private HashMap<String, String> termToCommonTerm;

	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
		System.out.println(">PPGenomeLoader.loadItemsFromSource()");
		//create map for genome type -> BRC
		typeToBRC = new HashMap<String,String>(){
			{
				put("Bacteria","PATRIC");
				put("bacteria","PATRIC");
				put("Virus","ViPR");
				put("Fungi","NA");
				put("Fungus","NA");
				put("Protist","EuPathDB");
				put("protozoa","EuPathDB");
				put("Parasitic Protozoa","EuPathDB");
				put("Unclassified","");
				put("Mammal","NA");
				put("Other","NA");
				put("Metagenome", "NA");
			}

		};

		termToCommonTerm = new HashMap<String, String>();
		termToCommonTerm.put("bacteria","Bacteria");
		termToCommonTerm.put("Fungus","Fungi");
		termToCommonTerm.put("protozoa","Protozoa");
		termToCommonTerm.put("Deprecated","Failed/Abandoned");
		termToCommonTerm.put("Failed","Failed/Abandoned");
		termToCommonTerm.put("Abandoned","Failed/Abandoned");
		termToCommonTerm.put("Ongoing","In Progress");
		termToCommonTerm.put("Complete","Completed");

		//see what genome data source this is, as each one needs to be handled differently
		if(dataSource != null) {
			String name = dataSource.getName();
			System.out.println("name: " + name);
			if(name.equalsIgnoreCase(EUPATHDB_GENOME)) {
				parseEuPathDBSequenceInfo(dataSource);
			} else if(name.equalsIgnoreCase(IRD_GENOMES)) {

			} else if(name.equalsIgnoreCase(PATRIC_GENOMES)) {
				parsePATRICSequenceInfo(dataSource);
			} else if(name.equalsIgnoreCase(PATRIC_GENOME_METADATA)) {
				parsePATRICGenomeMetadata(dataSource);
			} else if(name.equalsIgnoreCase(VECTORBASE_GENOMES)) {

			} else if(name.equalsIgnoreCase(VIPR_GENOMES)) {

			} else if(name.equalsIgnoreCase(IGS_GENOME_METADATA)) {
				parseIGSMetadata(dataSource);
			} else if(name.equalsIgnoreCase(BROAD_GENOME_METADATA)) {
				parseBroadMetadata(dataSource);
			} else if(name.equalsIgnoreCase(JCVI_GENOME_METADATA)) {
				parseJCVIMetadata(dataSource);
			} else if(name.equalsIgnoreCase(NCBI_TAXONOMY)) {
				loadTaxonomyMap(dataSource);
			}
		}
	}


	private void loadTaxonomyMap(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0].trim();
		System.out.println("location: " + location);
		String locationType = dataSource.getLocationType();
		System.out.println("locationType: " + locationType);
		String fullDoc = PathPortUtilities.getContentsOfURL(location);
		String[] lines = fullDoc.split("\n");
		System.out.println("lines in taxonomy file: " + lines.length);

		if(taxonIdToLineage == null) {
			taxonIdToLineage = new HashMap<String, String>();
		}

		for(int i = 0; i < lines.length; i++) {
			if(lines[i].length() > 2) {
				String[] fields = lines[i].split("\t");
				if(fields.length == 2) {
					taxonIdToLineage.put(fields[0].trim(), fields[1].trim());
				}
			}
		}

		System.out.println("loaded " + taxonIdToLineage.size() + " taxonomy entries");
	}


	/**
	 * Retrieves and parses the PATRIC tab-delimited file containing 
	 * genome metadata.
	 *  
	 * @param dataSource
	 */
	private void parsePATRICGenomeMetadata(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0].trim();
		System.out.println("location: " + location);
		String locationType = dataSource.getLocationType();
		System.out.println("locationType: " + locationType);
		String fullDoc = PathPortUtilities.getContentsOfURL(location);
		String[] lines = fullDoc.split("\n");
		System.out.println("lines in PATRIC file: " + lines.length);

		String[] dataSourceTags = dataSource.getTags();

		int gidIndex = 0;
		int genomeNameIndex = 1;
		int taxonIdIndex = 2;
		int lengthIndex = 3;
		int statusIndex = 4;
		int cdsCountIndex = 9;

		ArrayList<PPGenomeInfo> genomeList = new ArrayList<PPGenomeInfo>(lines.length);
		for(int i = 1; i < lines.length; i++) {
			if(lines[i].length() > 10) { //just to make sure it's not an empty line
				String[] fields = lines[i].split("\t");
				if(fields.length > 9) {
					//there are enough fields, so load data from this line
					String gid = fields[gidIndex].trim();
					String genomeName = fields[genomeNameIndex].trim();
					String taxonId = fields[taxonIdIndex].trim();
					String lengthS = fields[lengthIndex].trim();
					String status = fields[statusIndex].trim();
					String cdsCountS = fields[cdsCountIndex].trim();

					int length = 0;
					if(lengthS != null && lengthS.length() > 0) {
						try {
							length = Integer.parseInt(lengthS);
						} catch(NumberFormatException nfe) {
							nfe.printStackTrace();
							System.out.println("PPGenomeLoader.parsePATRICGenomeMetada() problem with length from line " + i + ": ");
							System.out.println(lines[i]);
						}
					}

					int cdsCount = 0;
					if(cdsCountS != null && cdsCountS.length() > 0) {
						try {
							cdsCount = Integer.parseInt(cdsCountS);
						} catch(NumberFormatException nfe) {
							nfe.printStackTrace();
							System.out.println("PPGenomeLoader.parsePATRICGenomeMetada() problem with cds count from line " + i + ": ");
							System.out.println(lines[i]);
						}
					}

					PPGenomeInfo genome = new PPGenomeInfo();
					genome.setBRC("PATRIC");
					genome.setGenomeName(genomeName);
					genome.setTaxId(taxonId);
					genome.setLength(length);
					genome.setGeneCount(cdsCount);

					if(genomeName != null) {
						//try to determine genus, species, and strain from the name
						//assume first token is genus, second token is species, and subsequent tokens are strain
						String[] tokens = genomeName.split("\\s+");
						String genus = "";
						String species = "";
						String strain = "";
						if(tokens.length > 0) {
							genus = tokens[0];
						}
						if(tokens.length > 1) {
							species = tokens[1];
						}
						if(tokens.length > 2) {
							strain = tokens[2];
						}
						for(int j = 3; j < tokens.length; j++) {
							strain = strain + " " + tokens[j];
						}

						genome.setGenus(genus);
						genome.setSpecies(species);
						genome.setStrain(strain);
					}

					genome.setDataSource(dataSource);
					genome.setTags(dataSourceTags);

					if(taxonId != null) {
						genome.setBRCURL(PATRIC_TAXONOMY_URL_PREFIX+taxonId);
					}

					expandGenomeInfo(genome);
					genomeList.add(genome);
				}
			}
		}

		PPGenomeInfo[] genomes = new PPGenomeInfo[genomeList.size()];
		genomeList.toArray(genomes);
		dataSource.setDataSets(genomes);
	}


	/**
	 * Adds information to the given genome. This includes BRC links,
	 * whitepaper info, etc.
	 * 
	 * @param genome
	 */
	private void expandGenomeInfo(PPGenomeInfo genome) {
		//consolidate terms
		String status = genome.getStatus();
		String commonStatus = termToCommonTerm.get(status);
		
//		System.out.println("PPGenomeLoader.expandGenomeInfo() from: " + genome.getSequencingCenter() + "\tstatus: " + status + "\tcommonStatus: " + commonStatus);
		if(status.equalsIgnoreCase("Abandoned")) {
			System.out.println("Abandoned: " + status + " -> " + commonStatus);
		}
		if(commonStatus != null) {
			genome.setStatus(commonStatus);
		}

		String commonType = termToCommonTerm.get(genome.getOrganismType());
		if(commonType != null) {
			genome.setType(commonType);
		}
	}


	private void parseJCVIMetadata(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0].trim();
		System.out.println("location: " + location);
		String locationType = dataSource.getLocationType();
		if(locationType.equalsIgnoreCase("json url")) {
			String json = PathPortUtilities.getContentsOfURL(location).trim();

			try {
				JSONArray ja = new JSONArray(json);
				JSONObject jo = new JSONObject();
				jo.put("jcvi_projects", ja);

				Object projects = jo.get("jcvi_projects");

				String genomeNameKey = "Organism";
				String organismTypeKey = "Superkingdom";
				String taxonIdKey = "Taxonomy ID";
				String statusKey = "Sample Status";
				String sampleAcquisitionDateKey = "";//JCVI doesn't provide this info
				String projectIdKey = "Project ID";

				int jsonArrayLength = ja.length();
				ArrayList<PPGenomeInfo> genomeList = new ArrayList<PPGenomeInfo>(jsonArrayLength);

				//start at 1, because the first element is something other than genome info
				for(int i = 1; i < jsonArrayLength; i++) {
					String genomeName = "";
					String organismType = "";
					String taxonId = "";
					String status= "";
					String sampleAcquisitionDate = "";
					String projectId = "";

					JSONObject element = ja.getJSONObject(i);

					if(element.has(genomeNameKey)) {
						String nameFull = element.getString(genomeNameKey);
						//Genome name is the display text of an anchor tag, so need to parse it out.
						String[] nameParts = nameFull.split("[<>]");
						if(nameParts.length > 2) {
							genomeName = nameParts[2].trim();
						}
					}

					if(element.has(organismTypeKey)) {
						organismType = element.getString(organismTypeKey).trim();
					} 

					if(element.has(taxonIdKey)) {
						String taxonFull = element.getString(taxonIdKey);
						//Taxon ID is the display text of an anchor tag, so need to parse it out
						String[] taxonParts = taxonFull.split("[<>]");
						if(taxonParts.length > 2) {
							taxonId = taxonParts[2];
						} 
					}

					if(element.has(statusKey)) {
						status = element.getString(statusKey);
					}

					PPGenomeInfo genome = new PPGenomeInfo();
					genome.setGenomeName(genomeName);
					genome.setOrganismType(organismType);
					genome.setBRC(typeToBRC.get(organismType));
					genome.setTaxId(taxonId);
					genome.setStatus(status.trim());
					genome.setSampleAcquisitionDate(sampleAcquisitionDate);
					genome.setTags(dataSource.getTags());
					genome.setType(dataSource.getType());
					genome.setSequencingCenter(JCVI_SHORT);

					if(genomeName != null) {
						//try to determine genus, species, and strain from the name
						//assume first token is genus, second token is species, and subsequent tokens are strain
						String[] tokens = genomeName.split("\\s+");
						String genus = "";
						String species = "";
						String strain = "";
						if(tokens.length > 0) {
							genus = tokens[0];
						}
						if(tokens.length > 1) {
							species = tokens[1];
						}
						if(tokens.length > 2) {
							strain = tokens[2];
						}
						for(int j = 3; j < tokens.length; j++) {
							strain = strain + " " + tokens[j];
						}

						genome.setGenus(genus);
						genome.setSpecies(species);
						genome.setStrain(strain);

					}

					expandGenomeInfo(genome);
					genomeList.add(genome);
				}

				PPGenomeInfo[] genomes = new PPGenomeInfo[genomeList.size()];
				genomeList.toArray(genomes);
				dataSource.setDataSets(genomes);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private void parseBroadMetadata(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0];
		String locationType = dataSource.getLocationType();
		if(locationType.equalsIgnoreCase("xls url")) {
			//this is an xls file, so we need to download it to a local location
			//and then parse it
			try {
				File tempBroadFile = File.createTempFile("broad_project_status_", ".xls");
				tempBroadFile.deleteOnExit();
				String localXLSName = tempBroadFile.getAbsolutePath();
				System.out.println("writing broad metadata to local file: " + localXLSName);
				PathPortUtilities.downloadDataURLToFile(location, localXLSName);

				int genomeNameIndex = 1;
				int organismTypeIndex = 2;
				int taxonIdIndex = 7;
				int statusIndex = 4;
				int sampleAcquisitionIndex = 3;
				String genomeName = "";
				String organismType = "";
				String taxonId = "";
				String status= "";
				String sampleAcquisitionDate = "";

				InputStream inp = new FileInputStream(localXLSName);
				HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
				HSSFSheet sheet =  wb.getSheetAt(0);
				int lastRow = sheet.getLastRowNum();
				lastRow++;
				HSSFRow header = sheet.getRow(0);
				int cellCount = header.getPhysicalNumberOfCells();

				ArrayList<PPGenomeInfo> genomeList = new ArrayList<PPGenomeInfo>(lastRow);
				for(int i = 1; i < lastRow; i++) {
					HSSFRow row = sheet.getRow(i);

					Cell cell;
					cell = row.getCell(genomeNameIndex); 
					if(cell != null) {
						genomeName = cell.toString().trim();
					}
					cell = row.getCell(organismTypeIndex); 
					if(cell != null) {
						organismType = cell.toString().trim();
					}
					cell = row.getCell(taxonIdIndex); 
					if(cell != null) {
						taxonId = cell.toString().trim();
						//the values in this cell are being treated as floating point numbers,
						//so they are getting a .0 appended. Remove this if it is present
						taxonId = taxonId.replaceAll("\\.0", "");
					}
					cell = row.getCell(statusIndex); 
					if(cell != null) {
						status = cell.toString().trim();
					}
					cell = row.getCell(sampleAcquisitionIndex); 
					if(cell != null) {
						sampleAcquisitionDate = cell.toString().trim();
					}

					PPGenomeInfo genome = new PPGenomeInfo();
					genome.setGenomeName(genomeName);
					genome.setOrganismType(organismType);
					genome.setBRC(typeToBRC.get(organismType));
					genome.setTaxId(taxonId);
					genome.setStatus(status.trim());
					genome.setSampleAcquisitionDate(sampleAcquisitionDate);
					genome.setTags(dataSource.getTags());
					genome.setType(dataSource.getType());
					genome.setSequencingCenter(BROAD_SHORT);

					if(genomeName != null) {
						//try to determine genus, species, and strain from the name
						//assume first token is genus, second token is species, and subsequent tokens are strain
						String[] tokens = genomeName.split("\\s+");
						String genus = "";
						String species = "";
						String strain = "";
						if(tokens.length > 0) {
							genus = tokens[0];
						}
						if(tokens.length > 1) {
							species = tokens[1];
						}
						if(tokens.length > 2) {
							strain = tokens[2];
						}
						for(int j = 3; j < tokens.length; j++) {
							strain = strain + " " + tokens[j];
						}

						genome.setGenus(genus);
						genome.setSpecies(species);
						genome.setStrain(strain);
					}

					expandGenomeInfo(genome);
					genomeList.add(genome);
				}

				PPGenomeInfo[] genomes = new PPGenomeInfo[genomeList.size()];
				genomeList.toArray(genomes);
				dataSource.setDataSets(genomes);

			} catch(IOException ioe) {
				ioe.printStackTrace();
			}

		}
	}


	private void parseIGSMetadata(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0];
		String xmlDoc = PathPortUtilities.getContentsOfURL(location);

		if(xmlDoc != null) {
			XMLTreeElement fullDoc = new XMLTreeElement(xmlDoc);

			XMLTreeElement[] projectEls = fullDoc.getChildrenOfType(PROJECT_TAG);

			ArrayList<PPGenomeInfo> genomes = new ArrayList<PPGenomeInfo>();
			for(int i = 0; i < projectEls.length; i++) {
				XMLTreeElement whitePaperTitle = projectEls[i].getChildOfType(WHITEPAPER_TITLE_TAG);
				String wpTitle = whitePaperTitle.getContent().trim();
				String convertedTitle = wpTitle.replaceAll("\\s+", "_").toLowerCase();
				String wpDetailURL = IGS_DETAIL_URL_PREFIX + convertedTitle + IGS_DETAIL_URL_SUFFIX;
				PPGenomeInfo[] wpGenomes = loadIGSWhitepaperDetails(wpDetailURL);
				genomes.ensureCapacity(genomes.size() + wpGenomes.length);
				for(int j = 0; j < wpGenomes.length; j++) {
					wpGenomes[j].setTags(dataSource.getTags());
					wpGenomes[j].setType(dataSource.getType());
					expandGenomeInfo(wpGenomes[j]);
					genomes.add(wpGenomes[j]);
				}
			}
			PPGenomeInfo[] allSourceGenomes = new PPGenomeInfo[genomes.size()];
			genomes.toArray(allSourceGenomes);
			dataSource.setDataSets(allSourceGenomes);
		}

	}

	private PPGenomeInfo[] loadIGSWhitepaperDetails(String detailsURL) {
		PPGenomeInfo[] r = null;
		String detailsDoc = PathPortUtilities.getContentsOfURL(detailsURL);
		String spName = detailsURL.substring(detailsURL.lastIndexOf('/')+1);
//		try {
//			System.out.println(spName);
//			System.out.println(detailsDoc);
//			FileWriter fw = new FileWriter(spName);
//			fw.write(detailsDoc);
//			fw.flush();
//			fw.close();
//		} catch(IOException ioe) {
//			ioe.printStackTrace();
//		}

		XMLTreeElement fullDoc = new XMLTreeElement(detailsDoc);
		XMLTreeElement[] projects = fullDoc.getChildrenOfType(PROJECT_TAG);
		ArrayList<PPGenomeInfo> genomes = new ArrayList<PPGenomeInfo>(projects.length);

		for(int i = 0; i < projects.length; i++) {
			PPGenomeInfo genome = new PPGenomeInfo();
			genome.setSequencingCenter(IGS_SHORT);

			XMLTreeElement nameEl = projects[i].getChildOfType(SPECIES_NAME_TAG);
			if(nameEl != null && nameEl != XMLTreeElement.NO_SUCH_ELEMENT) {
				String name = nameEl.getContent();
				genome.setGenomeName(name);

				if(name != null) {
					//try to determine genus, species, and strain from the name
					//assume first token is genus, second token is species, and subsequent tokens are strain
					String[] tokens = name.split("\\s+");
					String genus = "";
					String species = "";
					String strain = "";
					if(tokens.length > 0) {
						genus = tokens[0];
					}
					if(tokens.length > 1) {
						species = tokens[1];
					}
					if(tokens.length > 2) {
						strain = tokens[2];
					}
					for(int j = 3; j < tokens.length; j++) {
						strain = strain + " " + tokens[j];
					}

					genome.setGenus(genus);
					genome.setSpecies(species);
					genome.setStrain(strain);
				}
			}				

			XMLTreeElement typeEl = projects[i].getChildOfType(ORGANISM_TAG);
			if(typeEl != null && typeEl != XMLTreeElement.NO_SUCH_ELEMENT) {
				String organismType = typeEl.getContent().trim();
				genome.setOrganismType(organismType);
				genome.setBRC(typeToBRC.get(organismType));
			}

			XMLTreeElement taxonEl = projects[i].getChildOfType(TAXONOMY_TAG);
			if(taxonEl != null && taxonEl != XMLTreeElement.NO_SUCH_ELEMENT) {
				genome.setTaxId(taxonEl.getContent().trim());
			}

			XMLTreeElement statusEl = projects[i].getChildOfType(STATUS_TAG);
			if(statusEl != null && statusEl != XMLTreeElement.NO_SUCH_ELEMENT) {
				genome.setStatus(statusEl.getContent().trim());
			}

			XMLTreeElement acquisitionDateEl = projects[i].getChildOfType(ACQUISITION_TAG);
			if(acquisitionDateEl != null && acquisitionDateEl != XMLTreeElement.NO_SUCH_ELEMENT) {
				genome.setSampleAcquisitionDate(acquisitionDateEl.getContent().trim());
			}

			genomes.add(genome);
		}

		r = new PPGenomeInfo[genomes.size()];
		genomes.toArray(r);
		return r;
	}


	private void parseEuPathDBSequenceInfo(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0];
		String xmlDoc = PathPortUtilities.getContentsOfURL(location);

		//each <record> element describes one genome.
		XMLTreeElement fullDoc = new XMLTreeElement(xmlDoc);
		XMLTreeElement[] records = fullDoc.getDescendantsOfType(RECORD_TAG);

		String genomeName = null;
		String gid = null;
		String taxId = null;
		String genus = null;
		String species = null;
		String strain = null;
		String fastaURL = null;
		String gffURL = null;
		String genomeVersion = null;

		ArrayList<PPDataSet> dataSetList = new ArrayList<PPDataSet>(records.length);

		for(int i = 0; i < records.length; i++) {
			XMLTreeElement[] recordDetails = records[i].getChildren();

			for(int j = 0; j < recordDetails.length; j++) {
				String name = (String)recordDetails[j].getAttribute("name");
				if(name.equalsIgnoreCase("Taxon_ID")) {
					taxId = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("Genus")) {
					genus = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("Species")) {
					species = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("Strain")) {
					strain = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("URLGenomeFasta")) {
					fastaURL = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("URLgff")) {
					gffURL = recordDetails[j].getContent();
				} else if(name.equalsIgnoreCase("Genome_Version")) {
					genomeVersion = recordDetails[j].getContent();
				}
			}

			genomeName = genus + " " + species;
			if(strain != null) {
				genomeName += " " + strain;
			}
			PPGenomeInfo genomeInfo = new PPGenomeInfo();
			genomeInfo.setGenomeName(genomeName);
			genomeInfo.setGid(gid);
			genomeInfo.setTaxId(taxId);
			genomeInfo.setGenus(genus);
			genomeInfo.setSpecies(species);
			genomeInfo.setStrain(strain);
			genomeInfo.setFastaURL(fastaURL);
			genomeInfo.setGffURL(gffURL);
			genomeInfo.setGenomeVersion(genomeVersion);

			genomeInfo.setTags(dataSource.getTags());
			genomeInfo.setType(dataSource.getType());

			dataSetList.add(genomeInfo);
		} 

		PPDataSet[] dataSets = new PPDataSet[dataSetList.size()];
		dataSetList.toArray(dataSets);
		dataSource.setDataSets(dataSets);
	}

	private void parsePATRICSequenceInfo(PPDataSource dataSource) {
		String location = dataSource.getLocationURLs()[0];
		String xmlDoc = PathPortUtilities.getContentsOfURL(location);

		//each <doc> element describes one genome.
		XMLTreeElement fullDoc = new XMLTreeElement(xmlDoc

		);

		//within <doc>, <int name="gid"> has the genome info id,
		//which is a PATRIC unique identifier for the genome.
		//within <doc>, <str name="genome_name"> has the genome name.
		//replace special characters with underscores to get the 
		//directory name for the genome at the download site.

		XMLTreeElement[] docs = fullDoc.getDescendantsOfType("doc");

		String genomeName = null;
		String genus = null;
		String gid = null;
		String taxId = null;
		String species = null;
		String strain = "";
		int length = 0;

		ArrayList<PPDataSet> dataSetList = new ArrayList<PPDataSet>(docs.length);

		for(int i = 0; i < docs.length; i++) {
			boolean rastAnnotationsAvailable = false;
			XMLTreeElement[] docDetails = docs[i].getChildren();

			for(int j = 0; j < docDetails.length; j++) {
				String name = (String)docDetails[j].getAttribute("NAME");
				if(name.equals("gid")) {
					gid = docDetails[j].getContent();
				} else if(name.equals("genome_name")) {
					genomeName = docDetails[j].getContent();
				} else if(name.equals("ncbi_tax_id")) {
					taxId = docDetails[j].getContent();
				} else if(name.equals("rast_cds")) {
					String rastCDS = docDetails[j].getContent();
					if(rastCDS != null && rastCDS.length() > 0 && !rastCDS.equals("0")) {
						rastAnnotationsAvailable = true;
					}
				} else if(name.equals("length")) {
					length = Integer.parseInt(docDetails[j].getContent().trim());
				}

				//try to get genus name from genomeName
				if(genomeName != null) {
					int index = 0;
					String[] parts = genomeName.split("\\s+");
					if(parts.length > 1 && parts[0].equalsIgnoreCase("candidatus")) {
						index++;
					}
					genus = parts[index];
					index++;
					if(parts.length > index) {
						species = parts[index];
					}
					index++;
					if(parts.length > index) {
						strain = parts[index];
					}
					for(index++; index < parts.length; index++) {
						strain = strain + " " + parts[index];
					}
				}


			}
			String genomePage = PATRIC_GENOME_PAGE_URL + taxId;
			String fileName = genomeNameToFileName(genomeName);

			PPGenomeInfo genomeInfo = new PPGenomeInfo();
			genomeInfo.setGenomeName(genomeName);
			genomeInfo.setGid(gid);
			genomeInfo.setTaxId(taxId);
			genomeInfo.setGenus(genus);
			genomeInfo.setSpecies(species);
			genomeInfo.setStrain(strain);
			genomeInfo.setGenomePageURL(genomePage);
			//			genomeInfo.setFastaURL(fastaURL);
			//			genomeInfo.setGffURL(gffURL);

			genomeInfo.setTags(dataSource.getTags());
			genomeInfo.setType(dataSource.getType());

			dataSetList.add(genomeInfo);

		} 

		PPDataSet[] dataSets = new PPDataSet[dataSetList.size()];
		dataSetList.toArray(dataSets);
		dataSource.setDataSets(dataSets);
	}

	/**
	 * Removes problematic characters and replaces them with 
	 * underscores.
	 * 
	 * @param genomeName
	 * @return
	 */
	private String genomeNameToFileName(String genomeName) {
		String r = null;
		//some characters need to be replaced with underscore 
		r = genomeName.replaceAll("[\\s\\(\\)]+", "_");

		//some characters need to be removed, and not replaced with anything
		r = r.replaceAll("_$", "");
		r = r.replaceAll("\\/", "");
		r = r.replaceAll("\\.", "");

		//it looks like patric is replacing ':' with '-'
		r = r.replaceAll(":", "-");

		return r;
	}


	private void parseVectorbaseSequenceInfo(String xmlData) {
		XMLTreeElement fullDoc = new XMLTreeElement(xmlData);

		XMLTreeElement[] genomeElements = fullDoc.getChildrenOfType("genome");
		for(int i = 0; i < genomeElements.length; i++) {
			String genomeName = genomeElements[i].getChildOfType("display_name").getContent().trim();
			String taxonId = genomeElements[i].getChildOfType("tax_id").getContent().trim();
			String fastaURL = genomeElements[i].getChildOfType("fasta_url").getContent().trim();
			String gtfURL = genomeElements[i].getChildOfType("gtf_url").getContent().trim();
		}
	}

	public static void writeTaxonIdToLineageMap(String fileName) {
		if(taxonIdToLineage != null) {
			try {
				FileWriter fw = new FileWriter(fileName);
				String[] keys = new String[taxonIdToLineage.size()];
				taxonIdToLineage.keySet().toArray(keys);

				for(int i = 0; i < keys.length; i++) {
					String lineage = taxonIdToLineage.get(keys[i]);
					String line = keys[i] + "\t" + lineage + "\n";
					System.out.print(line);
					fw.write(line);
				}
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getLineage(String taxId) {
		String r = null;

		//first check in the taxonIdToLineage map
		if(taxonIdToLineage == null) {
			taxonIdToLineage = new HashMap<String, String>();
		}
		r = taxonIdToLineage.get(taxId);

		if(r == null) {
			//if it's not in the map, get taxonomy info from ncbi
			String taxURL = ncbiTaxonomyURL + taxId;
			String taxXMLString = AppletUtilities.getContentsOfURL(taxURL);

			try {
				XMLTreeElement taxXML = new XMLTreeElement(taxXMLString);
				XMLTreeElement[] descendants = taxXML.getDescendantsOfType("Lineage");
				if(descendants.length > 0) { 
					String lineage = descendants[0].getContent();
					r = descendants[0].getContent();

					taxonIdToLineage.put(taxId, r);
				}
			} catch(XMLParseException xpe) {}
		}
		return r;
	}


}
