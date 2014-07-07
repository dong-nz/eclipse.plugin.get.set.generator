package get.set.generator.util;

public class StringUtil {
	public static String[] getObjectInfo(String selectedText) {
		return selectedText.trim().split("\\s+");
	}
}
