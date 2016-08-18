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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/**
 * Default implementation of {@link JsonFeeder} used internally by
 * the {@link JsonParser}.
 * @author Michel Kraemer
 */
public class DefaultJsonFeeder implements JsonFeeder {
  private final ByteBuffer byteBuf;
  private final CharBuffer charBuf;
  private final CharsetDecoder decoder;
  private boolean done = false;
  
  /**
   * Constructs a feeder
   * @param charset the charset that should be used to decode input data
   */
  public DefaultJsonFeeder(Charset charset) {
    this(charset, 1024);
  }
  
  /**
   * Constructs a feeder
   * @param charset the charset that should be used to decode input data
   * @param capacity the capacity of the internal byte buffer caching input data
   */
  public DefaultJsonFeeder(Charset charset, int capacity) {
    byteBuf = ByteBuffer.allocate(capacity);
    charBuf = CharBuffer.allocate(capacity * 2);
    charBuf.limit(0);
    decoder = charset.newDecoder();
  }
  
  @Override
  public void feed(byte b) {
    if (!byteBuf.hasRemaining()) {
      throw new IllegalStateException("JSON parser is full");
    }
    byteBuf.put(b);
  }
  
  @Override
  public void done() {
    done = true;
  }
  
  @Override
  public boolean isFull() {
    return !byteBuf.hasRemaining();
  }
  
  /**
   * @return true if the feeder has more input to be parsed
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  public boolean hasInput() throws CharacterCodingException {
    fillBuffer();
    return charBuf.hasRemaining();
  }
  
  /**
   * @return true if the end of input has been reached
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  public boolean isDone() throws CharacterCodingException {
    return done && !hasInput();
  }
  
  /**
   * @return the next character to be parsed
   * @throws IllegalStateException if there is no input to parse
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  public char nextInput() throws CharacterCodingException {
    if (!hasInput()) {
      throw new IllegalStateException("Not enough input data");
    }
    char c = charBuf.get();
    return c;
  }
  
  /**
   * Decode bytes from {@link #byteBuf} and fill {@link #charBuf}. This method
   * is a no-op if {@link #charBuf} is not empty or if there are no bytes to
   * decode.
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  private void fillBuffer() throws CharacterCodingException {
    if (charBuf.hasRemaining()) {
      return;
    }
    if (byteBuf.position() == 0) {
      return;
    }
    
    charBuf.position(0);
    charBuf.limit(charBuf.capacity());
    byteBuf.flip();
    
    CoderResult result = decoder.decode(byteBuf, charBuf, done);
    if (result.isError()) {
      result.throwException();
    }
    
    charBuf.flip();
    byteBuf.compact();
  }
}
