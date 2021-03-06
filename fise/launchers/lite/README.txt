This module builds a runnable FISE jar using the Sling Launchpad Maven plugin,
including the bundles defined at src/main/bundles/list.xml

That Sling plugin is not released as I write this, so you need to build it
(source at http://svn.apache.org/repos/asf/sling/trunk/maven/maven-launchpad-plugin)

To start this after building use:

  java -Xmx512M -jar target/eu.iksproject.fise.launchers.lite-0.9-SNAPSHOT.jar

The FISE HTTP endpoint should then be available at 

  http://localhost:8080

So that you can POST content using, for example:

  curl -H "Content-Type: text/plain" -T data/text-examples/obama-signing.txt http://localhost:8080/engines

Configure any required parameter for the enhancement engines, at

  http://localhost:8080/system/console/

The OSGi state is stored in the ./sling folder.

The logs are found at sling/logs/error.log and can be configured from the
OSGi console.
