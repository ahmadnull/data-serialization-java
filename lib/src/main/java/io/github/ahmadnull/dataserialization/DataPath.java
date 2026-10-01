package io.github.ahmadnull.dataserialization;

import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;

public class DataPath {

	public enum Seperator {
		Dot {
			public String toString() {
				return squareBracketsRegex + Pattern.quote(".") + ']';
			}
		},

		Slash {
			public String toString() {
				return squareBracketsRegex + Pattern.quote("/") + ']';
			}
		};

		String squareBracketsRegex = "[\\[\\]";
		public abstract String toString();
	}

	public static Object getByPath(Map<String, Object> map, String path) {
		return getByPath(map, DataPath.Seperator.Dot, path);
	}

	public static Object getByPath(Map<String, Object> map, DataPath.Seperator seperator, String path) {
		String seperatorRegex = seperator.toString();

		String[] splittedPath = path.split(seperatorRegex);

		return getByPath(map, seperator, splittedPath);
	}

	public static Object getByPath(Map<String, Object> map, String... path) {
		return getByPath(map, DataPath.Seperator.Dot, path);
	}

	public static Object getByPath(Map<String, Object> map, DataPath.Seperator seperator, String... path) {
		Object node = map.get(path[0]);

		int length = path.length;

		for (int i = 1; i < length; i++) {
			switch(node) {
				case Map h -> node = h.get(path[i]);
				case List a -> node = a.get(Integer.parseInt(path[i]));
				default -> System.out.println("Invalid type");
			}
		}

		return node;
	}

	public static Object getByPath(List<Object> list, String path) {
		return getByPath(list, DataPath.Seperator.Dot, path);
	}

	public static Object getByPath(List<Object> list, DataPath.Seperator seperator, String path) {
		String seperatorRegex = seperator.toString();

		String[] splittedPath = path.split(seperatorRegex);

		return getByPath(list, seperator, splittedPath);
	}

	public static Object getByPath(List<Object> list, String... path) {
		return getByPath(list, DataPath.Seperator.Dot, path);
	}

	public static Object getByPath(List<Object> list, DataPath.Seperator seperator, String... path) {
		Object node = list.get(Integer.parseInt(path[0]));

		int length = path.length;

		for (int i = 1; i < length; i++) {
			switch(node) {
				case Map h -> node = h.get(path[i]);
				case List a -> node = a.get(Integer.parseInt(path[i]));
				default -> System.out.println("Invalid type");
			}
		}

		return node;
	}
}
