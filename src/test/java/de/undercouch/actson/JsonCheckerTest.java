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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Tests {@link JsonChecker}
 * @author Michel Kraemer
 */
public class JsonCheckerTest {
  /**
   * Test if valid files can be parsed correctly
   * @throws IOException if one of the test files could not be read
   */
  @Test
  public void testPass() throws IOException {
    for (int i = 1; i <= 3; ++i) {
      URL u = getClass().getResource("pass" + i + ".txt");
      String json = IOUtils.toString(u, "UTF-8");
      JsonChecker checker = new JsonChecker(20);
      for (int j = 0; j < json.length(); ++j) {
        assertTrue(checker.feed(json.charAt(j)));
      }
      assertTrue(checker.done());
    }
  }
  
  /**
   * Test if invalid files cannot be parsed
   * @throws IOException if one of the test files could not be read
   */
  @Test
  public void testFail() throws IOException {
    for (int i = 1; i <= 33; ++i) {
      URL u = getClass().getResource("fail" + i + ".txt");
      String json = IOUtils.toString(u, "UTF-8");
      JsonChecker checker = new JsonChecker(20);
      boolean ok = true;
      for (int j = 0; j < json.length(); ++j) {
        ok &= checker.feed(json.charAt(j));
        if (!ok) {
          break;
        }
      }
      if (ok) {
        ok &= checker.done();
      }
      assertFalse(ok);
    }
  }
}
