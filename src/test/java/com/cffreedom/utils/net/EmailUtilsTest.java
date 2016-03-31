package com.cffreedom.utils.net;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class EmailUtilsTest {
	@Test
	public void getEmailsTest() {
		String text = "John Doe <jdoe@example.com>,steve@there.com";
		List<String> emails = EmailUtils.getEmailAddresses(text);
		assertEquals(emails.size(), 2);
		assertEquals(emails.get(0), "jdoe@example.com");
		assertEquals(emails.get(1), "steve@there.com");
		
		text = null;
		emails = EmailUtils.getEmailAddresses(text);
		assertEquals(emails.size(), 0);
	}
}
