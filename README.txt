Original project site:
http://code.google.com/p/json-simple/

Original GitHub clone:
https://github.com/fangyidong/json-simple.git

---------------------------------------------------

My idea of forking is to combine the powers of different forks of this project and build a better and simple json parser by taking json-simple further...

for maven

<repositories>
...
    	<repository>
    		<id>git-aureagle</id>
    		<name>aureagle's git based repository</name>
    		<url>https://raw.githubusercontent.com/aureagle/json-simple/maven-repo/lib/</url>
    	</repository>
...
</repositories>

        <dependency>
        	<groupId>com.pastefs.libs</groupId>
        	<artifactId>json-simple</artifactId>
        	<version>1.1.2</version>
        	<scope>compile</scope>
        </dependency>

it conflicts with com.googlecode.json-simple so add this under a dependency that requires json-simple

<dependency>
....
            <exclusions>
            	<exclusion>
            		<groupId>com.googlecode.json-simple</groupId>
            		<artifactId>json-simple</artifactId>
            	</exclusion>
            </exclusions>

....
</dependency>
