// MIT License
//
// Copyright (c) 2016 Michel Kraemer
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

import java.util.Arrays;

/**
 * This class is based on the file
 * <a href="http://www.json.org/JSON_checker/">JSON_checker.c</a>
 * from <a href="http://www.json.org/">JSON.org</a>
 * @author Michel Kraemer
 * @author JSON.org
 */
public class JsonChecker {
  private static final int __ = -1; // the universal error code

  /**
   * Characters are mapped into these 31 character classes. This allows for
   * a significant reduction in the size of the state transition table.
   */
  private static final int C_SPACE =  0;  // space
  private static final int C_WHITE =  1;  // other whitespace
  private static final int C_LCURB =  2;  // {
  private static final int C_RCURB =  3;  // }
  private static final int C_LSQRB =  4;  // [
  private static final int C_RSQRB =  5;  // ]
  private static final int C_COLON =  6;  // :
  private static final int C_COMMA =  7;  // ,
  private static final int C_QUOTE =  8;  // "
  private static final int C_BACKS =  9;  // \
  private static final int C_SLASH = 10;  // /
  private static final int C_PLUS  = 11;  // +
  private static final int C_MINUS = 12;  // -
  private static final int C_POINT = 13;  // .
  private static final int C_ZERO  = 14;  // 0
  private static final int C_DIGIT = 15;  // 123456789
  private static final int C_LOW_A = 16;  // a
  private static final int C_LOW_B = 17;  // b
  private static final int C_LOW_C = 18;  // c
  private static final int C_LOW_D = 19;  // d
  private static final int C_LOW_E = 20;  // e
  private static final int C_LOW_F = 21;  // f
  private static final int C_LOW_L = 22;  // l
  private static final int C_LOW_N = 23;  // n
  private static final int C_LOW_R = 24;  // r
  private static final int C_LOW_S = 25;  // s
  private static final int C_LOW_T = 26;  // t
  private static final int C_LOW_U = 27;  // u
  private static final int C_ABCDF = 28;  // ABCDF
  private static final int C_E     = 29;  // E
  private static final int C_ETC   = 30;  // everything else

