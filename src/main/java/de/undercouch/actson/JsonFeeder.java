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

import java.nio.charset.CharacterCodingException;

/**
 * A feeder can be used to provide more input data to the {@link JsonParser}.
 * The caller has to take care to only feed as much data as the parser can
 * process at the time. Use {@link #isFull()} to determine if the parser
 * accepts more data. Then call {@link #feed(byte)} or {@link #feed(byte[], int, int)}
 * until there is no more data to feed or until {@link #isFull()} returns
 * <code>true</code>. Next call {@link JsonParser#nextEvent()} until it returns
 * {@link JsonEvent#NEED_MORE_INPUT}. Repeat feeding and parsing until all
 * input data has been consumed. Finally, call {@link #done()} to indicate the
 * end of the JSON text.
 * @author Michel Kraemer
 * @since 1.0.0
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
   * Provide more data to the {@link JsonParser}. The method will consume as
   * many bytes from the input buffer as possible, either until all bytes have
   * been consumed or until the feeder is full (see {@link #isFull()}).
   * The method will return the number of bytes consumed (which can be 0 if the
   * parser does not accept more input at the moment).
   * @param buf the byte array containing the data to consume
   * @return the number of bytes consumed (can be 0 if the parser does not accept
   * more input at the moment, see {@link #isFull()})
   */
  int feed(byte[] buf);

  /**
   * Provide more data to the {@link JsonParser}. The method will consume as
   * many bytes from the input buffer as possible, either until <code>len</code>
   * bytes have been consumed or until the feeder is full (see {@link #isFull()}).
   * The method will return the number of bytes consumed (which can be 0 if the
   * parser does not accept more input at the moment).
   * @param buf the byte array containing the data to consume
   * @param offset the start offset in the byte array
   * @param len the number of bytes to consume
   * @return the number of bytes consumed (can be 0 if the parser does not accept
   * more input at the moment, see {@link #isFull()})
   */
  int feed(byte[] buf, int offset, int len);

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

  /**
   * Determine if the feeder has input data that can be parsed
   * @return true if the feeder has more input to be parsed
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  boolean hasInput() throws CharacterCodingException;

  /**
   * Check if the end of the JSON text has been reached
   * @return true if the end of input has been reached
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  boolean isDone() throws CharacterCodingException;

  /**
   * Decode and return the next character to be parsed
   * @return the next character to be parsed
   * @throws IllegalStateException if there is no input to parse
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  char nextInput() throws CharacterCodingException;
}
