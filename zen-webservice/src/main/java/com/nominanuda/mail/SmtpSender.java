package com.nominanuda.mail;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nominanuda.lang.Check;
import com.nominanuda.lang.Tuple2;

public class SmtpSender {
	private String host = "127.0.0.1";
	private int port = 25;
	private String username;
	private String password;
	private String from;
	private String to;
	private String contentType = "text/plain";
	private Properties sessionProps = new Properties();

	private transient Session session;


	public void sendMail(String subj, String body) throws IOException {
		sendMail(subj, body, to, from);
	}

	public void sendMail(String subj, String body, String to/*comma sep*/) throws IOException {
		sendMail(subj, body, to, from);
	}

	public void sendMail(String subj, String body, String to/*comma sep*/, String from) throws IOException {
		sendMail(subj, body, to, from, contentType);
	}

	public void sendMail(String subj, String body, String to/*comma sep*/, String from, String contentType) throws IOException {
		try {
			InternetAddress fromAddress = new InternetAddress(postprocessFrom(Check.notNull(from)));
			MimeMessage msg = new MimeMessage(getSession());
			msg.setFrom(fromAddress);
			msg.setRecipients(Message.RecipientType.TO, postprocessTo(Check.notNull(to)));
			msg.setSubject(postprocessSubject(subj));
			msg.setSentDate(new Date());
			Tuple2<String, String> bodyAndContentType = postprocessBody(body, contentType);
			msg.setText(bodyAndContentType.get0(), bodyAndContentType.get1());
			msg.setContent(body, Check.notNull(contentType));
			sendMessage(msg);
		} catch (MessagingException e) {
			throw new IOException(e);
		}
	}

	protected String postprocessFrom(String from) {
		return from;
	}

	protected String postprocessTo(String to) {
		return to;
	}

	protected String postprocessSubject(String subj) {
		return subj;
	}

	protected Tuple2<String, String> postprocessBody(String body, String contentType) {
		return new Tuple2<String, String>(body, contentType);
	}

	public void sendMessage(MimeMessage mimeMessage) throws IOException {
		try {
			Transport transport = getSession().getTransport("smtp");
			transport.connect(host, port, username, password);
			try {
				if (mimeMessage.getSentDate() == null) {
					mimeMessage.setSentDate(new Date());
				}
				String messageId = mimeMessage.getMessageID();
				mimeMessage.saveChanges();
				if (messageId != null) {
					mimeMessage.setHeader("Message-ID", messageId);
				}
				transport.sendMessage(mimeMessage, mimeMessage
						.getAllRecipients());
			} finally {
				transport.close();
			}
		} catch (AuthenticationFailedException ex) {
			throw new IOException(ex);
		} catch (MessagingException ex) {
			throw new IOException(ex);
		}
	}

	private synchronized Session getSession() {
		if (this.session == null) {
			this.session = Session.getInstance(sessionProps);
		}
		return this.session;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setSessionProperties(Properties props) {
		this.sessionProps = props;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
