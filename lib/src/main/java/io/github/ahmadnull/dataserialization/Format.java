package io.github.ahmadnull.dataserialization;

import java.util.HashMap;

public enum Format {
	JSON {
		public String serialize(HashMap<Object, Object> data) {
			return JSON.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return JSON.deserialize(raw);
		}
	},
	TOML {
		public String serialize(HashMap<Object, Object> data) {
			return TOML.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return TOML.deserialize(raw);
		}
	},
	XML {
		public String serialize(HashMap<Object, Object> data) {
			return XML.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return XML.deserialize(raw);
		}
	};
	
	abstract String serialize(HashMap<Object, Object> data);
	abstract HashMap<Object, Object> deserialize(String raw);
}
