package edu.vt.vbi.ci.pathport;

import edu.vt.vbi.ci.util.PathPortUtilities;

public class StaticPageLoader extends PPDataSourceLoader{

	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
		String url = dataSource.getLocationURLs()[0];
		String content = PathPortUtilities.getContentsOfURL(url);

		StaticPage sp = new StaticPage();
		sp.setContent(content);
	    dataSource.setDataSets(new PPDataSet[]{sp});
	    sp.setTags(dataSource.getTags());
	    sp.setType(dataSource.getType());
	}

}
