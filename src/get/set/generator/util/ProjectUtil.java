package get.set.generator.util;

import get.set.generator.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class ProjectUtil {
	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	public static IJavaProject getJavaProject(IEditorPart editorPart)
			throws JavaModelException {
		IFile file = getSourceFile(editorPart);
		IProject project = file.getProject();
		return JavaCore.create(project);
	}

	public static String getImportDeclaration(IFile file, String className)
			throws JavaModelException {
		String fullyQualifiedName = null;
		ICompilationUnit currentFile = JavaCore.createCompilationUnitFrom(file);
		IImportDeclaration[] imports = currentFile.getImports();
		for (IImportDeclaration iImportDeclaration : imports) {
			if (iImportDeclaration.getElementName().endsWith(
					MethodUtil.PACKAGE_DOT + className)) {
				fullyQualifiedName = iImportDeclaration.getElementName();
				break;
			}
		}
		if (fullyQualifiedName == null || fullyQualifiedName.length() <= 0) {
			IPackageDeclaration packageDeclaration = currentFile
					.getPackageDeclarations()[0];
			fullyQualifiedName = packageDeclaration.getElementName()
					+ MethodUtil.PACKAGE_DOT + className;
		}
		return fullyQualifiedName;
	}

	public static IFile getSourceFile(IEditorPart editorPart) {
		IEditorInput input = editorPart.getEditorInput();
		IFile file = null;
		if (input instanceof IFileEditorInput) {
			file = ((IFileEditorInput) input).getFile();
		}
		return file;
	}

	public static IEditorPart getEditorPart() {
		IEditorPart editorPart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		return editorPart;
	}
}
