package edu.vt.vbi.ci.pathport;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GoogleSitesLoader extends PPDataSourceLoader{

	private String content;
	
	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
		String[] locations = dataSource.getLocationURLs();
		if(locations != null && locations.length > 0) {
			try {
				content = getPageContent(locations[0]);
				StaticPage page = new StaticPage();
				page.setContent(content);
				page.setTags(dataSource.getTags());
				page.setDataSource(dataSource);
				dataSource.setDataSets(new PPDataSet[]{page});
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("loaded content from google sites page at " + locations[0]);
	}
	
	/**
	 * Retrieves the main content div from a Google Sites page at the 
	 * specified URL.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String getPageContent(String url) throws IOException {
		String r = null;
		Document doc = Jsoup.connect(url).get();
		Elements sites_canvas = doc.select("#sites-canvas-main-content");
		r = sites_canvas.toString();
		
		//remove rel="nofollow" from links. These are added by Google Sites
		r = r.replaceAll("\\s*rel=\"nofollow\"", "");
		return r;
	}
	
	public String getHTML() {
		return content;
	}


}
