// MIT License
//
// Copyright (c) 2016-2022 Michel Kraemer
// Copyright (c) 2005 JSON.org
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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * <p>A non-blocking, event-based JSON parser.</p>
 * <p>The parser gets input data from a feeder that can be accessed through
 * {@link #getFeeder()}. See {@link JsonFeeder} for more details.</p>
 * @author Michel Kraemer
 * @author JSON.org
 * @since 1.0.0
 */
public class JsonParser {
  private static final byte __ = -1; // the universal error code

  /**
   * Characters are mapped into these 31 character classes. This allows for
   * a significant reduction in the size of the state transition table.
   */
  private static final byte C_SPACE =  0;  // space
  private static final byte C_WHITE =  1;  // other whitespace
  private static final byte C_LCURB =  2;  // {
  private static final byte C_RCURB =  3;  // }
  private static final byte C_LSQRB =  4;  // [
  private static final byte C_RSQRB =  5;  // ]
  private static final byte C_COLON =  6;  // :
  private static final byte C_COMMA =  7;  // ,
  private static final byte C_QUOTE =  8;  // "
  private static final byte C_BACKS =  9;  // \
  private static final byte C_SLASH = 10;  // /
  private static final byte C_PLUS  = 11;  // +
  private static final byte C_MINUS = 12;  // -
  private static final byte C_POINT = 13;  // .
  private static final byte C_ZERO  = 14;  // 0
  private static final byte C_DIGIT = 15;  // 123456789
  private static final byte C_LOW_A = 16;  // a
  private static final byte C_LOW_B = 17;  // b
  private static final byte C_LOW_C = 18;  // c
  private static final byte C_LOW_D = 19;  // d
  private static final byte C_LOW_E = 20;  // e
  private static final byte C_LOW_F = 21;  // f
  private static final byte C_LOW_L = 22;  // l
  private static final byte C_LOW_N = 23;  // n
  private static final byte C_LOW_R = 24;  // r
  private static final byte C_LOW_S = 25;  // s
  private static final byte C_LOW_T = 26;  // t
  private static final byte C_LOW_U = 27;  // u
  private static final byte C_ABCDF = 28;  // ABCDF
  private static final byte C_E     = 29;  // E
  private static final byte C_ETC   = 30;  // everything else

  /**
   * This array maps the 128 ASCII characters into character classes.
   * The remaining Unicode characters should be mapped to C_ETC.
   * Non-whitespace control characters are errors.
   */
  private final static byte[] ascii_class = {
    __,      __,      __,      __,      __,      __,      __,      __,
    __,      C_WHITE, C_WHITE, __,      __,      C_WHITE, __,      __,
    __,      __,      __,      __,      __,      __,      __,      __,
    __,      __,      __,      __,      __,      __,      __,      __,

    C_SPACE, C_ETC,   C_QUOTE, C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_PLUS,  C_COMMA, C_MINUS, C_POINT, C_SLASH,
    C_ZERO,  C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT, C_DIGIT,
    C_DIGIT, C_DIGIT, C_COLON, C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,

    C_ETC,   C_ABCDF, C_ABCDF, C_ABCDF, C_ABCDF, C_E,     C_ABCDF, C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_LSQRB, C_BACKS, C_RSQRB, C_ETC,   C_ETC,

    C_ETC,   C_LOW_A, C_LOW_B, C_LOW_C, C_LOW_D, C_LOW_E, C_LOW_F, C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_ETC,   C_LOW_L, C_ETC,   C_LOW_N, C_ETC,
    C_ETC,   C_ETC,   C_LOW_R, C_LOW_S, C_LOW_T, C_LOW_U, C_ETC,   C_ETC,
    C_ETC,   C_ETC,   C_ETC,   C_LCURB, C_ETC,   C_RCURB, C_ETC,   C_ETC
  };

  /**
   * The state codes.
   */
  private static final byte GO =  0;  // start
  private static final byte OK =  1;  // ok
  private static final byte OB =  2;  // object
  private static final byte KE =  3;  // key
  private static final byte CO =  4;  // colon
  private static final byte VA =  5;  // value
  private static final byte AR =  6;  // array
  private static final byte ST =  7;  // string
  private static final byte ES =  8;  // escape
  private static final byte U1 =  9;  // u1
  private static final byte U2 = 10;  // u2
  private static final byte U3 = 11;  // u3
  private static final byte U4 = 12;  // u4
  private static final byte MI = 13;  // minus
  private static final byte ZE = 14;  // zero
  private static final byte IN = 15;  // integer
  private static final byte F0 = 16;  // frac0
  private static final byte FR = 17;  // fraction
  private static final byte E1 = 18;  // e
  private static final byte E2 = 19;  // ex
  private static final byte E3 = 20;  // exp
  private static final byte T1 = 21;  // tr
  private static final byte T2 = 22;  // tru
  private static final byte T3 = 23;  // true
  private static final byte F1 = 24;  // fa
  private static final byte F2 = 25;  // fal
  private static final byte F3 = 26;  // fals
  private static final byte F4 = 27;  // false
  private static final byte N1 = 28;  // nu
  private static final byte N2 = 29;  // nul
  private static final byte N3 = 30;  // null

  /**
   * The state transition table takes the current state and the current symbol,
   * and returns either a new state or an action. An action is represented as a
   * negative number. A JSON text is accepted if at the end of the text the
   * state is OK and if the mode is MODE_DONE.
   */
  private static final byte[] state_transition_table = {
  /*               white                                      1-9                                   ABCDF  etc
               space |  {  }  [  ]  :  ,  "  \  /  +  -  .  0  |  a  b  c  d  e  f  l  n  r  s  t  u  |  E  | pad */
  /*start  GO*/  GO,GO,-6,__,-5,__,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__,__,
  /*ok     OK*/  OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*object OB*/  OB,OB,__,-9,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*key    KE*/  KE,KE,__,__,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*colon  CO*/  CO,CO,__,__,__,__,-2,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*value  VA*/  VA,VA,-6,__,-5,__,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__,__,
  /*array  AR*/  AR,AR,-6,__,-5,-7,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__,__,
  /*string ST*/  ST,__,ST,ST,ST,ST,ST,ST,-4,ES,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,__,
  /*escape ES*/  __,__,__,__,__,__,__,__,ST,ST,ST,__,__,__,__,__,__,ST,__,__,__,ST,__,ST,ST,__,ST,U1,__,__,__,__,
  /*u1     U1*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,U2,U2,U2,U2,U2,U2,U2,U2,__,__,__,__,__,__,U2,U2,__,__,
  /*u2     U2*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,U3,U3,U3,U3,U3,U3,U3,U3,__,__,__,__,__,__,U3,U3,__,__,
  /*u3     U3*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,U4,U4,U4,U4,U4,U4,U4,U4,__,__,__,__,__,__,U4,U4,__,__,
  /*u4     U4*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,ST,ST,ST,ST,ST,ST,ST,ST,__,__,__,__,__,__,ST,ST,__,__,
  /*minus  MI*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,ZE,IN,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*zero   ZE*/  OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,F0,__,__,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__,__,
  /*int    IN*/  OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,F0,IN,IN,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__,__,
  /*frac0  F0*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,FR,FR,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*frac   FR*/  OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,FR,FR,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__,__,
  /*e      E1*/  __,__,__,__,__,__,__,__,__,__,__,E2,E2,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*ex     E2*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*exp    E3*/  OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*tr     T1*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T2,__,__,__,__,__,__,__,
  /*tru    T2*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T3,__,__,__,__,
  /*true   T3*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__,__,
  /*fa     F1*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F2,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,
  /*fal    F2*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F3,__,__,__,__,__,__,__,__,__,
  /*fals   F3*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F4,__,__,__,__,__,__,
  /*false  F4*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__,__,
  /*nu     N1*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N2,__,__,__,__,
  /*nul    N2*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N3,__,__,__,__,__,__,__,__,__,
  /*null   N3*/  __,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,
  };

  /**
   * These modes can be pushed on the stack.
   */
  private static final byte MODE_ARRAY  = 0;
  private static final byte MODE_DONE   = 1;
  private static final byte MODE_KEY    = 2;
  private static final byte MODE_OBJECT = 3;

  /**
   * The stack containing the current modes
   */
  private byte[] stack;

  /**
   * The top of the stack (-1 if the stack is empty)
   */
  private int top = -1;

  /**
   * The maximum number of modes on the stack
   */
  private int depth = 2048;

  /**
   * The current state
   */
  private byte state;

  /**
   * Collects all characters if the current state is ST (String),
   * IN (Integer), FR (Fraction) or the like
   */
  private final StringBuilder currentValue = new StringBuilder(128);

  /**
   * The number of characters processed by the JSON parser
   * @since 2.0.0
   */
  private long parsedCharacterCount = 0L;

  /**
   * The feeder is used to get input to parse
   */
  private final JsonFeeder feeder;

  /**
   * The first event returned by {@link #parse(char)}
   */
  private int event1 = JsonEvent.NEED_MORE_INPUT;

  /**
   * The second event returned by {@link #parse(char)}
   */
  private int event2 = JsonEvent.NEED_MORE_INPUT;

  /**
   * Push a mode onto the stack
   * @param mode the mode to push
   * @return false if there is overflow
   */
  private boolean push(byte mode) {
    ++top;
    if (top >= stack.length) {
      if (top >= depth) {
        return false;
      }
      stack = Arrays.copyOf(stack, Math.min(stack.length * 2, depth));
    }
    stack[top] = mode;
    return true;
  }

  /**
   * Pop the stack, assuring that the current mode matches the expectation
   * @param mode the expected mode
   * @return false if there is underflow or if the modes mismatch
   */
  private boolean pop(byte mode) {
    if (top < 0 || stack[top] != mode) {
      return false;
    }
    --top;
    return true;
  }

  /**
   * Constructs a JSON parser that uses the UTF-8 charset to decode input data
   */
  public JsonParser() {
    this(StandardCharsets.UTF_8);
  }

  /**
   * Constructs a JSON parser
   * @param charset the charset that should be used to decode the
   * parser's input data
   */
  public JsonParser(Charset charset) {
    this(new DefaultJsonFeeder(charset));
  }

  /**
   * Constructs the JSON parser
   * @param feeder the feeder that will provide the parser with input data
   */
  public JsonParser(JsonFeeder feeder) {
    stack = new byte[16];
    state = GO;
    push(MODE_DONE);
    this.feeder = feeder;
  }

  /**
   * Set the maximum number of modes on the stack (basically the maximum number
   * of nested objects/arrays in the JSON text to parse)
   * @param depth the maximum number of modes
   */
  public void setMaxDepth(int depth) {
    this.depth = depth;
  }

  /**
   * @return the maximum number of modes on the stack (basically the maximum
   * number of nested objects/arrays in the JSON text to parse)
   */
  public int getMaxDepth() {
    return depth;
  }

  /**
   * Call this method to proceed parsing the JSON text and to get the next
   * event. The method returns {@link JsonEvent#NEED_MORE_INPUT} if it needs
   * more input data from the parser's feeder.
   * @return the next JSON event or {@link JsonEvent#NEED_MORE_INPUT} if more
   * input is needed
   */
  public int nextEvent() {
    try {
      while (event1 == JsonEvent.NEED_MORE_INPUT) {
        if (!feeder.hasInput()) {
          if (feeder.isDone()) {
            if (state != OK) {
              int r = stateToEvent();
              if (r != JsonEvent.NEED_MORE_INPUT) {
                state = OK;
                return r;
              }
            }
            return (state == OK && pop(MODE_DONE) ? JsonEvent.EOF : JsonEvent.ERROR);
          }
          return JsonEvent.NEED_MORE_INPUT;
        }
        parse(feeder.nextInput());
      }
    } catch (CharacterCodingException e) {
      return JsonEvent.ERROR;
    }

    int r = event1;
    if (event1 != JsonEvent.ERROR) {
      event1 = event2;
      event2 = JsonEvent.NEED_MORE_INPUT;
    }

    return r;
  }

  /**
   * Get the feeder that can be used to provide more input to the parser
   * @return the parser's feeder
   */
  public JsonFeeder getFeeder() {
    return feeder;
  }

  /**
   * This function is called for each character (or partial character) in the
   * JSON text. It can accept UTF-8, UTF-16, or UTF-32. It will set
   * {@link #event1} and {@link #event2} accordingly. As a precondition these
   * fields should have a value of {@link JsonEvent#NEED_MORE_INPUT}.
   * @param nextChar the character to parse
   */
  private void parse(char nextChar) {
    parsedCharacterCount++;

    // Determine the character's class.
    byte nextClass;
    if (nextChar >= 128) {
        nextClass = C_ETC;
    } else {
        nextClass = ascii_class[nextChar];
        if (nextClass <= __) {
            event1 = JsonEvent.ERROR;
            return;
        }
    }

    // Get the next state from the state transition table.
    byte nextState = state_transition_table[(state << 5) + nextClass];
    if (nextState >= 0) {
      if (nextState >= ST && nextState <= E3) {
        // According to the 'state_transition_table' we don't need to check
        // for "state <= E3". There is no way we can get here without 'state'
        // being less than or equal to E3.
        // if (state >= ST && state <= E3) {
        if (state >= ST) {
          currentValue.append(nextChar);
        } else {
          currentValue.setLength(0);
          if (nextState != ST) {
            currentValue.append(nextChar);
          }
        }
      } else if (nextState == OK) {
        // end of token identified, convert state to result
        event1 = stateToEvent();
      }

      // Change the state.
      state = nextState;
    } else {
      // Or perform one of the actions.
      performAction(nextState);
    }
  }

  /**
   * Perform an action that changes the parser state
   * @param action the action to perform
   */
  private void performAction(byte action) {
    switch (action) {
    // empty }
    case -9:
      if (!pop(MODE_KEY)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      state = OK;
      event1 = JsonEvent.END_OBJECT;
      break;

    // }
    case -8:
      if (!pop(MODE_OBJECT)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      event1 = stateToEvent();
      if (event1 == JsonEvent.NEED_MORE_INPUT) {
        event1 = JsonEvent.END_OBJECT;
      } else {
        event2 = JsonEvent.END_OBJECT;
      }
      state = OK;
      break;

    // ]
    case -7:
      if (!pop(MODE_ARRAY)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      event1 = stateToEvent();
      if (event1 == JsonEvent.NEED_MORE_INPUT) {
        event1 = JsonEvent.END_ARRAY;
      } else {
        event2 = JsonEvent.END_ARRAY;
      }
      state = OK;
      break;

    // {
    case -6:
      if (!push(MODE_KEY)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      state = OB;
      event1 = JsonEvent.START_OBJECT;
      break;

    // [
    case -5:
      if (!push(MODE_ARRAY)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      state = AR;
      event1 = JsonEvent.START_ARRAY;
      break;

    // "
    case -4:
      if (stack[top] == MODE_KEY) {
        state = CO;
        event1 = JsonEvent.FIELD_NAME;
      } else {
        state = OK;
        event1 = JsonEvent.VALUE_STRING;
      }
      break;

    // ,
    case -3:
      switch (stack[top]) {
      case MODE_OBJECT:
        // A comma causes a flip from object mode to key mode.
        if (!pop(MODE_OBJECT) || !push(MODE_KEY)) {
          event1 = JsonEvent.ERROR;
          return;
        }
        event1 = stateToEvent();
        state = KE;
        break;
      case MODE_ARRAY:
        event1 = stateToEvent();
        state = VA;
        break;
      default:
        event1 = JsonEvent.ERROR;
        return;
      }
      break;

    // :
    case -2:
      // A colon causes a flip from key mode to object mode.
      if (!pop(MODE_KEY) || !push(MODE_OBJECT)) {
        event1 = JsonEvent.ERROR;
        return;
      }
      state = VA;
      break;

    // Bad action.
    default:
      event1 = JsonEvent.ERROR;
      break;
    }
  }

  /**
   * Converts the current parser state to a JSON event
   * @return the JSON event or {@link JsonEvent#NEED_MORE_INPUT} if the
   * current state does not produce a JSON event
   */
  private int stateToEvent() {
    if (state == IN || state == ZE) {
      return JsonEvent.VALUE_INT;
    } else if (state >= FR && state <= E3) {
      return JsonEvent.VALUE_DOUBLE;
    } else if (state == T3) {
      return JsonEvent.VALUE_TRUE;
    } else if (state == F4) {
      return JsonEvent.VALUE_FALSE;
    } else if (state == N3) {
      return JsonEvent.VALUE_NULL;
    }
    return JsonEvent.NEED_MORE_INPUT;
  }

  /**
   * If the event returned by {@link #nextEvent()} was
   * {@link JsonEvent#VALUE_STRING} this method will return the parsed string
   * @return the parsed string
   */
  public String getCurrentString() {
    return currentValue.toString();
  }

  /**
   * If the event returned by {@link #nextEvent()} was
   * {@link JsonEvent#VALUE_INT} this method will return the parsed integer
   * @return the parsed integer
   */
  public int getCurrentInt() {
    return Integer.parseInt(currentValue.toString());
  }

  /**
   * If the event returned by {@link #nextEvent()} was
   * {@link JsonEvent#VALUE_INT} this method will return the parsed long integer
   * @return the parsed long integer
   */
  public long getCurrentLong() {
    return Long.parseLong(currentValue.toString());
  }

  /**
   * If the event returned by {@link #nextEvent()} was
   * {@link JsonEvent#VALUE_DOUBLE} this method will return the parsed double
   * @return the parsed double
   */
  public double getCurrentDouble() {
    return Double.parseDouble(currentValue.toString());
  }

  /**
   * <p>Get the number of characters processed by the JSON parser so far.</p>
   * <p>Use this method to get the location of an event returned by
   * {@link #nextEvent()}. Note that the character count is always greater than
   * the actual position of the event in the parsed JSON text. For example, if
   * {@link #nextEvent()} returns {@link JsonEvent#START_OBJECT} and the
   * character count is <code>n</code>, the location of the <code>{</code>
   * character is <code>n-1</code>. If {@link #nextEvent()} returns
   * {@link JsonEvent#FIELD_NAME} and the parsed field name is
   * <code>"id"</code>, the location is <code>n-4</code> because the field
   * name is 4 characters long (including the quotes) and the parser has
   * already processed all characters of it.</p>
   * @return the character offset
   * @since 2.0.0
   */
  public long getParsedCharacterCount() {
    return parsedCharacterCount;
  }
}
