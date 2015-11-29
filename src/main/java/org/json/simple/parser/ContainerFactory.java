package org.json.simple.parser;

import java.util.List;
import java.util.Map;

/**
 * Container factory for creating containers for JSON object and JSON array.
 * 
 * @see org.json.simple.parser.JSONParser#parse(java.io.Reader, ContainerFactory)
 * 
 * @deprecated for heavy reliance on raw types and unchecked casts.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
@Deprecated
public interface ContainerFactory {
	/**
	 * @return A Map instance to store JSON object, or null if you want to use org.json.simple.JSONObject.
	 */
	Map<?, ?> createObjectContainer();
	
	/**
	 * @return A List instance to store JSON array, or null if you want to use org.json.simple.JSONArray. 
	 */
	List<?> createArrayContainer();
}
