package edu.vt.vbi.ci.pathport;

public class PPDataSourceReloader implements Runnable{

	private PPDataSource dataSource;

	public PPDataSourceReloader(PPDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void run() {
		System.out.println("PPDataSourceReloader started for data source: " + dataSource.getName());
		while(!Thread.interrupted()) {
			long waitTimeMillis = dataSource.getRefreshTimeSeconds()*1000;
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			PathPortData.reloadDataSource(dataSource);
		}
		System.out.println("reloader for data source " + dataSource.getName() + " is stopping");
	}

}
