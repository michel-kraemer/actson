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

package de.undercouch.actson;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates how you can use the {@link JsonParser} to pretty-print
 * a JSON object or array. Note: this is no perfect implementation of a
 * pretty-printer. The output could still be nicer. It's just a sample
 * application.
 * @author Michel Kraemer
 */
public class PrettyPrinter {
  /**
   * Main method demonstrating how to use the pretty-printer
   * @param args program arguments
   */
  public static void main(String[] args) throws IOException {
    byte[] json = "{\"name\":\"Elvis \\u266b\"}".getBytes(StandardCharsets.UTF_8);

    JsonParser parser = new JsonParser();

    final JsonGenerator generator = JsonHelper.defaultGenerator(new OutputStreamWriter(System.out));
    JsonHelper.setPrettyPrint(generator);
    JsonHelper.regenerateJson(json, parser, generator);
    // expected output:
    // {
    //   "name: "Elvis ♫"
    // }
  }
}
