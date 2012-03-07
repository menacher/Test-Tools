This is a webservices integration/functional testing tool, specifically designed to execute multi-request, multi-web service test cases. This testing tool can be used for functional and integration testing of SOAP webservice endpoints. It is built on top of  [TestNG](http://testng.org/) testing framework, [spring webservices](http://static.springsource.org/spring-ws/site/ "spring-ws") project and uses [Jetlang](http://code.google.com/p/jetlang/ "jetlang") and [Groovy](http://groovy.codehaus.org/ "language").    
Head over to the wiki pages for a quick start.

Installation
============
**Pre-requisites**: Please have maven 3+ and [Spring source tool suite](http://www.springsource.com/developer/sts "STS") (or eclipse) installed. This project relies on the [groovy-eclipse compiler](http://groovy.codehaus.org/Groovy-Eclipse+compiler+plugin+for+Maven "groovy-eclipse") in order to compile the code and not maven java or groovy compiler. If that is unviable please modify the pom accordingly. You also need to install the Groovy feature(including the optional 1.8 compiler) in STS/eclipse. If you are using plain vanilla eclipse, then M2Eclipse and EGit plugins need to be installed
apart from the groovy eclipse plugin.

Steps
-----
1.  git clone git@github.com:menacher/Test-Tools.git
2.  cd Test-Tools
3.  cd ws-int-test
4.  mvn clean eclipse:eclipse - **Takes time, the first time!**
5.  Now go to eclipse -> file -> import -> git -> select repository and import the ws-int-test project
6.  ws-int-test project in eclipse -> right click on pom.xml -> run as -> maven test - **Takes time the first time!**

If everything works as expected you should see some test cases executed successfully!

*Happy coding!*
