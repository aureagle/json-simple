package org.json.simple.parser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DefaultContentHandler implements ContentHandler {
	
	private enum ContainerType {
		ROOT,
		ENTRY,
		OBJECT,
		ARRAY
	}
	
	ArrayDeque<Object> valueStack;
	ArrayDeque<ContainerType> containerStack;
	
	public DefaultContentHandler() {
	}
	
	public Object getContent() {
		if (valueStack.size() > 0) {
			return valueStack.pop();
		}
		return null;
	}
	
	public int getContentSize() {
		return valueStack.size();
	}
	
	@Override
	public void startJSON() throws ParseException, IOException {
		valueStack = new ArrayDeque<Object>();
		containerStack = new ArrayDeque<ContainerType>();
		containerStack.push(ContainerType.ROOT);
	}

	@Override
	public void endJSON() throws ParseException, IOException {
		containerStack.pop();
		if (containerStack.peek() != ContainerType.ROOT) {
			throw new IOException("JSON ended but not in root container");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean startObject() throws ParseException, IOException {
		
		ContainerType container = containerStack.peek();
		if (container == ContainerType.OBJECT) {
			throw new IOException("Cannot start an object directly within another");
		}
		else if (container == ContainerType.ARRAY) {
			// insert into array
			// XXX raw types unavoidable due to the way ContainerFactory is written
			@SuppressWarnings("rawtypes")
			List array = (List) (valueStack.peek());
			JSONObject obj = new JSONObject();
			array.add(obj);
			valueStack.push(obj);  // put on front of stack so further items can be added to it
		}
		else {
			// entry or root, simply add to stack
			valueStack.push(new JSONObject());	
		}
		containerStack.push(ContainerType.OBJECT);
		return true;
	}

	@Override
	public boolean endObject() throws ParseException, IOException {
		ContainerType c = containerStack.pop();
		if (c != ContainerType.OBJECT) {
			throw new IOException("No object to end");
		}
		
		// peek at top
		c = containerStack.peek();
		if (c == ContainerType.ROOT) {
			// if we're back at root, stop parsing
			return false;
		} else if (c == ContainerType.ARRAY) {
			// pop object off stack (already stored in array)
			valueStack.pop();
		}
 		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws ParseException, IOException {
		ContainerType c = containerStack.peek();
		if (c != ContainerType.OBJECT) {
			throw new IOException("Must be at the start of an object");
		}
		valueStack.push(key);
		containerStack.push(ContainerType.ENTRY);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean endObjectEntry() throws ParseException, IOException {
		ContainerType c = containerStack.pop();
		if (c != ContainerType.ENTRY) {
			throw new IOException("Not currently in an object entry");
		}
		
		Object value = valueStack.pop();
		String key = (String) valueStack.pop();
		@SuppressWarnings("rawtypes")
		// XXX raw types unavoidable do to the way ContainerFactory is currently written
		Map parent = (Map) valueStack.peek();
		parent.put(key, value);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean startArray() throws ParseException, IOException {
		
		ContainerType c = containerStack.peek();
		if (c == ContainerType.OBJECT) {
			throw new IOException("Cannot start an array at the root of an object");
		} else if (c == ContainerType.ARRAY) {
			// push to start of array
			// XXX raw types unavoidable due to the way ContainerFactory is written
			@SuppressWarnings("rawtypes")
			List array = (List) (valueStack.peek());
			array.add(new JSONArray());
		} else {
			valueStack.push(new JSONArray());
		}
		containerStack.push(ContainerType.ARRAY);
		return true;
	}

	@Override
	public boolean endArray() throws ParseException, IOException {
		ContainerType c = containerStack.pop();
		if (c != ContainerType.ARRAY) {
			throw new IOException("No array to end here");
		}
		
		// if back at root, stop parsing
		c = containerStack.peek();
		if (c == ContainerType.ROOT) {
			return false;
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean primitive(Object value) throws ParseException, IOException {
		ContainerType c = containerStack.peek();
		if (c == ContainerType.OBJECT) {
			throw new IOException("Value cannot be at the root of an object");
		} else if (c == ContainerType.ARRAY) {
			@SuppressWarnings("rawtypes")
			List array = (List) (valueStack.peek());
			array.add(value);
		} else {
			valueStack.push(value);
		}
		
		// stop parsing primitive if at root of JSON
		if (c == ContainerType.ROOT) {
			return false;
		}
		return true;
	}

}
