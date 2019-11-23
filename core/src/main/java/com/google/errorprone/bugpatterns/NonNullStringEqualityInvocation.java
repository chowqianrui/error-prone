package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Matchers.instanceEqualsInvocation;
import static com.google.errorprone.util.ASTHelpers.getReceiver;

import java.util.List;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.BugPattern.ProvidesFix;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.dataflow.nullnesspropagation.Nullness;
import com.google.errorprone.dataflow.nullnesspropagation.NullnessAnalysis;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;

@BugPattern(
	    name = "NonNullStringEqualityInvocation",
	    summary = "String equality invocation check",
	    severity = WARNING,
	    providesFix = ProvidesFix.REQUIRES_HUMAN_ATTENTION)
public final class NonNullStringEqualityInvocation extends BugChecker implements MethodInvocationTreeMatcher {

	@Override
	public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
		if (!instanceEqualsInvocation().matches(tree, state)) {
			return Description.NO_MATCH;
		}
		
		// The source code is something like "receiver.equals(arguments[0])"
		List<? extends ExpressionTree> arguments = tree.getArguments();
		Tree receiver = getReceiver(tree);  // receiver node
		Tree argument = arguments.get(0);  // argument node
		
		// If the receiver is an identifier, a new Constructor() call, a new Array[]() call, or
		// some kinds of method invocation like foo(), and the argument is a string constant,
		// then we perform the check
		if (checkKind(receiver.getKind()) && argument.getKind().equals(Kind.STRING_LITERAL)) {
			// Check nullness of the receiver
			NullnessAnalysis nullnessAnalysis = state.getNullnessAnalysis();
			TreePath receiverPath = TreePath.getPath(state.getPath(), receiver);
			Nullness receiverNullness = nullnessAnalysis.getNullness(receiverPath, state.context);
			
			// If the receiver can potentially be null, warn the user
			if (Nullness.NULL.equals(receiverNullness) || Nullness.NULLABLE.equals(receiverNullness))
				return this.describeMatch(tree, SuggestedFix.swap(receiver, argument));
		}
		
		return Description.NO_MATCH;
	}
	
	private static boolean checkKind(Kind k) {
		return Kind.IDENTIFIER.equals(k) || Kind.NEW_CLASS.equals(k) 
				|| Kind.NEW_ARRAY.equals(k) || Kind.METHOD_INVOCATION.equals(k);
	}
}
