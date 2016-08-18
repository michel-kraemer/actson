# Actson [![CircleCI](https://img.shields.io/circleci/project/michel-kraemer/actson.svg?maxAge=2592000)](https://circleci.com/gh/michel-kraemer/actson) [![codecov](https://codecov.io/gh/michel-kraemer/actson/branch/master/graph/badge.svg)](https://codecov.io/gh/michel-kraemer/actson) [![Apache License, Version 2.0](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Actson is a reactive JSON parser (sometimes referred to as non-blocking or
asynchronous). It is event-based and can be used together with reactive
libraries/tool-kits such as [RxJava](https://github.com/ReactiveX/RxJava) or
[Vert.x](http://vertx.io).

The library is very small and has no dependencies. It only requires Java 7
(or higher).

## Usage

The following snippet demonstrates how you can use the parser sequentially.

```java
// JSON text to parse
byte[] json = "{\"name\":\"Elvis\"}".getBytes(StandardCharsets.UTF_8);

JsonParser parser = new JsonParser(StandardCharsets.UTF_8);

int pos = 0; // position in the input JSON text
int event; // event returned by the parser
do {
    // proceed until the parser returns a new event or until it needs more input
    while ((event = parser.nextEvent()) == JsonEvent.NEED_MORE_INPUT) {
        // provide the parser with more input
        while (!parser.getFeeder().isFull() && pos < json.length) {
            parser.getFeeder().feed(json[pos]);
            ++pos;
        }

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

## Examples

* [PrettyPrinter.java](src/test/java/de/undercouch/actson/examples/pretty/PrettyPrinter.java)
  demonstrates how you can use Actson to pretty-print a JSON object or array.
  Note: this is no perfect implementation of a pretty-printer. The output could
  still be nicer. It's just a sample application.

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
