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

package de.undercouch.actson;

import org.junit.jupiter.api.Test;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnmappableCharacterException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link DefaultJsonFeeder}
 * @author Michel Kraemer
 */
public class DefaultJsonFeederTest {
  private final DefaultJsonFeeder feeder = new DefaultJsonFeeder(
      StandardCharsets.UTF_8, 16);

  /**
   * Test if the feeder is empty at the beginning
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void emptyAtBeginning() throws CharacterCodingException {
    assertThat(feeder.hasInput()).isFalse();
    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.isDone()).isFalse();
  }

  /**
   * Test if the {@link DefaultJsonFeeder#hasInput()} method works correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void hasInput() throws CharacterCodingException {
    feeder.feed((byte)'a');
    assertThat(feeder.hasInput()).isTrue();
  }

  /**
   * Test if the {@link DefaultJsonFeeder#isFull()} method works correctly
   */
  @Test
  public void isFull() {
    for (int i = 0; i < 16; ++i) {
      assertThat(feeder.isFull()).isFalse();
      feeder.feed((byte)('a' + i));
    }
    assertThat(feeder.isFull()).isTrue();
  }

  /**
   * Test if the feeder accepts a byte array
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void feedBuf() throws CharacterCodingException {
    byte[] buf = "abcd".getBytes(StandardCharsets.UTF_8);

    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.hasInput()).isFalse();

    feeder.feed(buf);

    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.hasInput()).isTrue();

    assertThat(feeder.nextInput()).isEqualTo('a');
    assertThat(feeder.nextInput()).isEqualTo('b');
    assertThat(feeder.nextInput()).isEqualTo('c');
    assertThat(feeder.nextInput()).isEqualTo('d');
    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.hasInput()).isFalse();

    feeder.feed(buf);
    assertThat(feeder.isFull()).isFalse();
    feeder.feed(buf);
    assertThat(feeder.isFull()).isFalse();
    feeder.feed(buf);
    assertThat(feeder.isFull()).isFalse();
    feeder.feed(buf);
    assertThat(feeder.isFull()).isTrue();
  }

  /**
   * Test if the feeder accepts a byte array
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void feedPartialBuf() throws CharacterCodingException {
    byte[] buf = "----------------------abcd---------------"
        .getBytes(StandardCharsets.UTF_8);

    feeder.feed(buf, 22, 4);

    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.nextInput()).isEqualTo('a');
    assertThat(feeder.nextInput()).isEqualTo('b');
    assertThat(feeder.nextInput()).isEqualTo('c');
    assertThat(feeder.nextInput()).isEqualTo('d');
    assertThat(feeder.isFull()).isFalse();
    assertThat(feeder.hasInput()).isFalse();
  }

  /**
   * Test if the {@link DefaultJsonFeeder#isDone()} method works correctly
   * @throws CharacterCodingException if something goes wrong
   */
  @Test
  public void isDone() throws CharacterCodingException {
    assertThat(feeder.isDone()).isFalse();
    feeder.feed((byte)'a');
    assertThat(feeder.isDone()).isFalse();
    feeder.done();
    assertThat(feeder.isDone()).isFalse();
    feeder.nextInput();
    assertThat(feeder.isDone()).isTrue();
  }

  /**
   * Test if the feeder throws an exception if it is full
   */
  @Test
  public void tooFull() {
    assertThatThrownBy(() -> {
      for (int i = 0; i < 17; ++i) {
        feeder.feed((byte)('a' + i));
      }
    }).isInstanceOf(IllegalStateException.class);
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
        assertThat(feeder.nextInput()).isEqualTo(expected.charAt(j));
        ++j;
      }
    }
    assertThat(j).isEqualTo(expected.length());
    assertThat(feeder.hasInput()).isFalse();
    assertThat(feeder.isFull()).isFalse();
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
    assertThat(feeder.hasInput()).isFalse();
    assertThatThrownBy(feeder::nextInput).isInstanceOf(IllegalStateException.class);

    // first part of a UTF-8 character
    feeder.feed((byte)197);
    assertThat(feeder.hasInput()).isFalse();
    assertThatThrownBy(feeder::nextInput).isInstanceOf(IllegalStateException.class);

    // second part of a UTF-8 character
    feeder.feed((byte)147);
    assertThat(feeder.hasInput()).isTrue();

    // '\u0153' == [ (byte)197, (byte)147 ]
    char c = feeder.nextInput();
    assertThat(c).isEqualTo('\u0153');
    assertThat(feeder.hasInput()).isFalse();
  }

  /**
   * Tests if the feeder throws an exception if the input is malformed
   */
  @Test
  public void malformedInput() {
    feeder.feed((byte)0xff);
    feeder.feed((byte)0xff);
    assertThatThrownBy(feeder::nextInput).isInstanceOf(MalformedInputException.class);
  }

  /**
   * Tests if the feeder throws an exception if one of the output characters
   * is unmappable
   */
  @Test
  public void unmappableCharacter() {
    DefaultJsonFeeder feeder = new DefaultJsonFeeder(
        Charset.forName("IBM1098"));
    feeder.feed((byte)0x80);
    feeder.feed((byte)0x81);
    assertThatThrownBy(() -> {
      while (feeder.hasInput()) {
        System.out.println(feeder.nextInput());
      }
    }).isInstanceOf(UnmappableCharacterException.class);
  }
}
