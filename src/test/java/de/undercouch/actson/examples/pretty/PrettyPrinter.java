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

package de.undercouch.actson.examples.pretty;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import de.undercouch.actson.JsonEvent;
import de.undercouch.actson.JsonParser;

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
  public static void main(String[] args) {
    byte[] json = "{\"name\":\"Elvis\"}".getBytes(StandardCharsets.UTF_8);
    
    JsonParser parser = new JsonParser();
    PrettyPrinter prettyPrinter = new PrettyPrinter();
    
    int i = 0;
    int event;
    do {
      while ((event = parser.nextEvent()) == JsonEvent.NEED_MORE_INPUT) {
        i += parser.getFeeder().feed(json, i, json.length - i);
        if (i == json.length) {
          parser.getFeeder().done();
        }
      }
      prettyPrinter.onEvent(event, parser);
    } while (event != JsonEvent.EOF);
    
    System.out.println(prettyPrinter.getResult());
    // expected output:
    // {
    //   "name: "Elvis"
    // }
  }
  
  private static enum Type {
    OBJECT, ARRAY
  }

  private StringBuilder result = new StringBuilder();
  private Deque<Type> types = new ArrayDeque<>();
  private Deque<Integer> elementCounts = new ArrayDeque<>();
  private int level;
  
  private void indent() {
    for (int i = 0; i < level; ++i) {
      result.append("  ");
    }
  }

  private void onStartObject() {
    onValue();
    result.append("{\n");
    level++;
    indent();
    elementCounts.push(0);
    types.push(Type.OBJECT);
  }

  private void onEndObject() {
    level--;
    result.append("\n");
    indent();
    result.append("}");
    elementCounts.pop();
    types.pop();
  }

  private void onStartArray() {
    onValue();
    result.append("[\n");
    level++;
    indent();
    elementCounts.push(0);
    types.push(Type.ARRAY);
  }

  private void onEndArray() {
    level--;
    result.append("\n");
    indent();
    result.append("]");
    elementCounts.pop();
    types.pop();
  }

  private void onFieldName(String name) {
    if (elementCounts.peek() > 0) {
      result.append(",\n");
      indent();
    }
    result.append("\"" + name + "\": ");
    elementCounts.push(elementCounts.pop() + 1);
  }

  private void onValue() {
    if (types.peek() == Type.ARRAY) {
      if (elementCounts.peek() > 0) {
        result.append(", ");
      }
      elementCounts.push(elementCounts.pop() + 1);
    }
  }

  private void onValue(String value) {
    onValue();
    result.append("\"" + value + "\"");
  }

  private void onValue(int value) {
    onValue();
    result.append(value);
  }

  private void onValue(double value) {
    onValue();
    result.append(value);
  }

  private void onValue(boolean value) {
    onValue();
    result.append(value);
  }

  private void onValueNull() {
    onValue();
    result.append("null");
  }
  
  /**
   * Call this method on every JSON event. It will generate pretty JSON text.
   * @param event the JSON event returned by the parser
   * @param parser the JSON parser
   */
  public void onEvent(int event, JsonParser parser) {
    switch (event) {
    case JsonEvent.START_OBJECT:
      onStartObject();
      break;
    case JsonEvent.END_OBJECT:
      onEndObject();
      break;
    case JsonEvent.START_ARRAY:
      onStartArray();
      break;
    case JsonEvent.END_ARRAY:
      onEndArray();
      break;
    case JsonEvent.FIELD_NAME:
      onFieldName(parser.getCurrentString());
      break;
    case JsonEvent.VALUE_STRING:
      onValue(parser.getCurrentString());
      break;
    case JsonEvent.VALUE_INT:
      onValue(parser.getCurrentInt());
      break;
    case JsonEvent.VALUE_DOUBLE:
      onValue(parser.getCurrentDouble());
      break;
    case JsonEvent.VALUE_TRUE:
      onValue(true);
      break;
    case JsonEvent.VALUE_FALSE:
      onValue(false);
      break;
    case JsonEvent.VALUE_NULL:
      onValueNull();
      break;
    case JsonEvent.EOF:
      break;
    default:
      throw new IllegalArgumentException("Unknown event: " + event);
    }
  }
  
  /**
   * @return the pretty JSON string
   */
  public String getResult() {
    return result.toString();
  }
}
