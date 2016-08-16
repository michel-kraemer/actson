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

import java.util.ArrayDeque;
import java.util.Deque;

import de.undercouch.actson.JsonEventListener;
import de.undercouch.actson.JsonParser;

/**
 * Demonstrates how you can use {@link JsonEventListener} to pretty-print
 * a JSON object or array. Note: this is no perfect implementation of a
 * pretty-printer. The output could still be nicer. But it demonstrates
 * very well how to use the event listener.
 * @author Michel Kraemer
 */
public class PrettyPrinter implements JsonEventListener {
  /**
   * Main method demonstrating how to use the pretty-printer
   * @param args program arguments
   */
  public static void main(String[] args) {
    String json = "{\"name\":\"Elvis\"}";
    
    JsonParser parser = new JsonParser();
    PrettyPrinter prettyPrinter = new PrettyPrinter();
    parser.addListener(prettyPrinter);
    
    for (int i = 0; i < json.length(); ++i) {
      parser.feed(json.charAt(i));
    }
    parser.done();
    
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

  @Override
  public void onStartObject() {
    onValue();
    result.append("{\n");
    level++;
    indent();
    elementCounts.push(0);
    types.push(Type.OBJECT);
  }

  @Override
  public void onEndObject() {
    level--;
    result.append("\n");
    indent();
    result.append("}");
    elementCounts.pop();
    types.pop();
  }

  @Override
  public void onStartArray() {
    onValue();
    result.append("[\n");
    level++;
    indent();
    elementCounts.push(0);
    types.push(Type.ARRAY);
  }

  @Override
  public void onEndArray() {
    level--;
    result.append("\n");
    indent();
    result.append("]");
    elementCounts.pop();
    types.pop();
  }

  @Override
  public void onFieldName(String name) {
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

  @Override
  public void onValue(String value) {
    onValue();
    result.append("\"" + value + "\"");
  }

  @Override
  public void onValue(int value) {
    onValue();
    result.append(value);
  }

  @Override
  public void onValue(double value) {
    onValue();
    result.append(value);
  }

  @Override
  public void onValue(boolean value) {
    onValue();
    result.append(value);
  }

  @Override
  public void onValueNull() {
    onValue();
    result.append("null");
  }
  
  /**
   * @return the pretty JSON string
   */
  public String getResult() {
    return result.toString();
  }
}
