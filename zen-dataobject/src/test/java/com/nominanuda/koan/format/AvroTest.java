package com.nominanuda.koan.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.junit.Test;

public class AvroTest {

	@Test
	public void test() throws IOException {
		Schema.Parser parser = new Schema.Parser();
		Schema schema = parser.parse("{\"type\":\"record\",\"name\":\"Track\",\"fields\":[{\"name\":\"title\",\"type\":\"string\"}]}");
		System.err.println(schema.toString(true));
		GenericRecordBuilder b = new GenericRecordBuilder(schema/*.getField("Track").schema()*/);
		
		b.set("title", "Snow White");
		Record r = b.build();
		DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
		writer.write(r, encoder);
		encoder.flush();
		out.flush();
		System.err.println(out.toByteArray());

		// deserialize data
		DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
		Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
		Record datum = new GenericData.Record(schema);
		GenericRecord gr = reader.read(datum, decoder);
		System.out.println(gr);
		
	}
	@Test
	public void tests() throws IOException {
//		final String schemaStr = "{\"type\":\"record\",\"name\":\"TableRecord\",\"fields\":[{\"name\":\"ActionCode\",\"type\":\"string\"},{\"name\":\"Fields\",\"type\":{\"type\":\"map\",\"values\":[\"string\",\"long\",\"double\",\"null\"]}}]}";
//
//	    // create some data
//	    Map<String, Object> originalMap = new Hashtable<>();
//	    originalMap.put("Ric", "sZwmXAdYKv");
//	    originalMap.put("QuoteId", 4342740204922826921L);
//	    originalMap.put("CompanyName", "8PKQ9va3nW8pRWb4SjPF2DvdQDBmlZ");
//	    originalMap.put("Category", "AvrIfd");
//
//	    // serialize data
//	    Schema.Parser parser = new Schema.Parser();
//	    Schema schema = parser.parse(schemaStr);
//	    ByteArrayOutputStream out = new ByteArrayOutputStream();
//	    DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
//	    Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
//	    GenericRecord datum = new GenericData.Record(schema);
//	    datum.put("ActionCode", "R");
//	    datum.put("Map", originalMap);
//	    writer.write(datum, encoder);
//	    encoder.flush();
//	    out.flush();
//
//	    // deserialize data
//	    DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
//	    Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
//	    datum = new GenericData.Record(schema);
//	    Map<String, Object> deserializedMap = (Map<String, Object>) reader.read(datum, decoder).get("Map");
//	    System.out.println(originalMap);
//	    System.out.println(deserializedMap);
//	    Assert.assertEquals("Maps data don't match", originalMap, deserializedMap);
		
//		
//		
//		
//		
//		
//		
//		
//		
//		Schema schema = null;
//		SpecificData specificData = new SpecificData();
//		DatumWriter<Map<String, ?>> dw = specificData.createDatumWriter(schema);
////		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
//		Map<String, ?> blog1 = new HashMap<>();
//		DataFileWriter<?> dataFileWriter = new DataFileWriter(dw);
//		dataFileWriter.create(schema, new File("/tmp/test.avro"));
//		dataFileWriter.append(blog1);
//		dataFileWriter.close();
//
////		GenericRecord user1 = new GenericData.Record(schema);
////		user1.put("name", "Alyssa");
////		user1.put("favorite_number", 256);
////		// Leave favorite color null
////
////		GenericRecord user2 = new GenericData.Record(schema);
////		user2.put("name", "Ben");
////		user2.put("favorite_number", 7);
////		user2.put("favorite_color", "red");
////		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
////		DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
////		dataFileWriter.create(user1.getSchema(), new File("users.avro"));
////		dataFileWriter.append(user1);
////		dataFileWriter.append(user2);
////		dataFileWriter.append(user3);
////		dataFileWriter.close();
	}

}
