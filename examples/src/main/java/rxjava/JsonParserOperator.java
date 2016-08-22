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

package rxjava;

import de.undercouch.actson.JsonEvent;
import de.undercouch.actson.JsonParser;
import rx.Observable.Operator;
import rx.Subscriber;

/**
 * A reusable operator function that you can lift into an RxJava
 * {@link rx.Observable}. It transforms byte arrays into JSON events.
 * @author Michel Kraemer
 */
public class JsonParserOperator implements Operator<Result, byte[]> {
  /**
   * Non-blocking JSON parser
   */
  private JsonParser parser = new JsonParser();

  /**
   * Process events from the parser until it needs more input. Notify the
   * subscriber accordingly.
   * @param s the subscriber
   * @return true if the caller should continue parsing, false if there was an
   * error or if the end of the JSON text has been reached
   */
  private boolean processEvents(Subscriber<? super Result> s) {
    int event;
    do {
      event = parser.nextEvent();

      // handle event and notify subscriber
      if (event == JsonEvent.VALUE_STRING) {
        // forward string values to subscriber
        s.onNext(new Result(event, parser.getCurrentString()));
      } else if (event == JsonEvent.EOF) {
        // notify the subscriber that the observable has finished
        s.onNext(new Result(event));
        s.onCompleted();
        return false;
      } else if (event == JsonEvent.ERROR) {
        // notify the subscriber about the error
        s.onError(new IllegalStateException("Syntax error"));
        return false;
      } else if (event != JsonEvent.NEED_MORE_INPUT) {
        // forward JSON event
        s.onNext(new Result(event));
      }
    } while (event != JsonEvent.NEED_MORE_INPUT);

    return true;
  }

  @Override
  public Subscriber<? super byte[]> call(Subscriber<? super Result> s) {
    return new Subscriber<byte[]>(s) {
      @Override
      public void onCompleted() {
        if (!s.isUnsubscribed()) {
          // finish parsing and forward events to the
          // subscriber (including the EOF event)
          parser.getFeeder().done();
          processEvents(s);
        }
      }

      @Override
      public void onError(Throwable e) {
        if (!s.isUnsubscribed()) {
          s.onError(e);
        }
      }

      @Override
      public void onNext(byte[] buf) {
        if (s.isUnsubscribed()) {
          return;
        }

        // push bytes into the parser and then process JSON events
        int i = 0;
        while (i < buf.length) {
          i += parser.getFeeder().feed(buf, i, buf.length - i);
          if (!processEvents(s)) {
            break;
          }
        }
      }
    };
  }
}
