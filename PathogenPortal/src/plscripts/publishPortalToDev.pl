#!/usr/bin/perl

@filesToMove = ();
$deploy = "/Users/enordber/vbi/jboss_epp_4.3/jboss-epp-4.3/jboss-as/server/jboss-pathport/deploy/";

push(@filesToMove, $deploy."PPADBMain.war");
push(@filesToMove, $deploy."PPAboutMain.war");
push(@filesToMove, $deploy."PPAnalyzeMain.war");
push(@filesToMove, $deploy."PPDataMain.war");
push(@filesToMove, $deploy."PPEnvironmentMain.war");
push(@filesToMove, $deploy."PPGlobalNavigation.war");
push(@filesToMove, $deploy."PPHomeMain.war");
push(@filesToMove, $deploy."PPHostMain.war");
push(@filesToMove, $deploy."PPNewsMain.war");
push(@filesToMove, $deploy."PPPathogenMain.war");
push(@filesToMove, $deploy."PPSearchMain.war");

$lib = "/Users/enordber/vbi/jboss_epp_4.3/jboss-epp-4.3/jboss-as/server/jboss-pathport/lib/";
push(@filesToMove, $lib."PathPortUtilities.jar");


$theme = "/Users/enordber/vbi/jboss_epp_4.3/jboss-epp-4.3/jboss-as/server/jboss-pathport/deploy/jboss-portal.sar/portal-core.war/themes/pathport/";
push(@filesToMove, $theme."portal_style.css");

$copyTo = "enordber\@ravenhill.vbi.vt.edu:pathport2/";

$scpCommand = "scp ";

for($i = 0; $i < @filesToMove; $i++) {
	$scpCommand .= $filesToMove[$i]." ";
}

$scpCommand .= $copyTo;

print "$scpCommand\n";
system($scpCommand);