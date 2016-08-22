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
 * A class containing all possible JSON events returned by
 * {@link JsonParser#nextEvent()}
 * @author Michel Kraemer
 * @since 1.0.0
 */
public interface JsonEvent {
  /**
   * The JSON text contains a syntax error.
   */
  public static final int ERROR = -1;

  /**
   * The JSON parser needs more input before the next event can be returned.
   * Invoke the parser's feeder to give it more input.
   */
  public static final int NEED_MORE_INPUT = 0;

  /**
   * The start of a JSON object.
   */
  public static final int START_OBJECT = 1;

  /**
   * The end of a JSON object.
   */
  public static final int END_OBJECT = 2;

  /**
   * The start of a JSON array.
   */
  public static final int START_ARRAY = 3;

  /**
   * The end of a JSON array.
   */
  public static final int END_ARRAY = 4;

  /**
   * A field name. Call {@link JsonParser#getCurrentString()}
   * to get the name.
   */
  public static final int FIELD_NAME = 5;

  /**
   * A string value. Call {@link JsonParser#getCurrentString()}
   * to get the value.
   */
  public static final int VALUE_STRING = 6;

  /**
   * An integer value. Call {@link JsonParser#getCurrentInt()}
   * to get the value.
   */
  public static final int VALUE_INT = 7;

  /**
   * A double value. Call {@link JsonParser#getCurrentDouble()}
   * to get the value.
   */
  public static final int VALUE_DOUBLE = 8;

  /**
   * The boolean value <code>true</code>.
   */
  public static final int VALUE_TRUE = 9;

  /**
   * The boolean value <code>false</code>.
   */
  public static final int VALUE_FALSE = 10;

  /**
   * A <code>null</code> value.
   */
  public static final int VALUE_NULL = 11;

  /**
   * The end of the JSON text
   */
  public static final int EOF = 99;
}
