# Actson

Actson is a reactive JSON parser (sometimes referred to as non-blocking JSON
parser or asynchronous JSON parser). It is event-based and can be used together
with reactive libraries such as [RxJava](https://github.com/ReactiveX/RxJava).

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

actson is released under the **MIT license**. See the
[LICENSE](LICENSE) file for more information.
