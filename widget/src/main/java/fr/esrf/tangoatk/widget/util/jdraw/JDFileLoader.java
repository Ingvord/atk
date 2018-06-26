/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package fr.esrf.tangoatk.widget.util.jdraw;

import java.io.*;
import java.util.*;
import java.awt.*;

/** A class for loading JDraw file (jdw) */
public class JDFileLoader {

  /* Lexical coce */

  static final int NUMBER = 1;
  static final int STRING = 2;
  static final int COMA = 3;
  static final int COLON = 4;
  static final int OPENBRACE = 5;
  static final int CLOSEBRACE = 6;

  private final String[] lexical_word = {
    "NULL",
    "NUMBER",
    "STRING",
    "COMA",
    "COLON",
    "'{'",
    "'}'"
  };

  static final String COMA_STR = ",";
  static final String COLON_STR = ":";
  static final String OPENBRACE_STR = "{";
  static final String CLOSEBRACE_STR = "}";

  static final int MAX_BUFFER_SIZE = 65536;   // 64Ko reading buffer
  static final int MAX_STRING_LENGTH = 4095;  // Maximum string length

  private int CrtLine;
  private int StartLine;
  private char CurrentChar;

  private char[] loadingBuffer = null;
  private int bufferIdx;
  private int bufferSize;

  private String word;
  private String version;
  InputStreamReader f;

  private char[] tmpWord = new char[MAX_STRING_LENGTH+1];

  // Global param section
  Color globalBackground = JDrawEditor.defaultBackground;

  /**
   * Construct a JDFileLoader.
   * @param fr File to be read.
   * @see #parseFile
   */
  public JDFileLoader(FileReader fr) {
    f = fr;
    CrtLine = 1;
    CurrentChar = ' ';
    loadingBuffer = new char[MAX_BUFFER_SIZE];
    bufferIdx = 0;
    bufferSize = 0;
  }
  
  /**
   * Construct a JDFileLoader.
   * @param insr File to be read.
   * @see #parseFile
   */
  public JDFileLoader(InputStreamReader insr) {
    f = insr;
    CrtLine = 1;
    CurrentChar = ' ';
  }

  /**
   * Construct a JDFileLoader.
   * @param str File to be read.
   * @see #parseFile
   */
  public JDFileLoader(String str) {
    ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
    f = new InputStreamReader(bis);
    CrtLine = 1;
    CurrentChar = ' ';
  }

  // ****************************************************
  // read the next character in the file
  // ****************************************************
  private void refill_buffer() throws IOException {

    if( bufferIdx>=bufferSize ) {
      bufferSize = f.read(loadingBuffer);
      bufferIdx = 0;
    }

  }


  private void read_char() throws IOException {

    if (loadingBuffer != null) {

      // Use loading buffer
      refill_buffer();

      if (bufferSize <= 0) {
        CurrentChar = 0;
      } else {
        CurrentChar = loadingBuffer[bufferIdx];
        bufferIdx++;
      }

    } else {

      // Simple reading
      if (!f.ready()) {
        CurrentChar = 0;
      } else {
        CurrentChar = (char) f.read();
      }

    }

    if (CurrentChar == '\n') CrtLine++;

  }

  // ****************************************************
  // Go to the next significant character
  // ****************************************************
  private void jump_space() throws IOException {

    while (CurrentChar <= 32 && CurrentChar > 0) read_char();
  }

