package com.nominanuda.store.redis;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class Redis {

		private final String hostname;
		private final int port;

	public Redis(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	class RedisException extends RuntimeException {

		private static final long serialVersionUID = 3753411847230253753L;

		RedisException(Throwable e) {
			super(e);
		}

		RedisException(Throwable e, String pattern, String... arguments) {
			super(MessageFormat.format(pattern, arguments), e);
		}
	}

public Integer append(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("APPEND".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String auth(String password) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("AUTH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(password.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(password.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String bgrewriteaof() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("12".getBytes());
    output.write("\r\n".getBytes());
    output.write("BGREWRITEAOF".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String bgsave() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("BGSAVE".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> blpop(String key, int timeout) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("BLPOP".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(String.valueOf(timeout).length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(String.valueOf(timeout).getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> blpop(String[] keys, int timeout) {
int paramslen = 0;paramslen += keys.length;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("BLPOP".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    output.write("$".getBytes());
    output.write(String.valueOf(String.valueOf(timeout).length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(String.valueOf(timeout).getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> brpop(String key, int timeout) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("BRPOP".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(String.valueOf(timeout).length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(String.valueOf(timeout).getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> brpop(String[] keys, int timeout) {
int paramslen = 0;paramslen += keys.length;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("BRPOP".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    output.write("$".getBytes());
    output.write(String.valueOf(String.valueOf(timeout).length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(String.valueOf(timeout).getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String brpoplpush(String source, String destination, String timeout) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("10".getBytes());
    output.write("\r\n".getBytes());
    output.write("BRPOPLPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(source.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(source.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(timeout.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(timeout.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer dbsize() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("DBSIZE".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer decr(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("DECR".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer decrby(String key, String decrement) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("DECRBY".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(decrement.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(decrement.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer del(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("3".getBytes());
    output.write("\r\n".getBytes());
    output.write("DEL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer del(String[] keys) {
int paramslen = 0;paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("3".getBytes());
    output.write("\r\n".getBytes());
    output.write("DEL".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String discard() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("DISCARD".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String echo(String message) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("ECHO".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(message.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(message.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> exec() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("EXEC".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer exists(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("EXISTS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer expire(String key, String seconds) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("EXPIRE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(seconds.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(seconds.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer expireat(String key, String timestamp) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("EXPIREAT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(timestamp.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(timestamp.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String flushall() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("FLUSHALL".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String flushdb() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("FLUSHDB".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String get(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("3".getBytes());
    output.write("\r\n".getBytes());
    output.write("GET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer getbit(String key, String offset) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("GETBIT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(offset.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(offset.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String getrange(String key, String start, String end) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("GETRANGE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(end.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(end.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String getset(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("GETSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hdel(String key, String field) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("HDEL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hdel(String key, String[] fields) {
int paramslen = 0;paramslen++;
paramslen += fields.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("HDEL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < fields.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(fields[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(fields[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hexists(String key, String field) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("HEXISTS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String hget(String key, String field) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("HGET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> hgetall(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("HGETALL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hincrby(String key, String field, String increment) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("HINCRBY".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(increment.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(increment.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String hincrbyfloat(String key, String field, String increment) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("12".getBytes());
    output.write("\r\n".getBytes());
    output.write("HINCRBYFLOAT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(increment.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(increment.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> hkeys(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HKEYS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hlen(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("HLEN".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> hmget(String key, String field) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HMGET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> hmget(String key, String[] fields) {
int paramslen = 0;paramslen++;
paramslen += fields.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HMGET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < fields.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(fields[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(fields[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String hmset(String key, String field, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HMSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String hmset(String key, String[] fieldValues) {
int paramslen = 0;paramslen++;
paramslen += fieldValues.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HMSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < fieldValues.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(fieldValues[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(fieldValues[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hset(String key, String field, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("HSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer hsetnx(String key, String field, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("HSETNX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(field.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(field.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> hvals(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("HVALS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer incr(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("INCR".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer incrby(String key, String increment) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("INCRBY".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(increment.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(increment.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String incrbyfloat(String key, String increment) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("INCRBYFLOAT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(increment.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(increment.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String info() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("INFO".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> keys(String pattern) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("KEYS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(pattern.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(pattern.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer lastsave() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("LASTSAVE".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> lindex(String key, String index) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("LINDEX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(index.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(index.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer llen(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("LLEN".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String lpop(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("LPOP".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer lpush(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("LPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer lpush(String key, String[] values) {
int paramslen = 0;paramslen++;
paramslen += values.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("LPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < values.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(values[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(values[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer lpushx(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("LPUSHX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> lrange(String key, String start, String stop) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("LRANGE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(stop.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(stop.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer lrem(String key, String count, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("LREM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(count.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(count.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String lset(String key, String index, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("LSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(index.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(index.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String ltrim(String key, String start, String stop) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("LTRIM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(stop.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(stop.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> mget(String[] keys) {
int paramslen = 0;paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("MGET".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String migrate(String host, String port, String key, String destinationdb, String timeout) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("MIGRATE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(host.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(host.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(port.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(port.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destinationdb.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destinationdb.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(timeout.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(timeout.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer move(String key, String db) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("MOVE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(db.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(db.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String mset(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("MSET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String msetnx(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("MSETNX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String msetnx(String[] keyValues) {
int paramslen = 0;paramslen += keyValues.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("MSETNX".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keyValues.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keyValues[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keyValues[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String multi() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("MULTI".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer persist(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("PERSIST".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer pexpire(String key, String milliseconds) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("PEXPIRE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(milliseconds.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(milliseconds.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer pexpireat(String key, String millisecondtimestamp) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("9".getBytes());
    output.write("\r\n".getBytes());
    output.write("PEXPIREAT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(millisecondtimestamp.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(millisecondtimestamp.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String ping() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("PING".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String psetex(String key, String milliseconds, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("PSETEX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(milliseconds.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(milliseconds.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer pttl(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("PTTL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer publish(String channel, String message) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("PUBLISH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(channel.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(channel.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(message.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(message.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String quit() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("QUIT".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String randomkey() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("9".getBytes());
    output.write("\r\n".getBytes());
    output.write("RANDOMKEY".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer rename(String key, String newkey) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("RENAME".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(newkey.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(newkey.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String renamenx(String key, String newkey) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("RENAMENX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(newkey.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(newkey.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String restore(String key, String ttl, String serializedvalue) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("RESTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(ttl.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(ttl.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(serializedvalue.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(serializedvalue.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String rpop(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("RPOP".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String rpoplpush(String source, String destination) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("9".getBytes());
    output.write("\r\n".getBytes());
    output.write("RPOPLPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(source.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(source.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer rpush(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("RPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer rpush(String key, String[] values) {
int paramslen = 0;paramslen++;
paramslen += values.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("RPUSH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < values.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(values[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(values[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer rpushx(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("RPUSHX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer sadd(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SADD".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer sadd(String key, String[] members) {
int paramslen = 0;paramslen++;
paramslen += members.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SADD".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < members.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(members[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(members[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String save() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SAVE".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer scard(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SCARD".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sdiff(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SDIFF".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sdiff(String key, String[] values) {
int paramslen = 0;paramslen++;
paramslen += values.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SDIFF".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < values.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(values[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(values[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer sdiffstore(String destination, String[] keys) {
int paramslen = 0;paramslen++;
paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("10".getBytes());
    output.write("\r\n".getBytes());
    output.write("SDIFFSTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String select(String index) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SELECT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(index.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(index.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String set(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("3".getBytes());
    output.write("\r\n".getBytes());
    output.write("SET".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer setbit(String key, String offset, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SETBIT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(offset.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(offset.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String setex(String key, String seconds, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SETEX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(seconds.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(seconds.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer setnx(String key, String value) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SETNX".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer setrange(String key, String offset, String value) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("SETRANGE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(offset.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(offset.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(value.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(value.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sinter(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SINTER".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sinter(String[] keys) {
int paramslen = 0;paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SINTER".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer sinterstore(String destination, String[] keys) {
int paramslen = 0;paramslen++;
paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("SINTERSTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer sismember(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("9".getBytes());
    output.write("\r\n".getBytes());
    output.write("SISMEMBER".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String slaveof(String host, String port) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("SLAVEOF".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(host.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(host.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(port.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(port.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> smembers(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("SMEMBERS".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer smove(String source, String destination, String member) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("SMOVE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(source.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(source.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sort(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SORT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String spop(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SPOP".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String srandmember(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("SRANDMEMBER".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer srem(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SREM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer srem(String key, String[] members) {
int paramslen = 0;paramslen++;
paramslen += members.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("SREM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < members.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(members[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(members[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer strlen(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("STRLEN".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sunion(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SUNION".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sunion(String[] keys) {
int paramslen = 0;paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("SUNION".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> sunionstore(String destination, String[] keys) {
int paramslen = 0;paramslen++;
paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("SUNIONSTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> time() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("TIME".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer ttl(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("3".getBytes());
    output.write("\r\n".getBytes());
    output.write("TTL".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String type(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("TYPE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String unwatch() {
int paramslen = 0;
  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("UNWATCH".getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String watch(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("WATCH".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String watch(String[] keys) {
int paramslen = 0;paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("WATCH".getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return buff.toString();
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zadd(String key, String score, String member) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZADD".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(score.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(score.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zcard(String key) {
int paramslen = 0;paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZCARD".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zcount(String key, String min, String max) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZCOUNT".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(min.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(min.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(max.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(max.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String zincrby(String key, String increment, String member) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("7".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZINCRBY".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(increment.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(increment.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> zrange(String key, String start, String stop) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZRANGE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(stop.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(stop.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> zrangebyscore(String key, String min, String max) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("13".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZRANGEBYSCORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(min.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(min.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(max.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(max.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zrank(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("5".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZRANK".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zrem(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zrem(String key, String[] members) {
int paramslen = 0;paramslen++;
paramslen += members.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("4".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREM".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < members.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(members[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(members[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zremrangebyrank(String key, String start, String stop) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("15".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREMRANGEBYRANK".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(stop.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(stop.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zremrangebyscore(String key, String min, String max) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("16".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREMRANGEBYSCORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(min.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(min.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(max.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(max.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> zrevrange(String key, String start, String stop) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("9".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREVRANGE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(start.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(start.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(stop.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(stop.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public List<String> zrevrangebyscore(String key, String max, String min) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("16".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREVRANGEBYSCORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(max.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(max.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(min.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(min.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

      int size = Integer.parseInt(buff.toString());
      if (size == -1) {
      	input.close();
      	output.close();
      	return Arrays.asList();
      }
      List<String> result = new LinkedList<String>();
      for (int i = 0; i < size; i++) {
      	char r02 = (char) input.read();
      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();
      	for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      		buff2.write(b);
      	}
      	input.read();
      	int len = Integer.parseInt(buff2.toString());
      	if (len == -1) {
      		result.add(null);
      		continue;
      	}
      	byte[] response = new byte[len];
      	input.read(response);
      	input.read();
      	input.read();
      	result.add(new String(response));
      }

    input.close();
    output.close();

    return result;
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zrevrank(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("8".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZREVRANK".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public String zscore(String key, String member) {
int paramslen = 0;paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("6".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZSCORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(member.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(member.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    int len = Integer.parseInt(buff.toString());
    if (len == -1) {
        input.close();
        output.close();
        return null;
    }

    byte[] response = new byte[len];
    input.read(response);
    input.read();
    input.read();

    input.close();
    output.close();

    return new String(response);
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zunionstore(String destination, String numkeys, String key) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen++;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZUNIONSTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(numkeys.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(numkeys.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(key.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(key.getBytes());
    output.write("\r\n".getBytes());

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
public Integer zunionstore(String destination, int numkeys, String[] keys) {
int paramslen = 0;paramslen++;
paramslen++;
paramslen += keys.length;

  try {
    Socket socket = new Socket(this.hostname, this.port);
    OutputStream output = socket.getOutputStream();
    InputStream input = socket.getInputStream();

    output.write("*".getBytes());
    output.write(String.valueOf(paramslen + 1).getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write("11".getBytes());
    output.write("\r\n".getBytes());
    output.write("ZUNIONSTORE".getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(destination.length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(destination.getBytes());
    output.write("\r\n".getBytes());

    output.write("$".getBytes());
    output.write(String.valueOf(String.valueOf(numkeys).length()).getBytes());
    output.write("\r\n".getBytes());
    output.write(String.valueOf(numkeys).getBytes());
    output.write("\r\n".getBytes());

    for (int i = 0; i < keys.length; i++) {
    	output.write("$".getBytes());
    	output.write(String.valueOf(keys[i].length()).getBytes());
    	output.write("\r\n".getBytes());
    	output.write(keys[i].getBytes());
    	output.write("\r\n".getBytes());
    }

    char r0 = (char) input.read();
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    for (byte b = (byte) input.read(); '\r' != b; b = (byte) input.read()) {
      buff.write(b);
    }
    input.read();

    if ('-' == r0) {
      input.close();
      output.close();
      throw new RuntimeException(buff.toString());
    }

    input.close();
    output.close();

    return new Integer(buff.toString());
  }
  catch(Exception e) {
    throw new RedisException(e);
  }
}
}