  /**
   * This array maps the 128 ASCII characters into character classes.
   * The remaining Unicode characters should be mapped to C_ETC.
   * Non-whitespace control characters are errors.
   */
  private final static int[] ascii_class = {
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
  private static final int GO =  0;  // start
  private static final int OK =  1;  // ok
  private static final int OB =  2;  // object
  private static final int KE =  3;  // key
  private static final int CO =  4;  // colon
  private static final int VA =  5;  // value
  private static final int AR =  6;  // array
  private static final int ST =  7;  // string
  private static final int ES =  8;  // escape
  private static final int U1 =  9;  // u1
  private static final int U2 = 10;  // u2
  private static final int U3 = 11;  // u3
  private static final int U4 = 12;  // u4
  private static final int MI = 13;  // minus
  private static final int ZE = 14;  // zero
  private static final int IN = 15;  // integer
  private static final int FR = 16;  // fraction
  private static final int E1 = 17;  // e
  private static final int E2 = 18;  // ex
  private static final int E3 = 19;  // exp
  private static final int T1 = 20;  // tr
  private static final int T2 = 21;  // tru
  private static final int T3 = 22;  // true
  private static final int F1 = 23;  // fa
  private static final int F2 = 24;  // fal
  private static final int F3 = 25;  // fals
  private static final int F4 = 26;  // false
  private static final int N1 = 27;  // nu
  private static final int N2 = 28;  // nul
  private static final int N3 = 29;  // null

  /**
   * The state transition table takes the current state and the current symbol,
   * and returns either a new state or an action. An action is represented as a
   * negative number. A JSON text is accepted if at the end of the text the
   * state is OK and if the mode is MODE_DONE.
   */
  private static int[][] state_transition_table = {
  /*               white                                      1-9                                   ABCDF  etc
               space |  {  }  [  ]  :  ,  "  \  /  +  -  .  0  |  a  b  c  d  e  f  l  n  r  s  t  u  |  E  |*/
  /*start  GO*/ {GO,GO,-6,__,-5,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*ok     OK*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*object OB*/ {OB,OB,__,-9,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*key    KE*/ {KE,KE,__,__,__,__,__,__,ST,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*colon  CO*/ {CO,CO,__,__,__,__,-2,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*value  VA*/ {VA,VA,-6,__,-5,__,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__},
  /*array  AR*/ {AR,AR,-6,__,-5,-7,__,__,ST,__,__,__,MI,__,ZE,IN,__,__,__,__,__,F1,__,N1,__,__,T1,__,__,__,__},
  /*string ST*/ {ST,__,ST,ST,ST,ST,ST,ST,-4,ES,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST,ST},
  /*escape ES*/ {__,__,__,__,__,__,__,__,ST,ST,ST,__,__,__,__,__,__,ST,__,__,__,ST,__,ST,ST,__,ST,U1,__,__,__},
  /*u1     U1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U2,U2,U2,U2,U2,U2,U2,U2,__,__,__,__,__,__,U2,U2,__},
  /*u2     U2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U3,U3,U3,U3,U3,U3,U3,U3,__,__,__,__,__,__,U3,U3,__},
  /*u3     U3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,U4,U4,U4,U4,U4,U4,U4,U4,__,__,__,__,__,__,U4,U4,__},
  /*u4     U4*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,ST,ST,ST,ST,ST,ST,ST,ST,__,__,__,__,__,__,ST,ST,__},
  /*minus  MI*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,ZE,IN,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*zero   ZE*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,FR,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*int    IN*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,FR,IN,IN,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__},
  /*frac   FR*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,FR,FR,__,__,__,__,E1,__,__,__,__,__,__,__,__,E1,__},
  /*e      E1*/ {__,__,__,__,__,__,__,__,__,__,__,E2,E2,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*ex     E2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*exp    E3*/ {OK,OK,__,-8,__,-7,__,-3,__,__,__,__,__,__,E3,E3,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*tr     T1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T2,__,__,__,__,__,__},
  /*tru    T2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,T3,__,__,__},
  /*true   T3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__},
  /*fa     F1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F2,__,__,__,__,__,__,__,__,__,__,__,__,__,__},
  /*fal    F2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F3,__,__,__,__,__,__,__,__},
  /*fals   F3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,F4,__,__,__,__,__},
  /*false  F4*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__,__,__},
  /*nu     N1*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N2,__,__,__},
  /*nul    N2*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,N3,__,__,__,__,__,__,__,__},
  /*null   N3*/ {__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,__,OK,__,__,__,__,__,__,__,__},
  };

  /**
   * These modes can be pushed on the stack.
   */
  private static final int MODE_ARRAY  = 0;
  private static final int MODE_DONE   = 1;
  private static final int MODE_KEY    = 2;
  private static final int MODE_OBJECT = 3;
  
  /**
   * The stack containing the current modes
   */
  private int[] stack;
  
  /**
   * The top of the stack (-1 if the stack is empty)
   */
  private int top = -1;
  
  /**
   * The maximum number of modes on the stack
   */
  private final int depth;
  
  /**
   * The current state
   */
  private int state;
  
  /**
   * Push a mode onto the stack
   * @param mode the mode to push
   * @return false if there is overflow
   */
  private boolean push(int mode) {
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
  private boolean pop(int mode) {
    if (top < 0 || stack[top] != mode) {
      return false;
    }
    --top;
    return true;
  }
  
  /**
   * Constructs the JsonChecker
   */
  public JsonChecker() {
    this(2048);
  }

  /**
   * Constructs the JsonChecker
   * @param depth the maximum number of modes on the stack
   */
  public JsonChecker(int depth) {
    stack = new int[16];
    top = -1;
    this.depth = depth;
    state = GO;
    push(MODE_DONE);
  }

  /**
   * Call this function for each character (or partial character) in your JSON text.
   * It can accept UTF-8, UTF-16, or UTF-32.
   * @param nextChar the character to feed
   * @return <code>true</code> if things are looking ok so far, <code>false</code>
   * if it rejects the text.
   */
  public boolean feed(char nextChar) {
    // Determine the character's class.
    int nextClass;
    if (nextChar < 0) {
        return false;
    }
    if (nextChar >= 128) {
        nextClass = C_ETC;
    } else {
        nextClass = ascii_class[nextChar];
        if (nextClass <= __) {
            return false;
        }
    }
    
    // Get the next state from the state transition table.
    int nextState = state_transition_table[state][nextClass];
    if (nextState >= 0) {
      // Change the state.
      state = nextState;
    } else {
      // Or perform one of the actions.
      switch (nextState) {
      // empty }
      case -9:
        if (!pop(MODE_KEY)) {
            return false;
        }
        state = OK;
        break;

      // }
      case -8:
        if (!pop(MODE_OBJECT)) {
            return false;
        }
        state = OK;
        break;

      // ]
      case -7:
        if (!pop(MODE_ARRAY)) {
            return false;
        }
        state = OK;
        break;

      // {
      case -6:
        if (!push(MODE_KEY)) {
            return false;
        }
        state = OB;
        break;

      // [
      case -5:
        if (!push(MODE_ARRAY)) {
            return false;
        }
        state = AR;
        break;

      // "
      case -4:
        switch (stack[top]) {
        case MODE_KEY:
          state = CO;
          break;
        case MODE_ARRAY:
        case MODE_OBJECT:
          state = OK;
          break;
        default:
          return false;
        }
        break;

      // ,
      case -3:
        switch (stack[top]) {
        case MODE_OBJECT:
          // A comma causes a flip from object mode to key mode.
          if (!pop(MODE_OBJECT) || !push(MODE_KEY)) {
              return false;
          }
          state = KE;
          break;
        case MODE_ARRAY:
          state = VA;
          break;
        default:
          return false;
        }
        break;

      // :
      case -2:
        // A colon causes a flip from key mode to object mode.
        if (!pop(MODE_KEY) || !push(MODE_OBJECT)) {
          return false;
        }
        state = VA;
        break;

      // Bad action.
      default:
        return false;
      }
    }
    return true;
  }

  /**
   * This method should be called after all of the characters have been
   * processed, but only if every call to {@link #feed(char)} returned
   * <code>true</code>.
   * @return <code>true</code> if the JSON text was accepted.
   */
  public boolean done() {
    return state == OK && pop(MODE_DONE);
  }
}