  // ****************************************************
  // Read the next word in the file                           */
  // ****************************************************
  private String read_word() throws IOException {

    /* Jump space */
    jump_space();

    StartLine = CrtLine;

    /* Treat special character */
    if( CurrentChar == ',' ) {
      read_char();
      return COMA_STR;
    }

    if( CurrentChar == ':' ) {
      read_char();
      return COLON_STR;
    }

    if( CurrentChar == '{' ) {
      read_char();
      return OPENBRACE_STR;
    }

    if( CurrentChar == '}' ) {
      read_char();
      return CLOSEBRACE_STR;
    }

    int wIdx=0;

    /* Treat string */
    if (CurrentChar == '"') {
      tmpWord[wIdx++]=CurrentChar;
      read_char();
      while (CurrentChar != '"' && CurrentChar != 0 && CurrentChar != '\n' && wIdx<MAX_STRING_LENGTH) {
        tmpWord[wIdx++]=CurrentChar;
        read_char();
      }
      if (CurrentChar == 0 || CurrentChar == '\n' || wIdx>=MAX_STRING_LENGTH) {
        IOException e = new IOException("String too long at line " + StartLine);
        throw e;
      }
      tmpWord[wIdx++]=CurrentChar;
      read_char();
      return new String(tmpWord,0,wIdx);
    }

    /* Treat other word */
    while (CurrentChar > 32 && CurrentChar != ':' && CurrentChar != '{'
            && CurrentChar != '}' && CurrentChar != ',' && wIdx<MAX_STRING_LENGTH) {
      tmpWord[wIdx++]=CurrentChar;
      read_char();
    }

    if (wIdx == 0) {
      return null;
    }

    return new String(tmpWord,0,wIdx);

  }

  // ****************************************************
  // return the lexical classe of the next word        */
  // ****************************************************
  private boolean isNumber(String s) {
    boolean ok=true;
    for(int i=0;i<s.length() && ok;i++) {
      char c = s.charAt(i);
      ok = ok & ((c>='0' && c<='9') || c=='.' || c=='e' || c=='E' || c=='-');
    }
    return ok;
  }

  private int class_lex(String word) {

    /* Exception */
    if (word == null) return 0;
    if (word.length() == 0) return STRING;
    if (word.charAt(0)=='\"') return STRING;

    /* Special character */
    if (word.equals(COMA_STR)) return COMA;
    if (word.equals(COLON_STR)) return COLON;
    if (word.equals(OPENBRACE_STR)) return OPENBRACE;
    if (word.equals(CLOSEBRACE_STR)) return CLOSEBRACE;
    if (isNumber(word))   return NUMBER;

    return STRING;
  }

  // ****************************************************
  // Check lexical word
  // ****************************************************
  private void CHECK_LEX(int lt, int le) throws IOException {
    if (lt != le)
      throw new IOException("Invalid syntyax at line " + StartLine + ", " + lexical_word[le] + " expected");
  }

  int getCurrentLine() {
    return StartLine;
  }

  void jumpPropertyValue() throws IOException {
    // Trigger to the next value
    int lex = class_lex(word);

    if( lex==OPENBRACE) {
      jumpBlock();
      return;
    }

    boolean ok=true;
    while(ok && word!=null) {

      if(lex!=NUMBER && lex!=STRING)
        throw new IOException("Invalid syntyax at line " + StartLine + ": Number or String expected.");

      word=read_word();
      lex = class_lex(word);
      ok = (lex==COMA);
      if(ok) {
        word=read_word();
        lex = class_lex(word);
      }
    }
  }

  void jumpBlock() throws IOException {

    int lex = class_lex(word);
    CHECK_LEX(lex, OPENBRACE);
    int nb = 1;
    while (nb > 0 && word != null) {
      word = read_word();
      lex = class_lex(word);
      if (lex == OPENBRACE) nb++;
      if (lex == CLOSEBRACE) nb--;
    }
    if (word == null) throw new IOException("Unexpected end of file");
    word = read_word();

  }

  void startBlock() throws IOException {
    CHECK_LEX(class_lex(word), OPENBRACE);
    word=read_word();
  }

  void jumpLexem(int lexem) throws IOException {
    CHECK_LEX(class_lex(word), lexem);
    word=read_word();
  }

  void endBlock() throws IOException {
    CHECK_LEX(class_lex(word), CLOSEBRACE);
    word=read_word();
  }

  boolean isEndBlock() {
    return class_lex(word)==CLOSEBRACE;
  }

  // Value type ------------------------------------------------------------------

