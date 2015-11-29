/*
 * $Id: JSONParser.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-15
 */
package org.json.simple.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Parser for JSON text. Please note that JSONParser is NOT thread-safe.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>, modified by Antonio Sanchez <antonios@ece.ubc.ca>
 */
public class JSONParser {

	public static final int S_INIT = 0;
	public static final int S_IN_FINISHED_VALUE = 1;// string,number,boolean,null,object,array
	public static final int S_IN_OBJECT = 2;
	public static final int S_IN_ARRAY = 3;
	public static final int S_PASSED_PAIR_KEY = 4;
	public static final int S_IN_PAIR_VALUE = 5;
	public static final int S_END = 6;
	public static final int S_IN_ERROR = -1;

	private Deque<Integer> handlerStatusStack;
	private Yylex lexer = new Yylex((Reader) null);
	private Yytoken token = null;
	private int status = S_INIT;

	private int peekStatus(Deque<Integer> statusStack) {
		if (statusStack.size() == 0)
			return -1;
		Integer status = (Integer) statusStack.peek();
		return status.intValue();
	}

	/**
	 * Reset the parser to the initial state without resetting the underlying
	 * reader.
	 *
	 */
	public void reset() {
		token = null;
		status = S_INIT;
		handlerStatusStack = null;
	}

	/**
	 * Reset the parser to the initial state with a new character reader.
	 * 
	 * @param in
	 *            - The new character reader.
	 * @throws IOException
	 * @throws ParseException
	 */
	public void reset(Reader in) {
		lexer.yyreset(in);
		reset();
	}

	/**
	 * @return The position of the beginning of the current token.
	 */
	public int getPosition() {
		return lexer.getPosition();
	}

