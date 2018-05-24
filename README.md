To update the p2 repository :
1) Add features and plugins to source folder
2) update category.xml
3) 
eclipse -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -artifactRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -source source -configs gtk.linux.x86 -publishArtifacts
eclipse -application org.eclipse.equinox.p2.publisher.CategoryPublisher -metadataRepository file:/home/cchabanois/git/eclipse.jdt.debug/p2 -categoryDefinition file:/home/cchabanois/git/eclipse.jdt.debug/source/category.xml
