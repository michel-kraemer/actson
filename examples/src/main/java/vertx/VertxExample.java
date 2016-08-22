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

package vertx;

import java.util.function.Supplier;

import de.undercouch.actson.JsonEvent;
import de.undercouch.actson.JsonParser;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

/**
 * This example demonstrates how Actson can be used together with Vert.x. It
 * does the same as {@link simple.SimpleExample} or {@link rxjava.RxJavaExample}
 * but works completely asynchronously and non-blocking.
 * @author Michel Kraemer
 */
public class VertxExample {
  private Vertx vertx = Vertx.vertx();

  /**
   * The main program. Accepts one argument: the name of the file to parse
   * @param args the program arguments
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: VertxExample <json file>");
      System.exit(1);
    }

    VertxExample example = new VertxExample();
    example.parseFile(args[0], ar -> {
      if (ar.failed()) {
        System.err.println("Could not parse file");
        ar.cause().printStackTrace();
        System.exit(1);
      } else {
        System.exit(0);
      }
    });
  }

  /**
   * Asynchronously parse a file and print JSON events to System.out
   * @param filename the name of the JSON file to parse
   * @param handler a handler that will be called when the file has been parsed
   * or when an error has occurred
   */
  private void parseFile(String filename, Handler<AsyncResult<Void>> handler) {
    OpenOptions options = new OpenOptions()
        .setRead(true)
        .setWrite(false);

    // asynchronously open the file
    vertx.fileSystem().open(filename, options, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }

      JsonParser parser = new JsonParser();
      AsyncFile f = ar.result();

      Supplier<Boolean> processEvents = () -> {
        // process events from the parser until it needs more input
        int event;
        do {
          event = parser.nextEvent();

          // print all events to System.out
          if (event != JsonEvent.NEED_MORE_INPUT) {
            System.out.println("JSON event: " + event);
          }

          // handle values, errors, and end of file
          if (event == JsonEvent.VALUE_STRING) {
            System.out.println("VALUE: " + parser.getCurrentString());
          } else if (event == JsonEvent.EOF) {
            handler.handle(Future.succeededFuture());
            return false;
          } else if (event == JsonEvent.ERROR) {
            handler.handle(Future.failedFuture("Syntax error"));
            return false;
          }
        } while (event != JsonEvent.NEED_MORE_INPUT);

        return true;
      };

      f.exceptionHandler(t -> {
        handler.handle(Future.failedFuture(t));
      });

      f.handler(buf -> {
        // forward bytes read from the file to the parser
        byte[] bytes = buf.getBytes();
        int i = 0;
        while (i < bytes.length) {
          i += parser.getFeeder().feed(bytes, i, bytes.length - i);
          if (!processEvents.get()) {
            f.handler(null);
            f.endHandler(null);
            break;
          }
        }
      });

      f.endHandler(v -> {
        // process events one last time
        parser.getFeeder().done();
        processEvents.get();
      });
    });
  }
}
