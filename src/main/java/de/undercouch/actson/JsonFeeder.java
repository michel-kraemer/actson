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
 * A feeder is used by {@link JsonParser} to get more input to parse.
 * @author Michel Kraemer
 */
public interface JsonFeeder {
  /**
   * Provide more data to the {@link JsonParser}. Should only be called if
   * {@link #isFull()} returns <code>false</code>.
   * @param b the byte to provide as input to the parser
   * @throws IllegalStateException if the parser does not accept more input at
   * the moment (see {@link #isFull()})
   */
  void feed(byte b);
  
  /**
   * Checks if the parser accepts more input at the moment. If it doesn't,
   * you have to call {@link JsonParser#nextEvent()} until it returns
   * {@link JsonEvent#NEED_MORE_INPUT}. Only then new input can be provided
   * to the parser.
   * @return true if the parser does not accept more input
   */
  boolean isFull();
  
  /**
   * Call this method to indicate that the end of the JSON text has been
   * reached and that there is no more input to parse.
   */
  void done();
}
