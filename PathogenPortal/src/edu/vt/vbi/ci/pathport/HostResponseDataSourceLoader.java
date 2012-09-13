package edu.vt.vbi.ci.pathport;

import java.util.ArrayList;

import edu.vt.vbi.ci.util.PathPortUtilities;

public class HostResponseDataSourceLoader extends PPDataSourceLoader{

	private boolean loadAsSingle = false;


	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
		System.out.println("HostResponseDataSourceLoader.loadItemsFromSource(): " + dataSource.getName());
		if(dataSource.getLocationType().equals("xls url")) {
			loadXLS(dataSource);
		} else {
			if(loadAsSingle) {
				loadAsSingle(dataSource);
			} else {
				loadAsMultiple(dataSource);
			}
		}
	}


	private void loadXLS(PPDataSource dataSource) {
		System.out.println("HostResponseDataSourceLoader.loadXLS(): " + dataSource.getName());
		String contents = PathPortUtilities.getContentsOfURL(dataSource.getLocationURLs()[0]);
		System.out.println("contents: " + contents);
	}


	private void loadAsMultiple(PPDataSource dataSource) {
		System.out.println("CSVTableLoader.loadAsMultiple(): " + dataSource.getName());
		String csv = PathPortUtilities.getContentsOfURL(dataSource.getLocationURLs()[0]);
		//		System.out.println("csv:");
		//		System.out.println(csv);
		//parse csv file and create html table

		int idIndex = 0;
		int titleIndex = 1;
		int sourceLinkIndex = 2;
		int summaryIndex = 3;
		int contactOrganizationIndex = 5;
		int contactPersonIndex = 6;
		int contactEmailIndex = 7;
		int citeIndex = 8;
		int dateIndex;

		ArrayList<Experiment> expList = new ArrayList<Experiment>();
		Experiment exp;
		String[] rows = csv.split("\n");
		System.out.println("data rows: " + (rows.length-1));
		String[] cells;
		//first row has column titles. I am assuming a particular order, because this is just 
		//a short term solution. So I won't bother with lots of fancy options
		if(rows.length > 0) {
			String[] headings = rows[0].split("\t"); 
			int columnCount = headings.length;
			System.out.println("columns: " + columnCount);
			System.out.println("id: " + headings[idIndex]);
			System.out.println("title: " + headings[titleIndex]);
			System.out.println("source link: " + headings[sourceLinkIndex]);
			System.out.println("summary: " + headings[summaryIndex]);
			System.out.println("organization: " + headings[contactOrganizationIndex]);
			System.out.println("person: " + headings[contactPersonIndex]);
			System.out.println("email: " + headings[contactEmailIndex]);
			System.out.println("cite: " + headings[citeIndex]);

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

					expList.add(exp);
				}
			}

			Experiment[] experiments = new Experiment[expList.size()];
			expList.toArray(experiments);

			dataSource.setDataSets(experiments);
		}

	}


	private void loadAsSingle(PPDataSource dataSource) {
		System.out.println("CSVTableLoader.loadAsSingle(): " + dataSource.getName());

		String csv = PathPortUtilities.getContentsOfURL(dataSource.getLocationURLs()[0]);
		System.out.println("csv:");
		System.out.println(csv);
		//parse csv file and create html table

		String[] csvRows = csv.split("\n");
		String[] cells;
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"pp_table_wrapper\">");
		sb.append("<table class=\"host_response_data_table\">");
		//generate header row
		if(csvRows.length > 0) {
			sb.append("<tr>");
			cells = csvRows[0].split("\t");
			System.out.println("cells in header row: " + cells.length);
			for(int i = 0; i < cells.length; i++) {
				sb.append("<th>");
				sb.append(cells[i]);
				sb.append("</th>");
			}
			sb.append("</tr>");

			//add data rows 
			for(int i = 1; i < csvRows.length; i++) {
				cells = csvRows[i].split("\t");
				sb.append("<tr class=\"row_" + i + "\">");
				System.out.println("cells in row " + i + ": " + cells.length);
				for(int j = 0; j < cells.length; j++) {
					sb.append("<td class=\"col_" + j + "\">");
					sb.append(cells[j]);
					sb.append("</td>");
				}
				sb.append("</tr>");
			}
		}
		sb.append("</table>");
		sb.append("</div>");
		String table = sb.toString();

		StaticPage tablePage = new StaticPage();
		tablePage.setContent(table);
		tablePage.setTags(dataSource.getTags());
		tablePage.setType(dataSource.getType());
		dataSource.setDataSets(new PPDataSet[]{tablePage});

	}

}
