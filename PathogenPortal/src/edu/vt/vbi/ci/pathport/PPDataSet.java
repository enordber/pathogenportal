package edu.vt.vbi.ci.pathport;

import java.util.HashMap;

import edu.vt.vbi.ci.util.PathPortUtilities;

public class PPDataSet implements Comparable{

	public static final String RSS_DATA = "RSS";
	public static final String EXPERIMENT_DATA = "Experiment Data";
	public static final String GENOME_DATA = "Genome Data";
	public static final String GOOGLE_SITES_DATA = "Google Sites";
	public static final String GUIDES_DIRECTORIES = "Guides and Directories";
	public static final String CENTER_INFO = "Center Info";
	public static final String CSV = "csv";
	public static final String HTML = "html";
	public static final String TAXONOMY_DATA = "Taxonomy Data";

	private PPDataSource source;
	private String type;
	private String provider;
	private String[] tags;

	private HashMap<String, String> jsonMap;

	/*
	 * Key: String - facet name
	 * Value: String[] - facet values
	 */
	private HashMap<String, String[]> facetMap;

	/**
	 * Any PPData items contained by this data set
	 */
	private PPDataSet[] childData; 

	/**
	 * Any PPData items that contain this item.
	 */
	private PPDataSet parentData;

	public PPDataSource getDataSource() {
		return source;
	}

	public void setDataSource(PPDataSource source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		addKeyValuePair("type", type);
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
		//create json string for tags and add to jsonMap
		String tagsJSON = PathPortUtilities.getJSONString(tags);
		addKeyValuePair("tags", tagsJSON);
	}

	/**
	 * This can be overridden in subclasses to return HTML in cases where
	 * the DataSet already contains formatted HTML. Some DataSets will
	 * not support this, and the class using the DataSet must create 
	 * the HTML using the information in the DataSet.
	 * 
	 * @return
	 */
	public String getHTML() {
		return null;
	}

	/**
	 * Override in subclasses that can be sensibly sorted.
	 */
	public int compareTo(Object arg0) {
		return arg0.hashCode() - this.hashCode();
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;

		addKeyValuePair("provider", provider);
	}

	public boolean hasTag(String tag) {
		boolean r = false;
		if(tags != null) {
			for(int i = 0; !r && i < tags.length; i++) {
				r = tag.equals(tags[i]);
			}
		}
		return r;
	}

	/**
	 * Sets the single facetValue for the specified facetName. Any previous
	 * values for this facetName are removed. To add a new facetValue without
	 * removing existing facetValues, use setFacetValue(). 
	 * 
	 * @param facetName
	 * @param facetValue - use null to remove facet
	 * 
	 */
	public void setFacetValue(String facetName, String facetValue) {
		if(facetMap == null) {
			facetMap = new HashMap<String, String[]>();
		}

		if(facetValue == null) {
			//remove the facet
			facetMap.remove(facetName);
		} else {
			facetMap.put(facetName, new String[]{facetValue});
		}
	}

	/**
	 * Adds a new facetValue for the specified facetName. Existing values
	 * for this facetName are kept. To replace
	 * @param facetName
	 * @param facetValue
	 */
	public void addFacetValue(String facetName, String facetValue) {
		if(facetMap == null) {
			facetMap = new HashMap<String, String[]>();
		}

		String[] values = facetMap.get(facetName);
		String[] newValues = values;
		if(values == null) {
			//this is the first value for this facetName
			newValues = new String[]{facetValue};
		} else {
			//there are existing facets, so expand the array 
			//and add the new facet
			newValues = new String[values.length+1];
			System.arraycopy(values, 0, newValues, 0, values.length);
		}

		facetMap.put(facetName,newValues);
	}

	/**
	 * Returns the facet values for the specified facetName. If there are no
	 * values for this facetName, returns null.
	 * 
	 */
	public String[] getFacetValues(String facetName) {
		String[] r = null;
		if(facetMap != null) {
			r = facetMap.get(facetName);
		}

		return r;
	}


	/**
	 * Returns a list of all facet names associated with this Data Set.
	 * @return
	 */
	public String[] getFacetNames() {
		String[] r = null;
		if(facetMap == null) {
			r = new String[]{};
		} else {
			r = new String[facetMap.size()];
			facetMap.keySet().toArray(r);
		}
		return r;
	}

	/**
	 * Returns true if this data set has the specified facetValue for the
	 * specified facetName.
	 * 
	 * @param facetName  - null is not a valid facetName. 
	 * 					   null will always return false
	 * @param facetValue - null is not a valid facetValue. 
	 * 					   null will always return false
	 * @return
	 */
	public boolean hasFacetValue(String facetName, String facetValue) {
		boolean r = false;
		if(facetName != null && facetValue != null && facetMap != null){
			String[] values = facetMap.get(facetName);
			if(values != null) {
				for(int i = 0; !r && i < values.length; i++) {
					r = facetValue.equals(values[i]);
				}
			}
		}

		return r;
	}

	public void addKeyValuePair(String key, String value) {
		if(jsonMap == null) {
			jsonMap = new HashMap<String, String>();
		}

		jsonMap.put(key, value);
	}

	public String getFacetJSON() {
		String r = null;
		StringBuffer sb = new StringBuffer();
		String[] facetNames = getFacetNames();
		String[] values;
		sb.append("{");
		if(facetNames != null && facetNames.length > 0) {
			sb.append("'");
			sb.append(facetNames[0]);
			sb.append("':");
			values = getFacetValues(facetNames[0]);
			sb.append("[");
			if(values != null && values.length > 0) { 
				sb.append("'");
				sb.append(values[0]);
				sb.append("'");
				for(int j = 1; j < values.length; j++) {
					sb.append(",");
					sb.append("'");
					sb.append(values[j]);
					sb.append("'");

				}
			}
			sb.append("]");

			for(int i = 1; i < facetNames.length; i++) {
				sb.append(",");
				sb.append("'");
				sb.append(facetNames[i]);
				sb.append("':");
				values = getFacetValues(facetNames[i]);
				sb.append("[");
				if(values != null && values.length > 0) { 
					sb.append("'");
					sb.append(values[0]);
					sb.append("'");
					for(int j = 1; j < values.length; j++) {
						sb.append(",");
						sb.append("'");
						sb.append(values[j]);
						sb.append("'");

					}
					sb.append("]");
				}
			}
			sb.append("}");
		}

		r = sb.toString();
		return r;
	}

	public String getJSONString() {
		String r = null;

		StringBuffer sb = new StringBuffer();
		sb.append("{");
        sb.append("'facets':");
        sb.append(getFacetJSON());
		if(jsonMap != null) {
			String[] keys = new String[jsonMap.size()];
			jsonMap.keySet().toArray(keys);
			if(keys.length > 0) {
				sb.append(",");
				sb.append("'");
				sb.append(keys[0]);
				sb.append("':'");
				sb.append(jsonMap.get(keys[0]));
				sb.append("'");
				for(int i = 1; i < keys.length; i++) {
					sb.append(",");
					sb.append("'");
					sb.append(keys[i]);
					sb.append("':'");
					sb.append(jsonMap.get(keys[i]));
					sb.append("'");
				}
			}
		}
		sb.append("}");
		r = sb.toString();
		return r;
	}

}
