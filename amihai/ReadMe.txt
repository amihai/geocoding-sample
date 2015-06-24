Geocoding API Example
====================

You can run the project by executing the start.cmd from the  project zip artifact "amihai-1.0-SNAPSHOT.zip".

This "amihai-1.0-SNAPSHOT.zip" is generated using the maven assembly when you run the command

	mvn clean install

You can configure the url and API_KEY of the Geocoding API inside of the geocoding.properties

After you started the application you can trigger the processing of a file by placing it into the "./input" folder.

After the file will be processed a new output file be created into the "./output" directory and the initial file will be moved under "./input/.camel/".

Please not that the unit tests are using a REST simulator instead of the real connection url.

For more help see the Apache Camel documentation

    http://camel.apache.org/
    