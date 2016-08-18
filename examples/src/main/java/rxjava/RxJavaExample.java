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

package rxjava;

import java.nio.charset.StandardCharsets;

import de.undercouch.actson.JsonEvent;
import rx.Observable;

/**
 * This example shows how you can use Actson and RxJava to parse JSON in an
 * event-based manner. It uses an operator function {@link JsonParserOperator}
 * that can be lifted into an observable to transform byte arrays to JSON
 * events. The example basically does the same as {@link simple.SimpleExample}.
 * @author Michel Kraemer
 */
public class RxJavaExample {
  /**
   * The main program
   * @param args program arguments
   */
  public static void main(String[] args) {
    // JSON text to parse (split into two chunks for demonstration purpose)
    byte[] json_chunk1 = "{\"name\":\"El".getBytes(StandardCharsets.UTF_8);
    byte[] json_chunk2 = "vis\"}".getBytes(StandardCharsets.UTF_8);
    
    // Start parsing. Note: we're using toBlocking() here just to make the
    // example work. In production you will most likely want to avoid this
    // method if your application should be non-blocking.
    Observable.just(json_chunk1, json_chunk2)
      .lift(new JsonParserOperator()) // <-- this is the important line!
      .toBlocking() // just for demonstration purpose!!
      .forEach(v -> {
        System.out.println("JSON event: " + v.getEvent());
        if (v.getEvent() == JsonEvent.VALUE_STRING) {
          System.out.println("VALUE: " + v.getValue());
        }
      });
  }
}
