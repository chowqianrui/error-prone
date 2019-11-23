package com.google.errorprone.bugpatterns;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.errorprone.CompilationTestHelper;
import com.google.errorprone.bugpatterns.NonNullStringEqualityInvocation;

@RunWith(JUnit4.class)
public class NonNullStringEqualityInvocationTest {
	@Test
	public void testGoodPractice() {
		CompilationTestHelper
			.newInstance(NonNullStringEqualityInvocation.class, getClass())
			.addSourceLines("Test.java", new String[] {
					"class Test {", 
					"  int foo(String str) {", 
					"    if (\"abc\".equals(str)) return 1;",
					"    return 0;",
					"  }", 
					"}"
			})
			.doTest();
	}
	
	@Test
	public void testFunctionParam() {
		CompilationTestHelper
			.newInstance(NonNullStringEqualityInvocation.class, getClass())
			.addSourceLines("Test.java", new String[] {
					"class Test {", 
					"  int foo(String str) {", 
					"    // BUG: Diagnostic contains:",  // Should report a warning
					"    if (str.equals(\"abc\")) return 1;",
					"    return 0;",
					"  }", 
					"}"
			})
			.doTest();
	}
	
	@Test
	public void testNewlyAssignedString() {
		CompilationTestHelper
			.newInstance(NonNullStringEqualityInvocation.class, getClass())
			.addSourceLines("Test.java", new String[] {
					"class Test {", 
					"  int foo() {", 
					"    String str = \"abc\";",
					"    if (str.equals(\"abc\")) return 1;",
					"    return 0;",
					"  }", 
					"}"
			})
			.doTest();
	}
	
	@Test
	public void testConstructor() {
		CompilationTestHelper
			.newInstance(NonNullStringEqualityInvocation.class, getClass())
			.addSourceLines("Test.java", new String[] {
					"class Test {", 
					"  int foo(String str) {", 
					"    if (new String(str).equals(\"abc\")) return 1;",
					"    return 0;",
					"  }", 
					"}"
			})
			.doTest();
	}
	
	@Test
	public void testArray() {
		CompilationTestHelper
		.newInstance(NonNullStringEqualityInvocation.class, getClass())
		.addSourceLines("Test.java", new String[] {
				"class Test {", 
				"  int foo() {", 
				"    if (new String[3].equals(\"abc\")) return 1;",
				"    return 0;",
				"  }", 
				"}"
		})
		.doTest();
	}
	
	@Test
	public void testMethodInvocation() {
		CompilationTestHelper
		.newInstance(NonNullStringEqualityInvocation.class, getClass())
		.addSourceLines("Test.java", new String[] {
				"class Test {", 
				"  int foo(String str) {", 
				"    // BUG: Diagnostic contains:",  // Should report a warning
				"    if (new Object().toString().equals(\"abc\")) return 1;",
				"    return 0;",
				"  }", 
				"}"
		})
		.doTest();
	}
}
