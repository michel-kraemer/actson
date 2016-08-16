# Actson

Actson is a reactive JSON parser (sometimes referred to as non-blocking or
asynchronous). It is event-based and can be used together with reactive
libraries/tool-kits such as [RxJava](https://github.com/ReactiveX/RxJava) or
[Vert.x](http://vertx.io).

The library is very small and has no dependencies. It only requires Java 7
(or higher).

## Usage

Consider a callback function `dataHandler` that asynchronously receives char
arrays containing incomplete parts of a JSON text to parse. Consider another
callback function `endHandler` that will be called when there is no more data
to parse. Actson's `JsonParser` can be used as follows (pseudo code):

```java
JsonParser parser = new JsonParser();

// Add your listener here. The listener will receive events from the
// parser when it encouters JSON tokens.
parser.addListener(new MyListener());

public void dataHandler(char[] c) {
    // Forward all characters to the parser. The parser will immediately
    // call the listener on each JSON token.
    for (int i = 0; i < c.length; ++i) {
        parser.feed(c[i]); // returns false if the JSON text is invalid
    }
}

public void endHandler() {
    // don't forget to call done()
    parser.done(); // returns false if the JSON text was invalid
}
```

## Examples

* [PrettyPrinter.java](src/test/java/de/undercouch/actson/examples/pretty/PrettyPrinter.java)
  demonstrates how you can use
  [JsonEventListener](src/main/java/de/undercouch/actson/JsonEventListener.java)
  to pretty-print a JSON object or array. Note: this is no perfect
  implementation of a pretty-printer. The output could still be nicer. But it
  demonstrates very well how to use the event listener.

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
