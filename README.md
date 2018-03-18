# Deployment Recorder Extension

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/deployment-recorder-extension.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.maven.extensions/deployment-recorder-extension.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.soebes.maven.extensions%22%20a%3A%22deployment-recorder-extension%22)
[![Build Status](https://travis-ci.org/khmarbaise/deployment-recorder-extension.svg?branch=master)](https://travis-ci.org/khmarbaise/deployment-recorder-extensions)

This is an [EventSpy][1] implementation which collects the information about
the deployed artifacts and writes it into a single file in `projectRoot/target/`.
This file is named `deployment-recorder.lst`.

If you like to use this extension with Maven 3.1.1+ till Maven 3.2.5 you need
to manually download it from Maven Central and put the resulting jar
file into the `${M2_HOME}/lib/ext` directory or if you
like to use it directly you have to add the following parameter on command line:

```
mvn -Dmaven.ext.class.path=PathWhereItIsLocated/deployment-recorder-extension-0.1.0-mvn311.jar clean package
```

If you like to use this extension for Maven 3.3.1+ you
have to define the following `.mvn/extensions.xml` file:

``` xml
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd">
  <extension>
    <groupId>com.soebes.maven.extensions</groupId>
    <artifactId>deployment-recorder-extension</artifactId>
    <version>0.1.0</version>
  </extension>
</extensions>
```

If you have configured the deployment-recorder-extension this will mentioned at the beginning
of the build like this:
```
[INFO] deployment-recorder-extension Version 0.1.0 started.
```

If you do an `mvn deploy` will run without supplemental output except the line
as mentioned before.  After the build has finished the file
`deployment-recorder.lst` will contain lines like this:

```
com.soebes.examples.j2ee:parent:pom:1.1.2-20170325.120735-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:domain:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:domain:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:service-client:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:service-client:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:webgui:war:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:webgui:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:service:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:service:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:app:ear:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:app:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:appasm:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:shade:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:shade:pom:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:shade:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT:test
com.soebes.examples.j2ee:shade:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT:dev
com.soebes.examples.j2ee:shade:jar:1.1.2-20170325.120736-41:1.1.2-SNAPSHOT:prod
com.soebes.examples.j2ee:assembly:pom:1.1.2-20170325.120736-42:1.1.2-SNAPSHOT
com.soebes.examples.j2ee:assembly:zip:1.1.2-20170325.120736-42:1.1.2-SNAPSHOT:archive
com.soebes.examples.j2ee:assembly:jar:1.1.2-20170325.120736-42:1.1.2-SNAPSHOT:prod
com.soebes.examples.j2ee:assembly:jar:1.1.2-20170325.120736-42:1.1.2-SNAPSHOT:dev
```
The format is: `groupId:artifactId:extension:version:baseVersion[:classifier]`.


Prerequisites minimum for this is Maven 3.1.1+ and Java 1.7 as run time.

If you have ideas for improvements etc. just fill in issues in the tracking system.

[1]: http://maven.apache.org/ref/3.0.3/maven-core/apidocs/org/apache/maven/eventspy/AbstractEventSpy.html
