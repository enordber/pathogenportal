package edu.vt.vbi.ci.pathport;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import edu.vt.vbi.ci.util.parse.XMLTreeElement;

public class PathPortData {

	private static final String DATA_SOURCE_URL = "http://staff.vbi.vt.edu/enordber/pathport/dev_data_sources.xml";
//	private static final String DATA_SOURCE_URL = "http://staff.vbi.vt.edu/enordber/data/data_sources.xml";
//	private static final String DATA_SOURCE_URL = "http://127.0.0.1/data_sources4.xml";

	/*
	 * Data Sources xml tags
	 */
	private static final String DATA_SOURCES_TAG = "data_sources";
	private static final String DATA_SOURCE_TAG = "data_source";
	private static final String NAME_TAG = "name";
	private static final String PROVIDER_TAG = "provider";
	private static final String TYPE_TAG = "type";
	private static final String TAG_TAG = "tag";
	private static final String LOCATION_TAG = "location";
	private static final String LOCATION_TYPE_TAG = "location_type";
	private static final String FAILOVER_LOCATION_TAG = "failover_location";
	private static final String REFRESH_SECONDS_TAG = "refresh_seconds";

	/*
	 * Type constants
	 */
	public static final String RSS = "RSS";
	public static final String EXPERIMENT_DATA = "Experiment Data";
	public static final String GOOGLE_SITES = "Google Sites";
	public static final String GENOME_DATA = "Genome Data";
	public static final String HTML = "HTML";

	/*
	 * Some metadata tags
	 */
	public static final String EUPATHDB = "eupathdb";
	public static final String IRD = "ird";
	public static final String PATHOGENPORTAL = "pathogenportal";
	public static final String PATRIC = "patric";
	public static final String VECTORBASE = "vectorbase";
	public static final String VIPR = "vipr";
	public static final String NEWS = "news";
	public static final String RELEASES = "releases";
	public static final String NIAID = "niaid";
	public static final String BRC = "brc";
	public static final String ANNOUNCEMENT = "announcement";
	public static final String PUBLICATIONS = "publications";
	public static final String DIRECTORY = "directory";
	public static final String GUIDE = "guide";
	public static final String TRANSCRIPTOME = "transcriptome";
	public static final String GENOME = "genome";
	public static final String PROTEOME = "proteome";
	public static final String GOOGLE_SITES_URL = "google sites url";
	public static final String LEGACY = "legacy";
	public static final String TOOL = "tool";
	public static final String HOST = "host";
	public static final String PATHOGEN = "pathogen";
	public static final String ENVIRONMENT = "environment";
	public static final String PRESENTATIONS = "Presentations";
	public static final String STRUCTURE = "structure";
	public static final String INTERACTOME = "interactome";
	public static final String REAGENT = "reagent";
	public static final String CLONE = "Clone";

	/*
	 * Taxonomy constants
	 */
	private static final String BACTERIA = "Bacteria";
	private static final String EUKARYOTA = "Eukaryota";
	private static final String VIRUSES = "Viruses";

	/*
	 * BRC Display Name constants
	 */
	private static final String EUPATHDB_DISPLAY = "EuPathDB";
	private static final String IRD_DISPLAY = "IRD";
	private static final String PATRIC_DISPLAY = "PATRIC";
	private static final String VECTORBASE_DISPLAY = "VectorBase";
	private static final String VIPR_DISPLAY = "ViPR";

	private static PPDataSource[] dataSources;
	private static HashMap<String, PPDataSourceLoader> typeToDataSourceLoaders;
	private static HashMap<String, ArrayList<PPDataSource>> tagToDataSources;
	private static HashMap<String, ArrayList<PPDataSource>> typeToDataSources;
	private static HashMap<String, PPDataSource> nameToDataSource;

	private static Thread[] reloaderThreads;

	private static PPRSS rss = new PPRSS();
	static {
		init();
	}

	public static void main(String[] args) {
		doTests();
	}

