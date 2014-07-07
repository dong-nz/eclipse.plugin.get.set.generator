package get.set.generator.util;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodVistor extends ASTVisitor {
	private MethodDeclaration methodDeclaration;

	private String currentMethodinSource;
	
	public MethodVistor() {
		super();
	}

	public void setCurrentMethodinSource(String currentMethodinSource) {
		this.currentMethodinSource = currentMethodinSource;
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println();
		if (node.getName().getFullyQualifiedName()
				.equals(currentMethodinSource)) {
			methodDeclaration = node;
		}
		return false;
	}
}
