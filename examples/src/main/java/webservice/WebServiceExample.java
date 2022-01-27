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

package webservice;

import de.undercouch.actson.JsonEvent;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import rx.Observable;
import rxjava.JsonParserOperator;
import rxjava.Result;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>This example combines Vert.x, RxJava and Actson to a reactive web service.
 * The HTTP service accepts JSON arrays and returns the number of elements in
 * this array. It can handle arbitrarily large files and multiple requests in
 * parallel without becoming unresponsive.</p>
 * <p>Start the application and then use curl to send a JSON file:</p>
 * <p><code>curl http://localhost:8080 --data @file.json</code></p>
 * @author Michel Kraemer
 */
public class WebServiceExample extends AbstractVerticle {
  /**
   * The main program
   * @param args the program arguments
   */
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.rxDeployVerticle(WebServiceExample.class.getName())
      .subscribe(v -> System.out.println("Web service successfully deployed"), err -> {
        err.printStackTrace();
        System.exit(1);
      });
  }

  @Override
  public void start(Promise<Void> startFuture) {
    // deploy the web server
    HttpServerOptions options = new HttpServerOptions()
        .setCompressionSupported(true);
    HttpServer server = vertx.createHttpServer(options);
    server.requestHandler(this::onRequest);
    server.rxListen(8080) // listen on port 8080
      .subscribe(v -> startFuture.complete(), startFuture::fail);
  }

  /**
   * Handle an HTTP request
   * @param request the HTTP request
   */
  private void onRequest(HttpServerRequest request) {
    // count the hierarchical level in the JSON file
    AtomicInteger level = new AtomicInteger(0);

    request.toObservable()
      .map(buf -> buf.getDelegate().getBytes())
      .lift(new JsonParserOperator()) // convert JSON file to JSON events
      .map(Result::getEvent)
      .flatMap(event -> {
        if (event == JsonEvent.EOF) {
          // skip EOF event
          return Observable.empty();
        }

        int l = level.get();
        if (l == 0 && event != JsonEvent.START_ARRAY) {
          // check that the top-level element is an array
          return Observable.error(new IllegalStateException("Array expected"));
        }

        // count hierarchical level
        if (event == JsonEvent.START_ARRAY || event == JsonEvent.START_OBJECT) {
          level.incrementAndGet();
        } else if (event == JsonEvent.END_ARRAY || event == JsonEvent.END_OBJECT) {
          level.decrementAndGet();
        }

        if (l == 1 && event != JsonEvent.END_ARRAY) {
          // count all elements in level 1 (the top-level array)
          return Observable.just(null);
        } else {
          // skip everything else
          return Observable.empty();
        }
      })
      .count() // count the number of elements
      .subscribe(v -> {
        // send counter back to client
        HttpServerResponse res = request.response();
        res.setStatusCode(200).end("{\"count:\": " + v + "}");
      }, err -> {
        // send error back to client
        HttpServerResponse res = request.response();
        if (err instanceof IllegalStateException) {
          res.setStatusCode(400);
        } else {
          res.setStatusCode(500);
        }
        res.end(err.getMessage());
      });
  }
}
