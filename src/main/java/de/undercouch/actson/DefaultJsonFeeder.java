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
 * Default implementation of {@link JsonFeeder} used internally by
 * the {@link JsonParser}.
 * @author Michel Kraemer
 */
public class DefaultJsonFeeder implements JsonFeeder {
  private boolean done = false;
  private boolean full = false;
  private char c;
  
  @Override
  public void feed(char c) {
    if (full) {
      throw new IllegalStateException("JSON parser is full");
    }
    this.c = c;
    full = true;
  }

  @Override
  public void done() {
    done = true;
  }
  
  @Override
  public boolean isFull() {
    return full;
  }
  
  /**
   * @return true if the feeder has more input to be parsed
   */
  public boolean hasInput() {
    return full;
  }
  
  /**
   * @return true of the end of input has been reached
   */
  public boolean isDone() {
    return !hasInput() && done;
  }
  
  /**
   * @return the next character to be parsed
   * @throws IllegalStateException if there is no input to parse
   */
  public char nextInput() {
    if (!full) {
      throw new IllegalStateException("Not enough input data");
    }
    full = false;
    return c;
  }
}
