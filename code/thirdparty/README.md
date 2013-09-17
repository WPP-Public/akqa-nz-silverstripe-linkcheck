#Broken Links Finder
===

## Description:
A web application(tool) that allows web developers to generate a report consisting of all the broken links in their website. It helps in knowing which links are valid and which links are dead.

##Requirements:
1. JRE to Execute
2. JDK to Compile

##Installation:
Clone the Repository.

	git clone https://github.com/crusador/blf-java.git
	
Note: The provided .class files are for JRE >= 1.7.0. If your JRE doesn't match, read the compilation steps.

##Compilation:
Compile the "crawler" package.

	javac Crawler.java LinkStats.java

Compile the "Project.java" file.

	javac Project.java

##Running:
If "crawler" package is in the same directory.

	java Project <URL> <Report/File/Path> <MaxDepthLevel> <MaxPages>
	
If "crawler" package is in some other directory.

	java Project -cp <path/crawler/package> <URL> <Report/File/Path> <MaxDepthLevel> <MaxPages>'

##Copyright and Licence

2012 [JAKWorks] (http://www.facebook.com/JAKWorks)

Broken Links Finder by JAKWorks is licensed under a [Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License](http://creativecommons.org/licenses/by-nc-sa/3.0/)