	private static int getDataSetCount(String tag) {
		int r = 0;
		PPDataSet[] dataSets = PathPortData.getDataWithTag(tag);
		if(dataSets != null) {
			r = dataSets.length;
		}
		return r;
	}


	public static void init() {
		System.out.println("PathPortData.init(); source: " + DATA_SOURCE_URL);
		if(dataSources == null) {
			try {
				loadDataSources();
				linkTypesToDataSources();
				linkTagsToDataSources();
				integrateDataSets();
				//				PPGenomeLoader.writeTaxonIdToLineageMap("taxon_to_lineage.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}

	private static void doTests() {
		init();
		long before = System.currentTimeMillis();
		System.out.println("Announcement data sets: " + getDataWithTag(ANNOUNCEMENT).length);
		//		System.out.println("genome data sets: " + getDataWithTag(GENOME).length);
		//		System.out.println("transcriptome data sets: " + getDataWithTag(TRANSCRIPTOME).length);
		System.out.println("proteome data sets: " + getDataWithTag(PROTEOME).length);
		System.out.println("host data sets: " + getDataWithTag(HOST).length);
		System.out.println("pathogen data sets: " + getDataWithTag(PATHOGEN).length);
		System.out.println("enviroment data sets: " + getDataWithTag(ENVIRONMENT).length);
		//		System.out.println(getDataFromSource("PFGRC Software")[0].getHTML());
		//		System.out.println(getDataFromSource("PFGRC Array Designs")[0].getHTML());
		//		System.out.println(getDataFromSource("PFGRC Microarray Protocols")[0].getHTML());
		//		System.out.println("Mouse guide: ");
		//		System.out.println(getDataFromSource("Mouse Model Strain Selection Guide")[0].getHTML());
		//		System.out.println(getDataFromSource("Related NIAID-Funded Resources")[0].getHTML());
		long after = System.currentTimeMillis();
		long elapsed = after - before;
		System.out.println();
		System.out.println("time to query items: " + elapsed + " ms");

		System.out.println("transcriptome data sets: " + getDataSetCount("transcriptome"));
		System.out.println("genome data sets: " + getDataSetCount("genome"));


		//check genome facets
		PPDataSet[] gscGenomes = getDataWithAllTags(new String[]{"gscid", "genome"});
		System.out.println("gscGenomes: " + gscGenomes.length);
		
		HashMap<String, HashMap<String, ExtendedBitSet>> facetMap = FacetCutter.getFacetMap(gscGenomes);
		String[] keys = new String[facetMap.size()];
		facetMap.keySet().toArray(keys);
		Arrays.sort(keys);
		System.out.println("genome facets: ");
		for(int i = 0; i < keys.length; i++) {
			System.out.println("\t"+ keys[i]);
		}

		//		String[] allTypes = getAvailableTypes();
		//		System.out.println("types: ");
		//		for(int i = 0; i < allTypes.length; i++) {
		//			System.out.println("\t" + allTypes[i]);
		//		}
		//		System.out.println();
		//		String[] allTags = getAvailableTags();
		//		System.out.println("tags:");
		//		for(int i = 0; i < allTags.length; i++) {
		//			System.out.println("\t" + allTags[i]);
		//		}

		//		PPDataSet[] genomeDataSets = getDataOfType(GENOME_DATA);
		//		System.out.println("genomeDataSets: " + genomeDataSets.length);
		//		for(int i = 0; i < 10 && i < genomeDataSets.length; i++) {
		//			System.out.println(genomeDataSets[i].getHTML());
		//		}

		//		PPDataSet[] rssItems = getDataOfType(RSS);
		//		System.out.println("rssItems: " + rssItems.length);
		//		for(int i = 0; i < rssItems.length; i++) {
		//			String[] categories = rssItems[i].getFacetValues("Category");
		//			for(int j = 0; j < categories.length; j++) {
		//				System.out.println(i + ": " + categories[j]);
		//			}
		//		}

		//		PPDataSet[] hostResponse = getDataFromSource("Host Response Data Sets");
		//		System.out.println("hostReponse: " + hostResponse.length);
		//		for(int i = 0; i < hostResponse.length; i++) {
		//			String[] tags = hostResponse[i].getTags();
		//			System.out.println("host response data set " + i + " tags: ");
		//			for(int j = 0; j < tags.length; j++) {
		//				System.out.println("\t" + tags[j]);
		//			}
		//		}

		//		PPDataSet[] transcriptomeDataSets = getDataWithTag(TRANSCRIPTOME);
		//		System.out.println("transcriptome data sets: " + transcriptomeDataSets.length);
		//		for(int i = 0; i < transcriptomeDataSets.length; i++) {
		//			try {
		//				System.out.println("data set "+ i + " id: " + ((Experiment)transcriptomeDataSets[i]).getId());
		//			} catch(ClassCastException cce) {
		//			}
		//		}


		//		HashMap<String, HashMap<String, ExtendedBitSet> > rssFacetMap = Cutter.getFacetMap(rssItems);
		//		String[] facetNames = new String[rssFacetMap.size()];
		//		rssFacetMap.keySet().toArray(facetNames);
		//		System.out.println("facetMap facetNames: " + facetNames.length);
		//		for(int i = 0; i < facetNames.length; i++) {
		//			HashMap<String, ExtendedBitSet> facetValueMap = rssFacetMap.get(facetNames[i]);
		//			String[] facetValues = new String[facetValueMap.size()];
		//			facetValueMap.keySet().toArray(facetValues);
		//			System.out.println(facetNames[i] + " (" + facetValues.length + ")");
		//			for(int j = 0; j < facetValues.length; j++) {
		//				System.out.println("\t" + facetValues[j] + " (" + facetValueMap.get(facetValues[j]).cardinality() + ")");
		//			}
		//		}

		//get Category:Releases and Source:IRD
		//		ExtendedBitSet releases = Cutter.getMemberSetList(rssFacetMap, "Category", "Data Releases & Updates");
		//		System.out.println("releases: " + releases.cardinality());
		//		ExtendedBitSet ird = Cutter.getMemberSetList(rssFacetMap, "Source", new String[]{"IRD", "ViPR"});
		//		System.out.println("IRD: " + ird.cardinality());
		//		ExtendedBitSet irdReleases = releases.getAnd(ird);
		//		System.out.println("IRD Releases: " + irdReleases.cardinality());
		//		int[] irdReleasesIndices = irdReleases.getSetValues();
		//		for(int i = 0; i < irdReleasesIndices.length; i++) {
		//			System.out.println(rssItems[irdReleasesIndices[i]]);
		//		}

		System.exit(0);
	}

	/**
	 * Data Sets loaded from different sources may be able to merge, or to 
	 * share information with each other. This is the method that orchestrates
	 * that.
	 */
	private static void integrateDataSets() {
		//Genome info from GSCs can be augmented with genome info from PATRIC
		//(and eventually from other BRCs)

		//load a HashMap with PATRIC genome info, using the genome name as the key
		HashMap<String, PPGenomeInfo> patricGenomeInfo = new HashMap<String, PPGenomeInfo>();
		PPDataSet[] patricGIs = getDataWithAllTags(new String[]{"genome", "patric"});
		for(int i = 0; i < patricGIs.length; i++) {
			PPGenomeInfo ppgi = (PPGenomeInfo)patricGIs[i];
			patricGenomeInfo.put(ppgi.getGenomeName(), ppgi);
		}

		int updateCount = 0;
		int notUpdated = 0;
		//for each genome from a GSC, see if there is info in the patricGenomeInfo map.
		PPDataSet[] gscGenomes = getDataWithAllTags(new String[]{"genome","gscid"});
		for(int i = 0; i < gscGenomes.length; i++) {
			PPGenomeInfo ppgi = (PPGenomeInfo)gscGenomes[i];
			String genomeName = ppgi.getGenomeName();

			PPGenomeInfo patricGI = patricGenomeInfo.get(genomeName);
			if(patricGI != null) {
				//				System.out.println("found PATRIC genome info for " + genomeName);
				//if PATRIC info is found, add it to the info from the GSC
				ppgi.addDataFrom(patricGI);
				updateCount++;
			} else {
				String type = ppgi.getOrganismType();
				if(type.equalsIgnoreCase("bacteria")) {
					//					System.out.println("bacterial genome with no PATRIC match: " + genomeName + " status: " + ppgi.getStatus());
					notUpdated++;
				}
			}
		}

		//some genomes don't have an Organism type assigned at this point.
		//For those, see if they have a taxonomy id. If they do, then use 
		//NCBI web service (via PPGenomeLoader) to get Lineage info and
		//see if organism type can be inferred.
		for(int i = 0; i < gscGenomes.length; i++) {
			PPGenomeInfo ppgi = (PPGenomeInfo)gscGenomes[i];
			String organismType = ppgi.getOrganismType();
			String organismClass = null;
			String organismBRC = null;
			String taxonId = ppgi.getTaxId();
			if(taxonId != null && taxonId != PPGenomeInfo.NOT_PROVIDED) {
				//this genome has a taxon id, but no organism type - 
				//get the Lineage info
				String lineage = PPGenomeLoader.getLineage(taxonId);
				if(lineage != null) {
					String[] taxa = lineage.split(";");
					if(taxa.length > 3) {
						organismClass = taxa[3];
					} else if(taxa.length > 0) {
						organismClass = taxa[taxa.length-1];							
					}

					//use lineage info to determine BRC this genome should belong to
					if(taxa.length > 0 && taxa[0].trim().equalsIgnoreCase(VIRUSES)) {
						organismBRC = VIPR_DISPLAY;
					} else if(taxa.length > 1) {
						String kingdom = taxa[1].trim();
						if(kingdom.equalsIgnoreCase(BACTERIA)) {
							organismBRC = PATRIC_DISPLAY;
						} else if(kingdom.equalsIgnoreCase(EUKARYOTA)) {
							if(lineage.matches(".*Arthropoda.*")) {
								organismBRC = VECTORBASE_DISPLAY;
							} else if(!lineage.matches(".*Opisthokonta.*") && !lineage.matches(".*Viridiplantae.*")){
								//I think Eupath has the Eukaryotes that are not Opisthokonts or plants.
								//There are probably other constraints, but I don't know what they
								//are now. So, for now, if it's Eukaryota, but not Opisthokonta, 
								//Viridiplantae, or Arthropoda, then it's a Eupath genome.
								organismBRC = EUPATHDB_DISPLAY;
							}
						}					
					}

					if(organismClass != null) {
						ppgi.setOrganismType(organismClass);
					}

					if(organismBRC != null) {
						ppgi.setBRC(organismBRC);
					}
				}

			}
		}
	}

	public static PPDataSet[] getDataFromSource(String sourceName) {
		PPDataSet[] r = null;
		//get DataSource with the specified name
		PPDataSource source = nameToDataSource.get(sourceName);
		//get DataSets from the DataSource
		if(source != null) {
			r = source.getDataSets();
		}
		return r;
	}

	/**
	 * Checks all sources for data of the specified type and returns
	 * a PPDataSet for each set found. 
	 * 
	 * The type should be one of the types defined in PPDataSet:
	 * 	PPDataSet.RSS_DATA
	 *  PPDataSet.EXPERIMENT_DATA
	 *  PPDataSet.GENOME_DATA
	 *  PPDataSet.GUIDES_DIRECTORIES
	 *  PPDataSet.CENTER_INFO
	 * 	
	 * @param type
	 * @return
	 */
	public static PPDataSet[] getDataOfType(String type) {
		PPDataSet[] r = null;
		ArrayList<PPDataSet> dsList = new ArrayList<PPDataSet>();
		PPDataSource[] typeSources = getDataSourcesOfType(type);
		for(int i = 0; i < typeSources.length; i++) {
			PPDataSet[] dataSets = typeSources[i].getDataSets();
			if(dataSets == null || dataSets.length == 0) {
				System.out.println("No data sets were returned by the data source " + typeSources[i].getName());
			} else {
				for(int j = 0; j < dataSets.length; j++) {
					dsList.add(dataSets[j]);
				} 
			}
		}

		r = new PPDataSet[dsList.size()];
		dsList.toArray(r);
		return r;
	}

	/**
	 * Returns the intersection of the data of each of the specified types.
	 * That is, a PPDataSet must match all of the specified types to be 
	 * included in the returned list.
	 * 
	 * @param types
	 * @return
	 */
	public static PPDataSet[] getDataOfAllTypes(String[] types) {
		PPDataSet[] r = null;
		return r;
	}

	public static PPDataSet[] getDataWithAllTags(String[] tags) {
		PPDataSet[] r = null;
		HashSet<PPDataSet> commonDataSets = new HashSet<PPDataSet>();
		if(tags != null && tags.length > 0) {
			PPDataSet[] tagSets = getDataWithTag(tags[0]);
			for(int i = 0; i < tagSets.length; i++) {
				commonDataSets.add(tagSets[i]);
			}

			for(int i = 1; i < tags.length; i++) {
				HashSet<PPDataSet> tagSet = new HashSet<PPDataSet>();
				tagSets = getDataWithTag(tags[i]);
				for(int j = 0; j < tagSets.length; j++) {
					tagSet.add(tagSets[j]);
				}

				//only keep the common data sets
				commonDataSets.retainAll(tagSet); 
			}
		}

		r = new PPDataSet[commonDataSets.size()];
		commonDataSets.toArray(r);
		return r;
	}

	public static PPDataSet[] getDataWithTag(String tag) {
		PPDataSet[] r = null;
		ArrayList<PPDataSet> dsList = new ArrayList<PPDataSet>();
		PPDataSource[] tagSources = getDataSourcesWithTag(tag);
		for(int i = 0; i < tagSources.length; i++) {
			PPDataSet[] dataSets = tagSources[i].getDataSets();
			if(dataSets != null) {
				for(int j = 0; j < dataSets.length; j++) {
					if(dataSets[j].hasTag(tag)) {
						dsList.add(dataSets[j]);
					}
				}
			}
		}

		r = new PPDataSet[dsList.size()];
		dsList.toArray(r);
		return r;
	}

	public static PPDataSet[] getDataOfTypeWithTag(String type, String tag) {
		PPDataSet[] r = null;
		PPDataSet[] typeItems = getDataOfType(type);
		PPDataSet[] tagItems = getDataWithTag(tag);

		HashSet<PPDataSet> typeSet = new HashSet<PPDataSet>();
		for(int i = 0; i < typeItems.length; i++) {
			typeSet.add(typeItems[i]);
		}

		HashSet<PPDataSet> tagSet = new HashSet<PPDataSet>();
		for(int i = 0; i < tagItems.length; i++) {
			tagSet.add(tagItems[i]);
		}

		typeSet.retainAll(tagSet);
		r = new PPDataSet[typeSet.size()];
		typeSet.toArray(r);
		Arrays.sort(r);
		return r;
	}

	/**
	 * Returns a list of all Types that are available
	 * @return
	 */
	public static String[] getAvailableTypes() {
		String[] r = null;
		r = new String[typeToDataSources.keySet().size()];
		typeToDataSources.keySet().toArray(r);
		return r;
	}

	public static String[] getAvailableTags() {
		String[] r = null;
		r = new String[tagToDataSources.keySet().size()];
		tagToDataSources.keySet().toArray(r);
		return r;
	}

	static void reloadDataSource(PPDataSource dataSource) {
		String type = dataSource.getType();
		String name = dataSource.getName();
		System.out.println("PathPortData.reloadDataSource() " + name);
		//if there is a Data Source Loader for this type, use it to load data from the data Source
		PPDataSourceLoader loader = typeToDataSourceLoaders.get(type);
		if(loader == null) {
			System.out.println("Trying to load the Data Source '" + name + "' with type '" + type + "', but there is no Data Source Loader for this type.");
		} else {
			loader.loadItemsFromSource(dataSource);	        		
		}	

		linkTypesToDataSources();
		linkTagsToDataSources();
	}

	private static void loadDataSources() throws IOException {
		//if any reloaderThreads exist and are running, stop them.
		if(reloaderThreads != null) {
			for(int i = 0; i  < reloaderThreads.length; i++) {
				if(reloaderThreads[i] != null) {
					reloaderThreads[i].interrupt();
				}
			}
		}
		ArrayList<Thread> reloaderThreadList = new ArrayList<Thread>();
		typeToDataSourceLoaders = new HashMap<String,PPDataSourceLoader>();
		typeToDataSourceLoaders.put(PPDataSet.RSS_DATA, new PPRSS());
		typeToDataSourceLoaders.put(PPDataSet.EXPERIMENT_DATA, new PPExperimentDataLoader());
		typeToDataSourceLoaders.put(PPDataSet.GOOGLE_SITES_DATA, new GoogleSitesLoader());
		typeToDataSourceLoaders.put(PPDataSet.GENOME_DATA, new PPGenomeLoader());
		typeToDataSourceLoaders.put(PPDataSet.CSV, new HostResponseDataSourceLoader());
		typeToDataSourceLoaders.put(PPDataSet.HTML, new StaticPageLoader());
		typeToDataSourceLoaders.put(PPDataSet.TAXONOMY_DATA, new PPGenomeLoader());

		nameToDataSource = new HashMap<String, PPDataSource>();

		String xmlDoc;
		xmlDoc = Jsoup.connect(DATA_SOURCE_URL).get().body().html().toString();

		XMLTreeElement dataSourcesEl = new XMLTreeElement(xmlDoc);
		XMLTreeElement[] dataSourceEls = dataSourcesEl.getChildrenOfType(DATA_SOURCE_TAG);

		String name;
		String type = null;
		String provider = "";
		String[] tags;
		String locationType;
		String[] locations;
		String[] failoverLocations;
		long refreshSeconds = -1;
		dataSources = new PPDataSource[dataSourceEls.length];
		for(int i = 0; i < dataSources.length; i++) {
			name = dataSourceEls[i].getChildOfType(NAME_TAG).getContent().trim();
			locationType = dataSourceEls[i].getChildOfType(LOCATION_TYPE_TAG).getContent();

			XMLTreeElement[] typeEls = dataSourceEls[i].getChildrenOfType(TYPE_TAG);
			if(typeEls != null && typeEls.length > 0) {
				type = typeEls[0].getContent().trim();
			}

			XMLTreeElement[] providerEls = dataSourceEls[i].getChildrenOfType(PROVIDER_TAG);
			if(providerEls != null && providerEls.length > 0) {
				provider = providerEls[0].getContent().trim();
			}

			XMLTreeElement[] tagEls = dataSourceEls[i].getChildrenOfType(TAG_TAG);
			tags = new String[tagEls.length];
			for(int j = 0; j < tags.length; j++) {
				tags[j] = tagEls[j].getContent().trim().toLowerCase();
			}

			XMLTreeElement[] locationEls = dataSourceEls[i].getChildrenOfType(LOCATION_TAG);
			locations = new String[locationEls.length];
			for(int j = 0; j < locations.length; j++) {
				locations[j] = locationEls[j].getContent();
			}

			XMLTreeElement[] failoverEls = dataSourceEls[i].getChildrenOfType(FAILOVER_LOCATION_TAG);
			failoverLocations = new String[failoverEls.length];
			for(int j = 0; j < failoverLocations.length; j++) {
				failoverLocations[j] = failoverEls[j].getContent();
			}

			XMLTreeElement[] refreshEls = dataSourceEls[i].getChildrenOfType(REFRESH_SECONDS_TAG);
			if(refreshEls != null && refreshEls.length > 0) {
				String refreshString = refreshEls[0].getContent().trim();
				try{
					refreshSeconds = Long.parseLong(refreshString); 
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}

			dataSources[i] = new PPDataSource();
			dataSources[i].setName(name);
			dataSources[i].setProvider(provider);
			dataSources[i].setType(type);
			dataSources[i].setTags(tags);
			dataSources[i].setLocationType(locationType);
			dataSources[i].setLocationURLs(locations);
			dataSources[i].setFailoverURLS(failoverLocations);

			if(refreshSeconds >= 0) {
				dataSources[i].setRefreshTimeSeconds(refreshSeconds);
			}

			nameToDataSource.put(name, dataSources[i]);
			//if there is a Data Source Loader for this type, use it to load data from the data Source
			PPDataSourceLoader loader = typeToDataSourceLoaders.get(type);
			if(loader == null) {
				System.out.println("Trying to load the Data Source '" + name + "' with type '" + type + "', but there is no Data Source Loader for this type.");
			} else {
				try {
					loader.loadItemsFromSource(dataSources[i]);	        		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			Thread reloaderThread = new Thread(new PPDataSourceReloader(dataSources[i]));
			reloaderThread.start();
			reloaderThreadList.add(reloaderThread);
		}

		reloaderThreads = new Thread[reloaderThreadList.size()];
		reloaderThreadList.toArray(reloaderThreads);
		System.out.println("data sources loaded: " + dataSources.length);
		System.out.println("reloaderThreads: " + reloaderThreads.length);
	}

	public static void reloadDataSources() {
		System.out.println("PathPortData.reloadDataSources() from " + DATA_SOURCE_URL);
		try {
			loadDataSources();
			linkTypesToDataSources();
			linkTagsToDataSources();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static PPDataSource[] getDataSourcesOfType(String type) {
		PPDataSource[] r = null;
		ArrayList<PPDataSource> dsList = typeToDataSources.get(type);
		if(dsList == null) {
			r = new PPDataSource[0];
		} else {
			r = new PPDataSource[dsList.size()];
			dsList.toArray(r);
		}

		return r;
	}

	private static PPDataSource[] getDataSourcesWithTag(String tag) {
		PPDataSource[] r = null;
		ArrayList<PPDataSource> dsList = tagToDataSources.get(tag.toLowerCase());
		if(dsList == null) {
			r = new PPDataSource[0];
		} else {
			r = new PPDataSource[dsList.size()];
			dsList.toArray(r);
		}

		return r;
	}

	private static void linkTypesToDataSources() {
		typeToDataSources = new HashMap<String, ArrayList<PPDataSource>>();

		for(int i = 0; i < dataSources.length; i++) {
			String type = dataSources[i].getType();
			//			System.out.println("linking data source with type " + type);
			ArrayList<PPDataSource> typedSources = typeToDataSources.get(type);
			if(typedSources == null) {
				typedSources = new ArrayList<PPDataSource>(1);
			}
			typedSources.ensureCapacity(typedSources.size()+1);
			typedSources.add(dataSources[i]);
			typeToDataSources.put(type, typedSources);
		}
	}

	private static void linkTagsToDataSources() {
		//		System.out.println(">PathPortData.linkTagsToDataSources()");
		tagToDataSources = new HashMap<String, ArrayList<PPDataSource>>();

		for(int i = 0; i < dataSources.length; i++) {
			String[] tags = dataSources[i].getTags();
			//			System.out.println("tags for DataSource: " + dataSources[i].getName());
			for(int j = 0; j < tags.length; j++) {
				//				System.out.println(tags[j]);
				ArrayList<PPDataSource> taggedSources = tagToDataSources.get(tags[j]);
				if(taggedSources == null) {
					taggedSources = new ArrayList<PPDataSource>(1);
				}
				taggedSources.add(dataSources[i]);
				taggedSources.trimToSize();
				tagToDataSources.put(tags[j], taggedSources);
			}
		}
	}
}