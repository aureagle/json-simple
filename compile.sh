#!/bin/bash

mvn install:install-file -DgroupId="com.pastefs.libs" -DartifactId="json-simple" -Dversion="1.1.2" -Dpackaging=jar -Dfile=target/json-simple-1.1.2.jar -DlocalRepositoryPath=lib
