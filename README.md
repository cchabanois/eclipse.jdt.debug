This p2 repository contains a feature patch for https://bugs.eclipse.org/bugs/show_bug.cgi?id=385738 : Launching command line exceeds the process creation command limit on Linux and MacOs. 
This patch will be contributed to Eclipse when ready.

# Install it
Add the update site url to your eclipse installation : https://raw.githubusercontent.com/cchabanois/eclipse.jdt.debug/repository-fix-385738/p2 

The patch is available for :
* eclipse 4.7.2
* eclipse 4.7.3
* eclipse 4.7.3a

# Code for the fix
Branches :
* for 4.8 dev version : fix_385738_master
* for 4.7.2 : fix_385738_R4_7_2
* for 4.7.3 : fix_385738_R4_7_3
* for 4.7.3a : fix_385738_R4_7_3a

# Updating P2 repository
To update the p2 repository :
1) generate the features and plugins from the fix branch. Each fix branch contains a project org.eclipse.jdt.launching.bug385738 for the feature
2) Add features and plugins to source folder
3) update category.xml
4) Publish the artifacts to the p2 repository
```
eclipse -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -artifactRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -source source -configs gtk.linux.x86 -publishArtifacts
```
5) Publish the categoy file
```
eclipse -application org.eclipse.equinox.p2.publisher.CategoryPublisher -metadataRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -categoryDefinition file:/home/cchabanois/git/eclipse.jdt.debug/source/category.xml
```
