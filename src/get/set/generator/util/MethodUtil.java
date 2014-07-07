package get.set.generator.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;

public class MethodUtil {
	public static final String GET_METHOD = "get";
	public static final String SET_METHOD = "set";
	public static final String PACKAGE_DOT = ".";

	public static List<String> getAllSetMethods(IMethod[] methods)
			throws JavaModelException {
		ArrayList<String> list = new ArrayList<String>();
		for (IMethod iMethod : methods) {
			if (!iMethod.isConstructor() && !iMethod.isMainMethod()
					&& iMethod.getElementName().startsWith(SET_METHOD)) {
				list.add(iMethod.getElementName());
			}
		}
		return list;
	}

	public static List<String> getAllGetMethods(IMethod[] methods)
			throws JavaModelException {
		ArrayList<String> list = new ArrayList<String>();
		for (IMethod iMethod : methods) {
			if (!iMethod.isConstructor()
					&& !iMethod.isMainMethod()
					&& iMethod.getElementName().startsWith(
							MethodUtil.GET_METHOD)) {
				list.add(iMethod.getElementName());
			}
		}
		return list;
	}

	public static ASTNode getPrevNodeInMethod(List statements, int offset) {
		ASTNode currentNode = null;

		for (Object object : statements) {
			ASTNode node = (ASTNode) object;
			if (offset == node.getStartPosition()) {
				currentNode = node;
				break;
			}
		}
		return currentNode;
	}
}