  double parseDouble() throws IOException {
    CHECK_LEX(class_lex(word),NUMBER);
    double ret = 0.0;
    try {
      ret = Double.parseDouble(word);
    } catch (NumberFormatException e) {
      throw new IOException("Invalid number at line " + StartLine);
    }
    word=read_word();
    return ret;
  }

  private String extractQuote(String s) {
    if(s.charAt(0)=='\"')
      return s.substring(1,s.length()-1);
    else
      return s;
  }

  String parseString()  throws IOException {
    CHECK_LEX(class_lex(word),STRING);
    String s=extractQuote(word);
    word=read_word();
    return s;
  }

  boolean parseBoolean() throws IOException {

    CHECK_LEX(class_lex(word),STRING);
    String value=word;
    word=read_word();
    return value.equalsIgnoreCase("true");

  }

  Point.Double parsePoint()  throws IOException {

    double x = parseDouble();
    jumpLexem(COMA);
    double y = parseDouble();

    return new Point.Double(x,y);
  }

  Point.Double[] parseSummitArray() throws IOException {

    Vector v = new Vector();
    double x,y;
    boolean end = false;

    CHECK_LEX(class_lex(word), STRING);
    if (!word.equals("summit"))
      throw new IOException("summit keyword missing at line " + StartLine);
    word = read_word();

    jumpLexem(COLON);

    while (!end && word!=null) {

      x = parseDouble();

      jumpLexem(COMA);

      y = parseDouble();

      v.add(new Point.Double(x,y));

      end = class_lex(word)!=COMA;
      if(!end)  word = read_word();

    }
    if (word == null) throw new IOException("Unexpected end of file");

    // Build summit array
    Point.Double[] ret = new Point.Double[v.size()];
    for(int i=0;i<v.size();i++) ret[i]=(Point.Double)v.get(i);
    return ret;

  }

  Point.Double[] parseRectangularSummitArray() throws IOException {

    // Build summit array
    Point.Double[] pts = parseSummitArray();
    Point.Double[] ret;

    if( version.compareTo("v11")>=0 ) {

      if (pts.length != 2)
        throw new IOException("Invalid summit number for JDRectangular at line " + StartLine);

      double x,y,w,h;
      x = pts[0].x;
      y = pts[0].y;
      w = pts[1].x-pts[0].x;
      h = pts[1].y-pts[0].y;
      ret = new Point.Double[8];
      ret[0] = pts[0];
      ret[4] = pts[1];
      ret[1] = new Point.Double(x + w/2.0,y);
      ret[2] = new Point.Double(x + w,y);
      ret[3] = new Point.Double(x + w,y + h/2.0);
      ret[5] = new Point.Double(x + w/2.0,y + h);
      ret[6] = new Point.Double(x,y + h);
      ret[7] = new Point.Double(x,y + h/2.0);

    } else {
      // v10
      ret = pts;
    }

    return ret;

  }

  String parseProperyName() throws IOException {
    String propName=parseString();
    jumpLexem(COLON);
    return propName;
  }

  Color parseColor()  throws IOException {

    int alpha = 255;
    int red = (int)parseDouble();
    jumpLexem(COMA);
    int green = (int)parseDouble();
    jumpLexem(COMA);
    int blue = (int)parseDouble();
    if( class_lex(word)==COMA ) {
      // Alpha definition
      jumpLexem(COMA);
      alpha = (int)parseDouble();
    }

    return new Color(red,green,blue,alpha);

  }

  String parseStringArray() throws IOException {

    Vector v = new Vector();
    boolean end = false;

    while (!end && word!=null) {

      String s=parseString();
      v.add(s);

      end = class_lex(word)!=COMA;
      if(!end)  word = read_word();
    }
    if (word == null) throw new IOException("Unexpected end of file");

    // Build String
    String ret = "";
    for(int i=0;i<v.size();i++) ret += (String)v.get(i) + "\n";
    return ret;

  }

  Font parseFont() throws IOException {

      String FontName=parseString();
      jumpLexem(COMA);
      int FontStyle=(int)parseDouble();
      jumpLexem(COMA);
      int FontSize=(int)parseDouble();

      return new Font(FontName,FontStyle,FontSize);

  }

