package get.set.generator.popup.actions;

import get.set.generator.util.ActionType;
import get.set.generator.util.MethodUtil;
import get.set.generator.util.MethodVistor;
import get.set.generator.util.ProjectUtil;
import get.set.generator.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class GetSetActionBuilder {

	private ActionType type;

	private GetSetActionBuilder() {

	}

	public GetSetActionBuilder(ActionType type) {
		this.type = type;
	}

	public void doAction() throws JavaModelException, MalformedTreeException,
			BadLocationException {

		IEditorPart editorPart = ProjectUtil.getEditorPart();

		if (editorPart instanceof AbstractTextEditor) {
			String selectedText = null;
			IEditorSite iEditorSite = editorPart.getEditorSite();
			if (iEditorSite != null) {
				ISelectionProvider selectionProvider = iEditorSite
						.getSelectionProvider();
				if (selectionProvider != null) {
					ISelection iSelection = selectionProvider.getSelection();
					if (iSelection != null && !iSelection.isEmpty()) {
						ITextSelection selection = ((ITextSelection) iSelection);
						selectedText = selection.getText();

						if (selectedText != null && selectedText.length() > 0) {
							String[] strSource = StringUtil
									.getObjectInfo(selectedText);

							String className = strSource[0];
							String objectName = strSource[1];

							IFile sourceFile = ProjectUtil
									.getSourceFile(editorPart);

							String fullyQualifiedName = ProjectUtil
									.getImportDeclaration(sourceFile, className);

							IJavaProject javaProject = ProjectUtil
									.getJavaProject(editorPart);

							ICompilationUnit iCUnit = JavaCore
									.createCompilationUnitFrom(sourceFile);

							IType findType = javaProject
									.findType(fullyQualifiedName);

							int offset = selection.getOffset();
							IJavaElement element = iCUnit.getElementAt(offset);
							String currentMethodinSource = null;
							if (element != null
									&& element.getElementType() == IJavaElement.METHOD) {
								currentMethodinSource = element
										.getElementName();
							}

							if (findType != null) {
								System.out.println("Found class: "
										+ fullyQualifiedName);
								if (findType.exists()) {
									List<String> methodList = null;
									if (ActionType.GET.equals(type)) {
										methodList = MethodUtil
												.getAllGetMethods(findType
														.getMethods());
									} else if (ActionType.SET.equals(type)) {
										methodList = MethodUtil
												.getAllSetMethods(findType
														.getMethods());
									} else {
										methodList = new ArrayList<String>();
									}

									System.out.println(methodList);
									if (!methodList.isEmpty()) {

										CompilationUnit astRoot = ProjectUtil
												.parse(iCUnit);

										MethodVistor vistor = new MethodVistor();
										vistor.setCurrentMethodinSource(currentMethodinSource);

										astRoot.accept(vistor);

										AST ast = astRoot.getAST();
										ASTRewrite rewrite = ASTRewrite
												.create(ast);

										MethodDeclaration methodDecl = vistor
												.getMethodDeclaration();
										Block block = methodDecl.getBody();
										List statements = block.statements();
										
										ASTNode prevNode = MethodUtil
												.getPrevNodeInMethod(
														statements, offset);

										MethodInvocation mInvo = null;

										Statement statement = null;

										ListRewrite listRewrite = rewrite
												.getListRewrite(
														block,
														Block.STATEMENTS_PROPERTY);

										for (String methodName : methodList) {
											mInvo = ast.newMethodInvocation();
											mInvo.setName(ast
													.newSimpleName(methodName));
											mInvo.setExpression(ast
													.newSimpleName(objectName));

											statement = ast
													.newExpressionStatement(mInvo);

											listRewrite.insertAfter(statement,
													prevNode, null);
											prevNode = statement;
										}

										TextEdit textEdit = rewrite
												.rewriteAST();

										Document document = new Document(
												iCUnit.getSource());

										textEdit.apply(document);

										iCUnit.getBuffer().setContents(
												document.get());
									}
								}
							} else {
								// Not implement yet
							}
						}
					}
				}
			}
		}

	}
}
