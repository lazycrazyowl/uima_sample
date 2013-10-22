package com.croeder.sesame_interface;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.File;

public class GetAbstracts_Test {
	GetAbstracts myClass;

	@Before
	public void setup() throws Exception { 
		//myClass.preliminaryStuff();
		myClass = new GetAbstracts();
	}

	@Test
	public void test_1() {
		//String s = myClass.doStuff(new File("test.txt"));
		//assertEquals("the returned string should be from the file", "This is a file at the top level.", s);
	}

	@After
	public void teardown() {
		//myClass.close();
	}
}
