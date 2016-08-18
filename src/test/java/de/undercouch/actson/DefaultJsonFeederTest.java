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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Tests {@link DefaultJsonFeeder}
 * @author Michel Kraemer
 */
public class DefaultJsonFeederTest {
  private DefaultJsonFeeder feeder = new DefaultJsonFeeder(
      StandardCharsets.UTF_8, 16);
  
  /**
   * Test if the feeder is empty at the beginning
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void emptyAtBeginning() throws CharacterCodingException {
    assertFalse(feeder.hasInput());
    assertFalse(feeder.isFull());
    assertFalse(feeder.isDone());
  }
  
  /**
   * Test if the {@link DefaultJsonFeeder#hasInput()} method works correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void hasInput() throws CharacterCodingException {
    feeder.feed((byte)'a');
    assertTrue(feeder.hasInput());
  }
  
  /**
   * Test if the {@link DefaultJsonFeeder#isFull()} method works correctly
   */
  @Test
  public void isFull() {
    for (int i = 0; i < 16; ++i) {
      assertFalse(feeder.isFull());
      feeder.feed((byte)('a' + i));
    }
    assertTrue(feeder.isFull());
  }
  
  /**
   * Test if a given string can be decoded correctly
   * @param expected the string to decode
   * @throws CharacterCodingException if something goes wrong
   */
  private void assertString(String expected) throws CharacterCodingException {
    byte[] input = expected.getBytes(StandardCharsets.UTF_8);
    int i = 0;
    int j = 0;
    while (i < input.length) {
      while (!feeder.isFull() && i < input.length) {
        feeder.feed(input[i]);
        ++i;
      }
      while (feeder.hasInput()) {
        assertEquals(expected.charAt(j), feeder.nextInput());
        ++j;
      }
    }
    assertEquals(expected.length(), j);
    assertFalse(feeder.hasInput());
    assertFalse(feeder.isFull());
  }
  
  /**
   * Test if a short string can be decoded correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void shortString() throws CharacterCodingException {
    assertString("abcdef");
  }
  
  /**
   * Test if a long string (longer than the feeder's buffer size)
   * can be decoded correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void longString() throws CharacterCodingException {
    assertString("abcdefghijklmnopqrstuvwxyz");
  }
  
  /**
   * Test if a very long string (much longer than the feeder's buffer size)
   * can be decoded correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void veryLongString() throws CharacterCodingException {
    assertString("abcdefghijklmnopqrstuvwxyz" +
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "abcdefghijklmnopqrstuvwxyz");
  }
  
  /**
   * Test if a short string containing unicode characters can be decoded
   * correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void shortUnicode() throws CharacterCodingException {
    assertString("abcd\u0153f");
  }
  
  /**
   * Test if a long string (longer than the feeder's buffer size) containing
   * unicode characters can be decoded correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void longUnicode() throws CharacterCodingException {
    char[] str = new char[26];
    for (int i = 0; i < str.length; ++i) {
      str[i] = (char)(0x110 + i);
    }
    assertString(new String(str));
  }
  
  /**
   * Test if a long string (longer than the feeder's buffer size) containing
   * normal and unicode characters can be decoded correctly. The test ensures
   * the feeder can also handle partial unicode characters.
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void mixedLongUnicode() throws CharacterCodingException {
    char[] str = new char[26];
    for (int i = 0; i < str.length; ++i) {
      if (i % 3 == 0) {
        str[i] = (char)('a' + i);
      } else {
        str[i] = (char)(0x110 + i);
      }
    }
    assertString(new String(str));
  }
  
  /**
   * Test if a very long string (much longer than the feeder's buffer size)
   * containing normal and unicode characters can be decoded correctly. The
   * test ensures the feeder can also handle partial unicode characters.
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void mixedVeryLongUnicode() throws CharacterCodingException {
    char[] str = new char[26 * 13];
    for (int i = 0; i < str.length; ++i) {
      if (i % 3 == 0) {
        str[i] = (char)('a' + (i % 26));
      } else {
        str[i] = (char)(0x110 + i);
      }
    }
    assertString(new String(str));
  }
  
  /**
   * Test if the feeder can handle partial unicode characters
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void partialUnicode() throws CharacterCodingException {
    assertFalse(feeder.hasInput());
    try {
      feeder.nextInput();
      fail();
    } catch (IllegalStateException e) {
      // OK
    }
    
    // first part of a UTF-8 character
    feeder.feed((byte)197);
    assertFalse(feeder.hasInput());
    try {
      feeder.nextInput();
      fail();
    } catch (IllegalStateException e) {
      // OK
    }
    
    // second part of a UTF-8 character
    feeder.feed((byte)147);
    assertTrue(feeder.hasInput());
    
    // '\u0153' == [ (byte)197, (byte)147 ]
    char c = feeder.nextInput();
    assertEquals('\u0153', c);
    assertFalse(feeder.hasInput());
  }
}
