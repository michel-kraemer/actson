# Actson

Actson is a reactive JSON parser (sometimes referred to as non-blocking or
asynchronous). It is event-based and can be used together with reactive
libraries such as [RxJava](https://github.com/ReactiveX/RxJava).

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
