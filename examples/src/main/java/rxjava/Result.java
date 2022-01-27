// MIT License
//
// Copyright (c) 2016-2022 Michel Kraemer
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

/**
 * Small helper class that is used by {@link JsonParserOperator} to send
 * JSON events and values to subscribers
 * @author Michel Kraemer
 */
public class Result {
  private final int event;
  private final Object value;

  /**
   * Create a new result
   * @param event the JSON event
   */
  public Result(int event) {
    this(event, null);
  }

  /**
   * Create a new result
   * @param event the JSON event
   * @param value the value
   */
  public Result(int event, Object value) {
    this.event = event;
    this.value = value;
  }

  /**
   * @return the JSON event
   */
  public int getEvent() {
    return event;
  }

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }
}
