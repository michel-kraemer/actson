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

package de.undercouch.actson.buffer;

/**
 * A buffer provider creates new {@link Buffer} instances on demand. Since
 * {@link de.undercouch.actson.JsonParser} always only uses one buffer at a
 * time, for the sake of performance, the buffer provider may always return
 * the same {@link Buffer} instance but reset it (or clear its contents) when
 * needed.
 * @author Michel Kraemer
 * @since 2.1.0
 */
public interface BufferProvider {
  /**
   * Creates a new {@link Buffer} instance. Since
   * {@link de.undercouch.actson.JsonParser} always only uses one buffer at a
   * time, for the sake of performance, implementations of this method may
   * always return the same {@link Buffer} instance but reset it (or clear its
   * contents) when needed.
   * @return a new (or reused/recycled) buffer instance
   */
  Buffer newBuffer();
}
