package io.github.ahmadnull.dataserialization;

import java.util.HashMap;

public enum Format {
	JSON {
		public String serialize(HashMap<Object, Object> data) {
			return JSONUtil.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return JSONUtil.deserialize(raw);
		}
	},

	TOML {
		public String serialize(HashMap<Object, Object> data) {
			return TOMLUtil.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return TOMLUtil.deserialize(raw);
		}
	},

	XML {
		public String serialize(HashMap<Object, Object> data) {
			return TOMLUtil.serialize(data);
		}
		
		public HashMap<Object, Object> deserialize(String raw) {
			return TOMLUtil.deserialize(raw);
		}
	};
	
	abstract public String serialize(HashMap<Object, Object> data);
	abstract public HashMap<Object, Object> deserialize(String raw);
}
