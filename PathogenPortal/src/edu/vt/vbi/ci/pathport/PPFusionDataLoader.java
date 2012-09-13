package edu.vt.vbi.ci.pathport;

import java.util.HashMap;

public class PPFusionDataLoader extends PPDataSourceLoader{
//	#use the vbi.cid@gmail account to access
//	#current fusion tables are:
//	#project table id = 16ePPrQ-RCIh7U1vqJ-5cPX4QM_Fp3fIpcLZ0jp0
//	#assay table id = 1QDxHnJ7GIyIVNak2QhP3IFCljOWHBHR7NJktsZM
//
//	function ClientLogin() {
//	read -p 'Email> ' email
//	read -p 'Password> ' -s password
//	local service=$1
//	curl -s -d Email=$email -d Passwd=$password -d service=$service https://www.google.com/accounts/ClientLogin | tr ' ' \n | grep Auth= | sed -e 's/Auth=//'
//	}
//
//	function FusionTableQuery() {
//	local sql=$1
//	curl -L -s -H "Authorization: GoogleLogin auth=$(ClientLogin fusiontables)" --data-urlencode sql="$sql" https://www.google.com/fusiontables/api/query
//	}
//
//	FusionTableQuery "SELECT * FROM 16ePPrQ-RCIh7U1vqJ-5cPX4QM_Fp3fIpcLZ0jp0"


	public static void main(String[] args) {
		PPFusionDataLoader ppfdl = new PPFusionDataLoader(args);
		
	}
	
	public PPFusionDataLoader(String[] args) {
		String projectTableId = "16ePPrQ-RCIh7U1vqJ-5cPX4QM_Fp3fIpcLZ0jp0";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("Email", "vbi.cid@gmail.com");
	}

	@Override
	public void loadItemsFromSource(PPDataSource dataSource) {
	}

}
