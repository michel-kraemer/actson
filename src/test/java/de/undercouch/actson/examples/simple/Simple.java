// MIT License
//
// Copyright (c) 2016 Michel Kraemer
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package de.undercouch.actson.examples.simple;

import java.nio.charset.StandardCharsets;

import de.undercouch.actson.JsonEvent;
import de.undercouch.actson.JsonParser;

/**
 * A simple example demonstrating sequential usage of {@link JsonParser}
 * @author Michel Kraemer
 */
public class Simple {
  /**
   * The main program
   * @param args program arguments
   */
  public static void main(String[] args) {
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
      if (event == JsonEvent.VALUE_STRING) {
        System.out.println("VALUE: " + parser.getCurrentString());
      }
    } while (event != JsonEvent.EOF);
  }
}
