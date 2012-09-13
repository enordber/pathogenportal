package edu.vt.vbi.ci.pathport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.portlet.PortletRequest;

public class FacetCutter {

	/**
	 * A Facet Map is a HashMap with Facet Names as Keys, and HashMaps
	 * as Values. The Value HashMaps have Facet Values as Keys and 
	 * ExtendedBitSets as Values. The set values in the Bit Sets are the indices
	 * of the Data Sets in the input data array that have that Facet Value
	 * for that Facet Name.
	 * 
	 * @param data
	 * @return HashMap<String, HashMap<String, ExtendedBitSet> >
	 */
	public static HashMap<String, HashMap<String, ExtendedBitSet> > getFacetMap(PPDataSet[] data) {
		HashMap<String, HashMap<String, ExtendedBitSet> > r = 
			new HashMap<String, HashMap<String, ExtendedBitSet> >();
		if(data != null) {
			for(int i = 0; i < data.length; i++) {
				String[] facetNames = data[i].getFacetNames();
				if(facetNames != null && facetNames.length > 0) {
					//for each facetName, add an entry in the map for each
					//facet value, then set the ith bit in the BitSet for
					//that facetName-facetValue pair
					for(int j = 0; j < facetNames.length; j++) {
						HashMap<String, ExtendedBitSet> facetValueMap = r.get(facetNames[j]);
						if(facetValueMap == null) {
							facetValueMap = new HashMap<String, ExtendedBitSet>();
						}
						//						System.out.println("facet name " + j + " from data set " + i + ": " + facetNames[j]);
						String[] facetValues = data[i].getFacetValues(facetNames[j]);
						if(facetValues != null && facetValues.length > 0) {
							for(int k = 0; k < facetValues.length; k++) {
								//								System.out.println("\tfacet value " + k + ": " + facetValues[k]);
								ExtendedBitSet members = facetValueMap.get(facetValues[k]);
								if(members == null) {
									members = new ExtendedBitSet();
								}
								members.set(i);
								facetValueMap.put(facetValues[k], members);
							}
						}

						r.put(facetNames[j], facetValueMap);
					}

				}
			}
		}

		return r;
	}

	public static ExtendedBitSet getMemberSetList(HashMap<String, HashMap<String, ExtendedBitSet>> facetMap, String facetName, String facetValue) {
		ExtendedBitSet r = null;
		if(facetName != null && facetValue != null &&facetMap != null) {
			HashMap<String, ExtendedBitSet> facetValueMap = facetMap.get(facetName);
			if(facetValueMap != null) {
				r = facetValueMap.get(facetValue);
			}
		}
		return r;
	}

	public static ExtendedBitSet getMemberSetList(HashMap<String, HashMap<String, ExtendedBitSet>> facetMap, String facetName, String[] facetValues) {
		ExtendedBitSet r = new ExtendedBitSet();;
		if(facetName != null && facetValues != null &&facetMap != null) {
			HashMap<String, ExtendedBitSet> facetValueMap = facetMap.get(facetName);
			if(facetValueMap != null) {
				for(int i = 0; i < facetValues.length; i++) {
					r = r.getOr(facetValueMap.get(facetValues[i]));
				}
			}
		}
		return r;
	}

	/**
	 * Returns a 2D array with pairs of Strings:
	 * r[0][0] facetName0
	 * r[0][1] facetValue0
	 * r[1][0] facetName1
	 * r[1][1] facetValue1
	 * 
	 * @param request
	 * @return
	 */
	public static String[][] getSelectedFacetValues(PortletRequest request) {
		String[][] r = new String[0][];

		String filterParam = request.getParameter("facet_values");
		request.setAttribute("filterParams", filterParam);
		if(filterParam != null) {
			String[] facetNameValueBlocks = filterParam.split(",");
			String block;

			ArrayList<String[]> pairList = new ArrayList<String[]>();

			for(int i = 0; i < facetNameValueBlocks.length; i++) {
				block = facetNameValueBlocks[i];
				if(block != null && block.length() > 2) {
					String[] facetNameValuePair = block.split("--");
					if(facetNameValuePair.length == 2) {
						pairList.add(facetNameValuePair);
					}
				}
			}

			r = new String[pairList.size()][2];
			pairList.toArray(r);
		}

		return r;
	}

