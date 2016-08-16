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

/**
 * Listens to events from {@link JsonParser}
 * @author Michel Kraemer
 */
public interface JsonEventListener {
  /**
   * Will be called when the start of a JSON object is encountered
   */
  void onStartObject();
  
  /**
   * Will be called when the end of a JSON object is encountered
   */
  void onEndObject();
  
  /**
   * Will be called when the start of a JSON array is encountered
   */
  void onStartArray();
  
  /**
   * Will be called when the end of a JSON array is encountered
   */
  void onEndArray();
  
  /**
   * Will be called when a field name is encountered
   * @param name the field name
   */
  void onFieldName(String name);
  
  /**
   * Will be called when a string value is encountered
   * @param value the value
   */
  void onValue(String value);
  
  /**
   * Will be called when an integer value is encountered
   * @param value the value
   */
  void onValue(int value);
  
  /**
   * Will be called when a double value is encountered
   * @param value the value
   */
  void onValue(double value);
  
  /**
   * Will be called when a boolean value is encountered
   * @param value the value
   */
  void onValue(boolean value);
  
  /**
   * Will be called when a null value is encountered
   */
  void onValueNull();
}