  void parseGlobalSection() throws IOException {

    startBlock();

    while(!isEndBlock()) {
      String propName = parseProperyName();
      if( propName.equals("background") ) {
        globalBackground = parseColor();
      } else {
        System.out.println("Unknown global property found:" + propName);
        jumpPropertyValue();
      }
    }

    endBlock();

  }

  JDObject parseObject() throws IOException {

    String className = parseString();

    if (className.equals("JDEllipse")) {
      return new JDEllipse(this);
    } else if (className.equals("JDRectangle")) {
      return new JDRectangle(this);
    } else if (className.equals("JDRoundRectangle")) {
      return new JDRoundRectangle(this);
    } if (className.equals("JDLabel")) {
      return new JDLabel(this);
    } else if (className.equals("JDLine")) {
      return new JDLine(this);
    } else if (className.equals("JDPolyline")) {
      return new JDPolyline(this);
    } else if (className.equals("JDTitledRect")) {
      return new JDTitledRect(this);
    } else if (className.equals("JDSpline")) {
      return new JDSpline(this);
    } else if (className.equals("JDGroup")) {
      return new JDGroup(this);
    } else if (className.equals("JDImage")) {
      return new JDImage(this);
    } else if (className.equals("JDSwingObject")) {
      return new JDSwingObject(this);
    } else if (className.equals("JDAxis")) {
      return new JDAxis(this);
    } else if (className.equals("JDBar")) {
      return new JDBar(this);
    } else if (className.equals("JDSlider")) {
      return new JDSlider(this);
    } else if (className.equals("Global")) {
      parseGlobalSection();
      return null;
    } else {
      System.out.println("JDFileLoader.parseObject() Unknown class found:" + className + " at line " + StartLine);
      jumpBlock();
      return null;
    }

  }

  String parseParamString() throws IOException {

    Vector v = new Vector();
    boolean end = false;
    int lex;

    while (!end && word!=null) {

      // Get the string array
      // (interpret number as string in param list)
      // (interpret kw as string in param list)
      lex = class_lex(word);

      if (lex != STRING && lex != NUMBER)
        throw new IOException("Error at line " + StartLine + ", '" + lexical_word[NUMBER] + "' or '" + lexical_word[STRING] + "' expected");

      v.add(extractQuote(word));
      word = read_word();

      end = class_lex(word)!=COMA;
      if(!end)  word = read_word();

    }
    if (word == null) throw new IOException("Unexpected end of file");

    // Build String
    String ret = "";
    for(int i=0;i<v.size();i++) {
      ret += (String)v.get(i);
      if(i<v.size()-1) ret += "\n";
    }
    return ret;

  }

  /**
   * Parse a JDFile (jdw format).
   * @return Vector of JDObject.
   * @throws IOException In case of failure
   */
  public Vector parseFile() throws IOException {
    boolean eof = false;
    int lex;
    Vector objects = new Vector();

    /* CHECK BEGINING OF FILE  */
    word = read_word();
    if (word == null) throw new IOException("File empty !");
    if (!word.equalsIgnoreCase("jdfile")) throw new IOException("Invalid header !");

    jumpLexem(STRING);  // jdfile keyword
    version = parseString();  // release number
    jumpLexem(OPENBRACE);
    lex = class_lex(word);

    /* PARSE */
    while (!eof) {
      switch (lex) {
        case STRING:
          JDObject p = parseObject();
          if (p != null) objects.add(p);
          break;
        case CLOSEBRACE:
          break;
        default:
          throw new IOException("Invalid syntyax at line " + StartLine + ": Class name or '}' expected.");
      }
      lex = class_lex(word);
      eof = ((word == null) || (lex==CLOSEBRACE));
    }

    if(word == null)
      throw new IOException("Unexpected end of file at line " + StartLine + "." );

    // Check the last '}'
    CHECK_LEX(class_lex(word), CLOSEBRACE);

    // Build object array
    return objects;

  }
}