	/**
	 * For a set of selected Facet Values, this method constructs the 
	 * resulting selection set for each Facet Name. The returned value is 
	 * a HashMap with 
	 * 	Key: Facet name
	 * 	Value: ExtendedBitSet indicating items selected by this facet.
	 * 
	 * If all the Values (ExtendedBitSets) from the returned HashMap are
	 * ANDed together, the result will be the final set of selected indices.
	 * 
	 * @param facetMap
	 * @param selectedFacetNameValuePairs
	 * @param itemCount
	 * @return
	 */
	public static HashMap<String, ExtendedBitSet> getSelectedFacetSets(HashMap<String, HashMap<String, ExtendedBitSet>> facetMap, String[][] selectedFacetNameValuePairs, int itemCount) {
		HashMap<String, ExtendedBitSet> r = new HashMap<String, ExtendedBitSet>();		
		//within a facet, OR
		//populate selectedFacetSets with facetName keys, and empty BitSets
		String facetName;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			if(!r.containsKey(facetName)) {
				r.put(facetName, new ExtendedBitSet());
			}
		}

		//for each facet name-value pair, get the BitSet for the members from the
		//fullFacetMap. OR each of these to the facet BitSet in selectedFacetSets
		String facetValue;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			facetValue = selectedFacetNameValuePairs[i][1];
			ExtendedBitSet facetValueMembers = 
				facetMap.get(facetName).get(facetValue);

			r.get(facetName).or(facetValueMembers);
		}

		return r;
	}
	
	/**
	 * Determines which items are selected, based on a set of facet value selections.
	 * Returns a BitSet, where set indices indicate the selected items.
	 * 
	 * @param selectedFacetNameValuePairs
	 */
	public static ExtendedBitSet determineSelectedMembers(HashMap<String, HashMap<String, ExtendedBitSet>> facetMap, String[][] selectedFacetNameValuePairs, int itemCount) {
		ExtendedBitSet r = null;

		//within a facet, OR
		HashMap<String, ExtendedBitSet> selectedFacetSets = new HashMap<String, ExtendedBitSet>();
		//populate selectedFacetSets with facetName keys, and empty BitSets
		String facetName;
		for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
			facetName = selectedFacetNameValuePairs[i][0];
			if(!selectedFacetSets.containsKey(facetName)) {
				selectedFacetSets.put(facetName, new ExtendedBitSet());
			}
		}

		HashSet<String > selectedCheckboxIds = new HashSet<String>();

		if(selectedFacetNameValuePairs.length == 0) {
			r = new ExtendedBitSet();
			r.set(0, itemCount);
		} else {
			//for each facet name-value pair, get the BitSet for the members from the
			//fullFacetMap. OR each of these to the facet BitSet in selectedFacetSets
			String facetValue;
			for(int i = 0; i < selectedFacetNameValuePairs.length; i++) {
				facetName = selectedFacetNameValuePairs[i][0];
				facetValue = selectedFacetNameValuePairs[i][1];
				ExtendedBitSet facetValueMembers = 
					facetMap.get(facetName).get(facetValue);

				selectedFacetSets.get(facetName).or(facetValueMembers);
				String checkboxId = facetName + "--" + facetValue;
				selectedCheckboxIds.add(checkboxId);
			}

			r = new ExtendedBitSet();
			String[] facetNames = new String[selectedFacetSets.size()];
			selectedFacetSets.keySet().toArray(facetNames);

			//then do an AND of the facets
			ExtendedBitSet[] facetSets = new ExtendedBitSet[selectedFacetSets.size()];
			selectedFacetSets.values().toArray(facetSets);
			if(facetSets.length > 0) {
				//start off by setting the members that selected by the first facet
				r.or(facetSets[0]);
			}
			//AND with the rest of the facet selection sets
			for(int i = 1; i < facetSets.length; i++) {
				r.and(facetSets[i]);
			}
		}


		return r;
	}

	/**
	 * For sorting Facet Names based on the number of Facet Values they have.
	 * Facet Names with fewer Facet Values will be first. 
	 * @author enordber
	 *
	 */
	public static class FacetValueComparator implements Comparator<String>{
		private HashMap<String, HashMap<String, ExtendedBitSet>> facetMap;

		public FacetValueComparator(HashMap<String, HashMap<String, ExtendedBitSet>> facetMap) {
			this.facetMap = facetMap;
		}

		public int compare(String o1, String o2) {
			HashMap m1 = facetMap.get(o1);
			HashMap m2 = facetMap.get(o2);
			int r = m1.size() - m2.size();
			return r;
		}
		
	}
}
