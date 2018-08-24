This p2 repository contains a feature patch for https://bugs.eclipse.org/bugs/show_bug.cgi?id=385738 : Launching command line exceeds the process creation command limit on Linux and MacOs. 
This patch has been contributed to Eclipse : http://git.eclipse.org/c/jdt/eclipse.jdt.debug.git/commit/?id=adc7d59a94a218a87318d0c800d57a84b1c347f8
However target milestone is 4.9M2, so this repository contains backports to 4.7 and 4.8 versions.

# Install it
Add the update site url to your eclipse installation : https://raw.githubusercontent.com/cchabanois/eclipse.jdt.debug/repository-fix-385738/p2 

The patch is available for :
* eclipse 4.7.2
* eclipse 4.7.3 (previous version of the fix : should not be used)
* eclipse 4.7.3a
* eclipse 4.8.0RC4 (previous version of the fix : should not be used)
* eclipse 4.8.0

# Code for the fix
Branches :
* for 4.9 dev version : master (fix has been committed) or fix_385738_master
* for 4.7.2 : fix_385738_R4_7_2 (backport of the fix)
* for 4.7.3 : fix_385738_R4_7_3 (previous version of the fix. Should not be used)
* for 4.7.3a : fix_385738_R4_7_3a (backport of the fix)
* for 4.8.0 : fix_385738_R4_8 (backport of the fix)

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
