package edu.vt.vbi.ci.pathport;

public class PPDataSource {

	private String name;
	private String provider;
	private String type;
	private String[] tags;
	private String locationType;
	private String[] locationURLs;
	private String[] failoverLocationURLS;
	private PPDataSet[] dataSets;
	private long refreshTime = 86400;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.trim();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String getLocationType() {
		return locationType.trim();
	}
	public void setLocationType(String locationType) {
		this.locationType = locationType.trim();
	}
	public String[] getLocationURLs() {
		return locationURLs;
	}
	public void setLocationURLs(String[] locationURLs) {
		this.locationURLs = locationURLs;
	}
	public String[] getFailoverURLS() {
		return failoverLocationURLS;
	}
	public void setFailoverURLS(String[] failoverURLS) {
		this.failoverLocationURLS = failoverURLS;
	}
	
	public boolean isType(String type) {
		boolean r = false;
		if(type != null) {
			r = this.type.equalsIgnoreCase(type);
		}
		return r;
	}
	
	public boolean hasTag(String tag) {
		boolean r = false;
		for(int i = 0; !r && i < tags.length; i++) {
			r = tags[i].equals(tag);
		}
		return r;
	}
	
	public PPDataSet[] getDataSets() {
		return dataSets;
	}
	
	public void setDataSets(PPDataSet[] dataSets) {
		this.dataSets = dataSets;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	/**
	 * Sets the number of seconds to allow to elapse before reloading 
	 * contents from this data Source.
	 * 
	 * Default is 86,400 (24 hours)
	 * @param seconds value must be > 30. Smaller values will default to 30.
	 */
	public void setRefreshTimeSeconds(long seconds) {
		seconds = Math.max(seconds, 30);
		refreshTime = seconds;
	}
	
	/**
	 * Returns the number of seconds that should be allowed to elapse before 
	 * re-loading the contents from this Data Source. 
	 * The default refresh time is one day (86,400 seconds)
	 * @return
	 */
	public long getRefreshTimeSeconds() {
	    return refreshTime;
	}

}
