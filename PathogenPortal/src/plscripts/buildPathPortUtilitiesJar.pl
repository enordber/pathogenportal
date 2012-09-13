#!/usr/bin/perl -w

#Package the necessary classes into a jar file for
#PathPortUtilities

@filesToJar = ();

$jarFileName = "/Users/enordber/vbi/workspace/PathogenPortal/PathPortUtilities.jar";

$baseDir = "/Users/enordber/vbi/workspace/PathogenPortal/bin";

push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/util/PathPortUtilities.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/util/ReverseComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/Experiment.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/Experiment\\\$ExperimentDataTypeComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/Experiment\\\$ExperimentDateComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/Experiment\\\$ExperimentOrganismComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/ExtendedBitSet.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/FacetCutter.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/FacetCutter\\\$FacetValueComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/GoogleSitesLoader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/HostResponseDataSourceLoader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PathPortData.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPDataSet.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPDataSource.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PathPortData.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPDataSourceLoader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPDataSourceReloader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPExperimentDataLoader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeLoader.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeLoader\\\$1.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$GenomeNameComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$BRCComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$GeneCountComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$LengthComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$OrganismTypeComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$SequencingCenterComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$StatusComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPGenomeInfo\\\$TaxonIdComparator.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/PPRSS.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/RSSItem.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/StaticPage.class");
push(@filesToJar, "-C $baseDir edu/vt/vbi/ci/pathport/StaticPageLoader.class");

#JSONObject classes
push(@filesToJar, "-C $baseDir org/json/CDL.class");
push(@filesToJar, "-C $baseDir org/json/Cookie.class");
push(@filesToJar, "-C $baseDir org/json/CookieList.class");
push(@filesToJar, "-C $baseDir org/json/HTTP.class");
push(@filesToJar, "-C $baseDir org/json/HTTPTokener.class");
push(@filesToJar, "-C $baseDir org/json/JSONArray.class");
push(@filesToJar, "-C $baseDir org/json/JSONException.class");
push(@filesToJar, "-C $baseDir org/json/JSONML.class");
push(@filesToJar, "-C $baseDir org/json/JSONObject.class");
push(@filesToJar, "-C $baseDir org/json/JSONObject\\\$Null.class");
push(@filesToJar, "-C $baseDir org/json/JSONString.class");
push(@filesToJar, "-C $baseDir org/json/JSONStringer.class");
push(@filesToJar, "-C $baseDir org/json/JSONTokener.class");
push(@filesToJar, "-C $baseDir org/json/JSONWriter.class");
push(@filesToJar, "-C $baseDir org/json/XML.class");
push(@filesToJar, "-C $baseDir org/json/XMLTokener.class");


$baseDir = "/Users/enordber/vbi/workspace/PathogenPortal/lib";
push(@filesToJar, "-C $baseDir nanoxml/XMLElement.class");
push(@filesToJar, "-C $baseDir nanoxml/XMLParseException.class");

$jarCommand = "jar cvf $jarFileName";

for($i = 0; $i < @filesToJar; $i++) {
    $jarCommand = "$jarCommand $filesToJar[$i]";
}

print "$jarCommand\n";
system($jarCommand);

@additionalLocations = ();
push(@additionalLocations, "/Users/enordber/vbi/jboss_epp_4.3/jboss-epp-4.3/jboss-as/server/jboss-pathport/lib");

for($i = 0; $i < @additionalLocations; $i++) {
    $cpCommand = "cp $jarFileName $additionalLocations[$i]";
    print "$cpCommand\n";
    system($cpCommand);	
}
