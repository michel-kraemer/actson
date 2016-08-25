# Actson [![CircleCI](https://img.shields.io/circleci/project/michel-kraemer/actson.svg?maxAge=2592000)](https://circleci.com/gh/michel-kraemer/actson) [![codecov](https://codecov.io/gh/michel-kraemer/actson/branch/master/graph/badge.svg)](https://codecov.io/gh/michel-kraemer/actson) [![Apache License, Version 2.0](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/de.undercouch/actson.svg?maxAge=2592000)](http://search.maven.org/#artifactdetails%7Cde.undercouch%7Cactson%7C1.0.0%7Cjar)

Actson is a reactive JSON parser (sometimes referred to as non-blocking or
asynchronous). It is event-based and can be used together with reactive
libraries/tool-kits such as [RxJava](https://github.com/ReactiveX/RxJava) or
[Vert.x](http://vertx.io).

The library is very small and has no dependencies. It only requires Java 7
(or higher).

## Why another JSON parser?

* **Non-blocking.** Other JSON parsers use blocking I/O (i.e. they read from an
  `InputStream`). If you want to develop a reactive application you should use
  non-blocking I/O (see the [Reactive Manifesto](http://www.reactivemanifesto.org/)).
* **Big Data.** Most parsers read the full JSON text into memory to map it to
  a POJO, for example. Actson can handle arbitrarily large JSON text. It is
  event-based and can be used for streaming.
* **GeoRocket.** Actson was primarily developed for [GeoRocket](http://georocket.io),
  a high-performance reactive data store for geospatial files. We use
  [Aalto XML](https://github.com/FasterXML/aalto-xml) to parse XML in a
  non-blocking way and we needed something similar for GeoRocket's
  [GeoJSON](http://geojson.org/) support.

## Usage

The following snippet demonstrates how you can use the parser sequentially.

```java
// JSON text to parse
byte[] json = "{\"name\":\"Elvis\"}".getBytes(StandardCharsets.UTF_8);

JsonParser parser = new JsonParser(StandardCharsets.UTF_8);

int pos = 0; // position in the input JSON text
int event; // event returned by the parser
do {
    // feed the parser until it returns a new event
    while ((event = parser.nextEvent()) == JsonEvent.NEED_MORE_INPUT) {
        // provide the parser with more input
        pos += parser.getFeeder().feed(json, pos, json.length - pos);

        // indicate end of input to the parser
        if (pos == json.length) {
            parser.getFeeder().done();
        }
    }

    // handle event
    System.out.println("JSON event: " + event);
    if (event == JsonEvent.ERROR) {
        throw new IllegalStateException("Syntax error in JSON text");
    }
} while (event != JsonEvent.EOF);
```

Find more complex examples using [RxJava](https://github.com/ReactiveX/RxJava)
or [Vert.x](http://vertx.io) below.

## Examples

* [SimpleExample.java](examples/src/main/java/simple/SimpleExample.java)
  shows sequential usage of Actson (basically the same as the example above).
* [RxJavaExample.java](examples/src/main/java/rxjava/RxJavaExample.java)
  demonstrates how you can use Actson and [RxJava](https://github.com/ReactiveX/RxJava)
  to parse JSON in an event-based manner. It uses an operator function
  [JsonParserOperator.java](examples/src/main/java/rxjava/JsonParserOperator.java)
  that can be lifted into an Observable to transform byte arrays to JSON
  events.
* [VertxExample.java](examples/src/main/java/vertx/VertxExample.java)
  shows how Actson can be used together with Vert.x. It does the same as
  [SimpleExample.java](examples/src/main/java/simple/SimpleExample.java)
  or [RxJavaExample.java](examples/src/main/java/rxjava/RxJavaExample.java)
  but works completely asynchronously and non-blocking.
* [WebServiceExample.java](examples/src/main/java/webservice/WebServiceExample.java)
  combines Vert.x, RxJava and Actson to a reactive web service. The HTTP service
  accepts JSON arrays and returns the number of elements in this array. It can
  handle arbitrarily large files and multiple requests in parallel without
  becoming unresponsive.
* [PrettyPrinter.java](src/test/java/de/undercouch/actson/PrettyPrinter.java)
  demonstrates how you can use Actson to pretty-print a JSON object or array.
  Note: this is no perfect implementation of a pretty-printer. The output could
  still be nicer. It's just a sample application.

## Download

Binaries and dependency information for Maven, Gradle, Ivy and others can be
found at [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg:%22de.undercouch%22%20AND%20a:%22actson%22).

Example for Maven:

```xml
<dependencies>
    <dependency>
        <groupId>de.undercouch</groupId>
        <artifactId>actson</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

Example for Gradle:

```gradle
dependencies {
    compile 'de.undercouch:actson:1.1.0'
}
```

## Building

Execute the following command to compile the library and to run the
unit tests:

    ./gradlew test

The script automatically downloads the correct Gradle version, so you
won't have to do anything else. If everything runs successfully, you
may create a .jar library:

    ./gradlew jar

The library will be located under the `build/libs` directory.

### Eclipse

Gradle includes a task that creates all files required to develop
Actson in Eclipse. Run the following command:

    ./gradlew eclipse

Then import the project into your workspace.

### IntelliJ

Gradle includes a task that creates all files required to develop
Actson in IntelliJ. Run the following command:

    ./gradlew idea

Then import the project into your workspace or open the root `actson.ipr`
project file.

## Similar libraries

* [Jackson](https://github.com/FasterXML/jackson) has a streaming API that
  produces JSON tokens/events. However, it uses blocking I/O because it reads
  from an `InputStream`.
* [Aalto XML](https://github.com/FasterXML/aalto-xml) is similar to Actson
  but parses XML instead of JSON.

## Acknowledgments

The event-based parser code and the JSON files used for testing are largely
based on the file [JSON_checker.c](http://www.json.org/JSON_checker/) and
the JSON test suite from [JSON.org](http://www.json.org/) originally released
under [this license](LICENSE_JSON_checker) (basically MIT license).

## License

Actson is released under the **MIT license**. See the
[LICENSE](LICENSE) file for more information.
