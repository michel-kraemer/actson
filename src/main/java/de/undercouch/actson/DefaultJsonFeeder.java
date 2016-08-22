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
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

/**
 * Default implementation of {@link JsonFeeder} used internally by
 * the {@link JsonParser}.
 * @author Michel Kraemer
 * @since 1.0.0
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
    if (isFull()) {
      throw new IllegalStateException("JSON parser is full");
    }
    byteBuf.put(b);
  }

  @Override
  public int feed(byte[] buf) {
    return feed(buf, 0, buf.length);
  }

  @Override
  public int feed(byte[] buf, int offset, int len) {
    int i = offset;
    int j = offset + len;
    int position = byteBuf.position();
    int limit = byteBuf.limit();
    byte[] arr = byteBuf.array();
    while (i < j && position < limit) {
      arr[position] = buf[i];
      ++i;
      ++position;
    }
    byteBuf.position(position);
    return i - offset;
  }

  @Override
  public void done() {
    done = true;
  }

  @Override
  public boolean isFull() {
    return !byteBuf.hasRemaining();
  }

  @Override
  public boolean hasInput() throws CharacterCodingException {
    return fillBuffer();
  }

  @Override
  public boolean isDone() throws CharacterCodingException {
    return done && !hasInput();
  }

  @Override
  public char nextInput() throws CharacterCodingException {
    if (!hasInput()) {
      throw new IllegalStateException("Not enough input data");
    }
    return charBuf.get();
  }

  /**
   * Decode bytes from {@link #byteBuf} and fill {@link #charBuf}. This method
   * is a no-op if {@link #charBuf} is not empty or if there are no bytes to
   * decode.
   * @return true if the buffer contains bytes now, false if it's still empty
   * @throws CharacterCodingException if the input data contains invalid
   * characters
   */
  private boolean fillBuffer() throws CharacterCodingException {
    if (charBuf.hasRemaining()) {
      return true;
    }
    if (byteBuf.position() == 0) {
      return false;
    }

    charBuf.position(0);
    charBuf.limit(charBuf.capacity());
    byteBuf.flip();

    CoderResult result = decoder.decode(byteBuf, charBuf, done);
    if (result.isMalformed()) {
      throw new MalformedInputException(result.length());
    }
    if (result.isUnmappable()) {
      throw new UnmappableCharacterException(result.length());
    }

    charBuf.flip();
    byteBuf.compact();

    return charBuf.hasRemaining();
  }
}
