package com.nominanuda.store.redis;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class ProtocolCompiler {

	class Command {

		private final String command;
		private final String return_type;

		private final List<String> list = new LinkedList<String>();

		public Command(String[] parts) {

			this.command = parts[0];

			for (int i = 1; i < parts.length - 1; i++) {
				list.add(parts[i].replaceAll("-", ""));
			}

			this.return_type = parts[parts.length - 1];
		}

		private Map<String, String> types = new HashMap<String, String>();

		public Command(String command, String[] parts, String return_type) {
			this.command = command;
			for (int i = 0; i < parts.length; i++) {
				String signature = parts[i].trim();
				String type = signature.replaceAll("\\s+.*$", "");
				String name = parts[i].replaceAll("^.*\\s+", "");
				types.put(name, type);
				list.add(name);
			}
			this.return_type = return_type;
		}

		@Override
		public String toString() {

			JavaBuilder java = new JavaBuilder();

			if ("BULK".equals(return_type)) {
				java.stmt(F("public {0} {1}", "String", command.toLowerCase()));
			} else if ("STATUS".equals(return_type)) {
				java.stmt(F("public {0} {1}", "String", command.toLowerCase()));
			} else if ("INTEGER".equals(return_type)) {
				java.stmt(F("public {0} {1}", "Integer", command.toLowerCase()));
			} else if ("MULTIBULK".equals(return_type)) {
				java.stmt(F("public {0} {1}", "List<String>", command.toLowerCase()));
			}

			StringBuilder count = new StringBuilder();
			count.append("int paramslen = 0;");

			StringBuilder params = new StringBuilder();
			params.append("(");
			for (int i = 0; i < list.size(); i++) {
				String arg0 = list.get(i);
				String type0 = "String";
				if (types.containsKey(arg0)) {
					type0 = types.get(arg0);
				}
				type0 = type0.replaceAll("\\.\\.\\.", "[]");
				if (arg0.endsWith("...]")) {
					arg0 = arg0.substring(1, list.get(i).length() - 4);
					params.append(F("{0}[] {1}", type0, arg0));
					count.append(F("paramslen += {0}.length;\n", arg0));
				} else if (type0.endsWith("[]")) {
					params.append(F("{0} {1}", type0, arg0));
					count.append(F("paramslen += {0}.length;\n", arg0));
				} else {
					params.append(F("{0} {1}", type0, arg0));
					count.append("paramslen++;\n");
				}
				if (i + 1 < list.size())
					params.append(", ");
			}
			params.append(") {\n");

			java.stmt(params.toString());

			java.stmt(count.toString());
			java.stmt("\n");

			java.stmt("  try {\n");
			java.stmt("    Socket socket = new Socket(this.hostname, this.port);\n");
			java.stmt("    OutputStream output = socket.getOutputStream();\n");
			java.stmt("    InputStream input = socket.getInputStream();\n");
			java.stmt("\n");

			java.stmt("    output.write(\"*\".getBytes());\n");
			java.stmt("    output.write(String.valueOf(paramslen + 1).getBytes());\n");
			java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
			java.stmt("\n");

			java.stmt("    output.write(\"$\".getBytes());\n");
			java.stmt(F("    output.write(\"{0}\".getBytes());\n", String.valueOf(command.length())));
			java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
			java.stmt(F("    output.write(\"{0}\".getBytes());\n", command));
			java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
			java.stmt("\n");

			for (String arg : list) {
				if (arg.endsWith("...]")) {
					String arg0 = arg.substring(1, arg.length() - 4);
					java.stmt(F("    for (int i = 0; i < {0}.length; i++)", arg0)).stmt(" {\n");
					java.stmt("    	output.write(\"$\".getBytes());\n");
					java.stmt(F("    	output.write(String.valueOf({0}[i].length()).getBytes());\n", arg0));
					java.stmt("    	output.write(\"\\r\\n\".getBytes());\n");
					java.stmt(F("    	output.write({0}[i].getBytes());\n", arg0));
					java.stmt("    	output.write(\"\\r\\n\".getBytes());\n");
					java.stmt("    }\n");
					java.stmt("\n");
				} else if (types.containsKey(arg) && types.get(arg).endsWith("...")) {
					String arg0 = arg;
					java.stmt(F("    for (int i = 0; i < {0}.length; i++)", arg0)).stmt(" {\n");
					java.stmt("    	output.write(\"$\".getBytes());\n");
					java.stmt(F("    	output.write(String.valueOf({0}[i].length()).getBytes());\n", arg0));
					java.stmt("    	output.write(\"\\r\\n\".getBytes());\n");
					java.stmt(F("    	output.write({0}[i].getBytes());\n", arg0));
					java.stmt("    	output.write(\"\\r\\n\".getBytes());\n");
					java.stmt("    }\n");
					java.stmt("\n");
				} else if (types.containsKey(arg) && types.get(arg).equals("int")) {
					java.stmt("    output.write(\"$\".getBytes());\n");
					java.stmt(F("    output.write(String.valueOf(String.valueOf({0}).length()).getBytes());\n", arg));
					java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
					java.stmt(F("    output.write(String.valueOf({0}).getBytes());\n", arg));
					java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
					java.stmt("\n");
				} else {
					java.stmt("    output.write(\"$\".getBytes());\n");
					java.stmt(F("    output.write(String.valueOf({0}.length()).getBytes());\n", arg));
					java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
					java.stmt(F("    output.write({0}.getBytes());\n", arg));
					java.stmt("    output.write(\"\\r\\n\".getBytes());\n");
					java.stmt("\n");
				}
			}

			java.stmt("    char r0 = (char) input.read();\n");
			java.stmt("    ByteArrayOutputStream buff = new ByteArrayOutputStream();\n");
			java.stmt("    for (byte b = (byte) input.read(); '\\r' != b; b = (byte) input.read()) {\n");
			java.stmt("      buff.write(b);\n");
			java.stmt("    }\n");
			java.stmt("    input.read();\n");
			java.stmt("\n");

			java.stmt("    if ('-' == r0) {\n");
			java.stmt("      input.close();\n");
			java.stmt("      output.close();\n");
			java.stmt("      throw new RuntimeException(buff.toString());\n");
			java.stmt("    }\n");
			java.stmt("\n");

			if ("MULTIBULK".equals(return_type)) {
				java.stmt("      int size = Integer.parseInt(buff.toString());\n");
				java.stmt("      if (size == -1) {\n");
				java.stmt("      	input.close();\n");
				java.stmt("      	output.close();\n");
				java.stmt("      	return Arrays.asList();\n");
				java.stmt("      }\n");
				java.stmt("      List<String> result = new LinkedList<String>();\n");
				java.stmt("      for (int i = 0; i < size; i++) {\n");
				java.stmt("      	char r02 = (char) input.read();\n");
				java.stmt("      	ByteArrayOutputStream buff2 = new ByteArrayOutputStream();\n");
				java.stmt("      	for (byte b = (byte) input.read(); '\\r' != b; b = (byte) input.read()) {\n");
				java.stmt("      		buff2.write(b);\n");
				java.stmt("      	}\n");
				java.stmt("      	input.read();\n");
				java.stmt("      	int len = Integer.parseInt(buff2.toString());\n");
				java.stmt("      	if (len == -1) {\n");
				java.stmt("      		result.add(null);\n");
				java.stmt("      		continue;\n");
				java.stmt("      	}\n");
				java.stmt("      	byte[] response = new byte[len];\n");
				java.stmt("      	input.read(response);\n");
				java.stmt("      	input.read();\n");
				java.stmt("      	input.read();\n");
				java.stmt("      	result.add(new String(response));\n");
				java.stmt("      }\n");
				java.stmt("\n");
			}

			if ("BULK".equals(return_type)) {

				java.stmt("    int len = Integer.parseInt(buff.toString());\n");
				java.stmt("    if (len == -1) {\n");
				java.stmt("        input.close();\n");
				java.stmt("        output.close();\n");
				java.stmt("        return null;\n");
				java.stmt("    }\n");
				java.stmt("\n");

				java.stmt("    byte[] response = new byte[len];\n");
				java.stmt("    input.read(response);\n");
				java.stmt("    input.read();\n");
				java.stmt("    input.read();\n");
				java.stmt("\n");

				java.stmt("    input.close();\n");
				java.stmt("    output.close();\n");
				java.stmt("\n");

				java.stmt("    return new String(response);\n");

			} else if ("STATUS".equals(return_type)) {

				java.stmt("    input.close();\n");
				java.stmt("    output.close();\n");
				java.stmt("\n");

				java.stmt("    return buff.toString();\n");

			} else if ("INTEGER".equals(return_type)) {

				java.stmt("    input.close();\n");
				java.stmt("    output.close();\n");
				java.stmt("\n");

				java.stmt("    return new Integer(buff.toString());\n");

			} else if ("MULTIBULK".equals(return_type)) {
				java.stmt("    input.close();\n");
				java.stmt("    output.close();\n");
				java.stmt("\n");

				java.stmt("    return result;\n");
			}

			java.stmt("  }\n");
			java.stmt("  catch(Exception e) {\n");
			java.stmt("    throw new RedisException(e);\n");
			java.stmt("  }\n");
			java.stmt("}\n");
			return java.toString();
		}
	}

	class JavaBuilder {

		private final StringBuilder builder = new StringBuilder();

		public JavaBuilder stmt(String statement) {
			builder.append(statement);
			return this;
		}

		@Override
		public String toString() {
			return builder.toString();
		}
	}

	private static final String F(String pattern, String... arguments) {
		return MessageFormat.format(pattern, arguments);
	}

	private final OutputStream protocol = new ByteArrayOutputStream();

	public ProtocolCompiler(InputStream source) {

		JavaBuilder java = new JavaBuilder();

		java.stmt("package com.nominanuda.store.redis;\n");
		java.stmt("\n");

		java.stmt("import java.io.*;\n");
		java.stmt("import java.net.*;\n");
		java.stmt("import java.text.*;\n");
		java.stmt("import java.util.*;\n");
		java.stmt("\n");

		java.stmt("public class Redis {\n");
		java.stmt("\n");
		java.stmt("		private final String hostname;\n");
		java.stmt("		private final int port;\n");
		java.stmt("\n");

		java.stmt("	public Redis(String hostname, int port) {\n");
		java.stmt("		this.hostname = hostname;\n");
		java.stmt("		this.port = port;\n");
		java.stmt("	}\n");
		java.stmt("\n");

		java.stmt("	class RedisException extends RuntimeException {\n");
		java.stmt("\n");

		java.stmt("		private static final long serialVersionUID = 3753411847230253753L;\n");
		java.stmt("\n");

		java.stmt("		RedisException(Throwable e) {\n");
		java.stmt("			super(e);\n");
		java.stmt("		}\n");
		java.stmt("\n");

		java.stmt("		RedisException(Throwable e, String pattern, String... arguments) {\n");
		java.stmt("			super(MessageFormat.format(pattern, arguments), e);\n");
		java.stmt("		}\n");
		java.stmt("	}\n");
		java.stmt("\n");

		try {
			protocol.write(java.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Command command = null;

		Scanner scanner = new Scanner(source).useDelimiter("\n");

		while (scanner.hasNext()) {
			String line = scanner.next();
			if ("".equals(line))
				continue;
			if (line.startsWith("#"))
				continue;

			Pattern pattern = Pattern.compile("(\\w+)\\s+\\((.*)\\)\\s+(\\w+)");
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				String cmd = matcher.group(1);
				String signature = matcher.group(2);
				String[] parts = signature.split(",");
				String repl = matcher.group(3);
				try {
					protocol.write(new Command(cmd, parts, repl).toString().getBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				String[] parts = line.split(" |:");
				try {
					protocol.write(new Command(parts).toString().getBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		try {
			protocol.write("}\n".getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream getProtocol() {
		return protocol;
	}

}