	/**
	 * Parse JSON text into java object from the input source, continuously reading until
	 * the end of the String.
	 * 
	 * @param in

	 * @return Instance of the following: org.json.simple.JSONObject,
	 *         org.json.simple.JSONArray, java.lang.String, java.lang.Number,
	 *         java.lang.Boolean, null
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public Object parse(String s) throws ParseException {
		DefaultContentHandler handler = new DefaultContentHandler();
		parse(s, handler);
		return handler.getContent();
	}

	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @param in
	 * @param ContentHandler delegate responsible for assembling each component
	 * 
	 * @throws ParseException
	 */
	public void parse(String s, ContentHandler handler) throws ParseException {
		StringReader in = new StringReader(s);
		try {
			parse(in, handler);
		} catch (IOException ie) {
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}

	/**
	 * Parse JSON text into java object from the input source.  Reads
	 * until end of the next JSON object, array, or value
	 * 
	 * @param in
	 * @return Instance of the following: org.json.simple.JSONObject,
	 *         org.json.simple.JSONArray, java.lang.String, java.lang.Number,
	 *         java.lang.Boolean, null
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public Object parse(Reader in) throws IOException, ParseException {
		DefaultContentHandler handler = new DefaultContentHandler();
		parse(in, handler);
		return handler.getContent();
	}

	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @param in
	 * @param contentHandler handler for constructing JSON objects
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parse(Reader in, ContentHandler contentHandler) throws IOException, ParseException {
		parse(in, contentHandler, false);
	}
	
	private void nextToken() throws ParseException, IOException {
		token = lexer.yylex();
		if (token == null) {
			token = new Yytoken(Yytoken.TYPE_EOF, null);
		}
	}

	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @param in
	 * @param contentHandler handler for constructing JSON objects
	 * @param resume resume from previous stream
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parse(String s, ContentHandler contentHandler, boolean resume) throws ParseException {
		StringReader in = new StringReader(s);
		try {
			parse(in, contentHandler, resume);
		} catch (IOException ie) {
			/*
			 * Actually it will never happen.
			 */
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @param in
	 * @param ContainerFactory delegate responsible for creating containers
	 * 
	 * @throws ParseException
	 */
	@Deprecated
	public Object parse(String s, ContainerFactory factory) throws ParseException {
		StringReader in = new StringReader(s);
		try {
			return parse(in, factory);
		} catch (IOException ie) {
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	/**
	 * Parse JSON text into java object from the input source.  Reads
	 * until end of the next JSON object, array, or value
	 * 
	 * @param in
	 * @param factory factory for constructing JSON containers
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Deprecated
	public Object parse(Reader in, ContainerFactory factory) throws IOException, ParseException {
		ContainerContentHandler handler = new ContainerContentHandler(factory);
		parse(in, handler, false);
		return handler.getContent();
	}

	/**
	 * Stream processing of JSON text.
	 * 
	 * @see ContentHandler
	 * 
	 * @param in
	 * @param contentHandler
	 * @param isResume
	 *            - Indicates if it continues previous parsing operation. If set
	 *            to true, resume parsing the old stream, and parameter 'in'
	 *            will be ignored. If this method is called for the first time
	 *            in this instance, isResume will be ignored.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void parse(Reader in, ContentHandler contentHandler, boolean isResume) throws IOException, ParseException {
		
		if (handlerStatusStack == null) {
			isResume = false;
		}
		
		if (!isResume) {
			reset(in);
			handlerStatusStack = new ArrayDeque<Integer>();
		}

		Deque<Integer> statusStack = handlerStatusStack;

		try {
			do {

				switch (status) {
				case S_INIT:
					// we are in "root" (no object)
					contentHandler.startJSON();
					nextToken();
					switch (token.type) {
					case Yytoken.TYPE_VALUE:
						status = S_IN_FINISHED_VALUE;
						statusStack.push(status);
						if (!contentHandler.primitive(token.value))
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						status = S_IN_OBJECT;
						statusStack.push(status);
						if (!contentHandler.startObject())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						status = S_IN_ARRAY;
						statusStack.push(status);
						if (!contentHandler.startArray())
							return;
						break;
					case Yytoken.TYPE_EOF:
						// nothing but end parsing
						// stay in S_INIT
						contentHandler.endJSON();
						return;
					default:
						status = S_IN_ERROR;
					}// inner switch
					break;

				case S_IN_OBJECT:
					nextToken();
					// must be a string (key) or comma or close brace
					switch (token.type) {
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						if (token.value instanceof String) {
							String key = (String) token.value;
							status = S_PASSED_PAIR_KEY;
							statusStack.push(status);
							if (!contentHandler.startObjectEntry(key))
								return;
						} else {
							status = S_IN_ERROR;
						}
						break;
					case Yytoken.TYPE_RIGHT_BRACE:
						if (statusStack.size() > 1) {
							statusStack.pop();
							status = peekStatus(statusStack);
							if (!contentHandler.endObject())
								return;
						} else {
							status = S_IN_FINISHED_VALUE;
						}
						break;
					default:
						status = S_IN_ERROR;
						break;
					}// inner switch
					break;
					
				case S_PASSED_PAIR_KEY:
					nextToken();
					// must be a colon then value/array/object
					switch (token.type) {
					case Yytoken.TYPE_COLON:
						break;
					case Yytoken.TYPE_VALUE:
						statusStack.pop();
						status = peekStatus(statusStack);
						if (!contentHandler.primitive(token.value))
							return;
						if (!contentHandler.endObjectEntry())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						statusStack.pop();
						statusStack.push(S_IN_PAIR_VALUE);
						status = S_IN_ARRAY;
						statusStack.push(status);
						if (!contentHandler.startArray())
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						statusStack.pop();
						statusStack.push(S_IN_PAIR_VALUE);
						status = S_IN_OBJECT;
						statusStack.push(status);
						if (!contentHandler.startObject())
							return;
						break;
					default:
						status = S_IN_ERROR;
					}
					break;

				case S_IN_PAIR_VALUE:
					/*
					 * S_IN_PAIR_VALUE is just a marker to indicate the end of
					 * an object entry, it doesn't process any token, therefore
					 * delay consuming token until next round.
					 */
					statusStack.pop();
					status = peekStatus(statusStack);
					if (!contentHandler.endObjectEntry())
						return;
					break;

				case S_IN_ARRAY:
					nextToken();
					switch (token.type) {
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						if (!contentHandler.primitive(token.value))
							return;
						break;
					case Yytoken.TYPE_RIGHT_SQUARE:
						if (statusStack.size() > 1) {
							statusStack.pop();
							status = peekStatus(statusStack);
						} else {
							status = S_IN_FINISHED_VALUE;
						}
						if (!contentHandler.endArray())
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						status = S_IN_OBJECT;
						statusStack.push(status);
						if (!contentHandler.startObject())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						status = S_IN_ARRAY;
						statusStack.push(status);
						if (!contentHandler.startArray())
							return;
						break;
					default:
						status = S_IN_ERROR;
					}// inner switch
					break;
					
				case S_IN_FINISHED_VALUE:
					nextToken();
					if (token.type == Yytoken.TYPE_EOF) {
						contentHandler.endJSON();
						status = S_END;
						return;
					} else {
						status = S_IN_ERROR;
						throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
					}
					// break;
					
				case S_END:
					return;

				case S_IN_ERROR:
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}// switch
				if (status == S_IN_ERROR) {
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}
			} while (token.type != Yytoken.TYPE_EOF);
		} catch (IOException ie) {
			status = S_IN_ERROR;
			throw ie;
		} catch (ParseException pe) {
			status = S_IN_ERROR;
			throw pe;
		} catch (RuntimeException re) {
			status = S_IN_ERROR;
			throw re;
		} catch (Error e) {
			status = S_IN_ERROR;
			throw e;
		}

		status = S_IN_ERROR;
		throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
	}
}
