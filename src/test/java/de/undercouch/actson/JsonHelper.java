package de.undercouch.actson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.Writer;

public class JsonHelper {
  public static JsonGenerator defaultGenerator(Writer writer) {
    try {
      return new JsonFactory().createGenerator(writer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static JsonGenerator setPrettyPrint(JsonGenerator generator) {
    return generator.setPrettyPrinter(new DefaultPrettyPrinter());
  }

  /**
   * Parse input JSON byte array with Actson and generate JSON text again with Jackson.
   * Is intended for demonstration and tests.
   */
  public static void regenerateJson(final byte[] sourceJson, JsonParser parser, JsonGenerator generator) {
    try {
      int i = 0;
      int event;
      do {
        while ((event = parser.nextEvent()) == JsonEvent.NEED_MORE_INPUT) {
          i += parser.getFeeder().feed(sourceJson, i, sourceJson.length - i);
          if (i == sourceJson.length) {
            parser.getFeeder().done();
          }
        }
        eventToGenerator(event, parser, generator);
      } while (event != JsonEvent.EOF);
      generator.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * To be called on JSON event to generate JSON text.
   *
   * @param event     the JSON event returned by the parser
   * @param parser    the JSON parser
   * @param generator send the event to this JSON text generator
   */
  public static void eventToGenerator(int event, JsonParser parser, JsonGenerator generator) throws IOException {
    switch (event) {
      case JsonEvent.START_OBJECT:
        generator.writeStartObject();
        break;
      case JsonEvent.END_OBJECT:
        generator.writeEndObject();
        break;
      case JsonEvent.START_ARRAY:
        generator.writeStartArray();
        break;
      case JsonEvent.END_ARRAY:
        generator.writeEndArray();
        break;
      case JsonEvent.FIELD_NAME:
        generator.writeFieldName(parser.getCurrentString());
        break;
      case JsonEvent.VALUE_STRING:
        generator.writeString(parser.getCurrentString());
        break;
      case JsonEvent.VALUE_INT:
        generator.writeNumber(parser.getCurrentInt());
        break;
      case JsonEvent.VALUE_DOUBLE:
        generator.writeNumber(parser.getCurrentDouble());
        break;
      case JsonEvent.VALUE_TRUE:
        generator.writeBoolean(true);
        break;
      case JsonEvent.VALUE_FALSE:
        generator.writeBoolean(false);
        break;
      case JsonEvent.VALUE_NULL:
        generator.writeNull();
        break;
      case JsonEvent.EOF:
        break;
      case JsonEvent.ERROR:
        throw new IllegalArgumentException("JSON syntax error.");
      default:
        throw new IllegalArgumentException("Unknown event: " + event);
    }
  }
}
