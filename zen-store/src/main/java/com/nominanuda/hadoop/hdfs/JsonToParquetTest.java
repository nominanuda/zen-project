package com.nominanuda.hadoop.hdfs;

import static com.nominanuda.lang.Strings.notNullOrBlank;
import static com.nominanuda.obj.JsonDeserializer.JSON_DESERIALIZER;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map.Entry;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.GenericData.Record;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nominanuda.lang.Strings;
import com.nominanuda.obj.JsonDeserializer;
import com.nominanuda.obj.Obj;
import com.nominanuda.obj.Stru;

public class JsonToParquetTest {
	static Schema avroSchema;
	static {
		Schema.Parser parser = new Schema.Parser();
		avroSchema = parser.parse("{\"type\":\"record\",\"name\":\"Track\",\"fields\":[{\"name\":\"trackCode\",\"type\":[\"long\",\"null\"]},{\"name\":\"albumCode\",\"type\":[\"long\",\"null\"]},{\"name\":\"artistCode\",\"type\":[\"long\",\"null\"]},{\"name\":\"genreCode\",\"type\":[\"long\",\"null\"]},{\"name\":\"usageType\",\"type\":[\"string\",\"null\"]},{\"name\":\"userId\",\"type\":[\"string\",\"null\"]},{\"name\":\"subscription\",\"type\":[\"string\",\"null\"]},{\"name\":\"deviceId\",\"type\":[\"string\",\"null\"]},{\"name\":\"appId\",\"type\":[\"string\",\"null\"]},{\"name\":\"sample\",\"type\":[\"string\",\"null\"]},{\"name\":\"auth\",\"type\":[\"string\",\"null\"]},{\"name\":\"pin\",\"type\":[\"string\",\"null\"]},{\"name\":\"cli\",\"type\":[\"string\",\"null\"]},{\"name\":\"offline\",\"type\":[\"boolean\",\"null\"]},{\"name\":\"type\",\"type\":[\"string\",\"null\"]},{\"name\":\"occurredAt\",\"type\":[\"long\",\"null\"]}]}");
		System.err.println(avroSchema.toString(true));
	}
	@Test
	public void test() throws IOException {
		Configuration hc = new Configuration();
		hc.addResource(new Path("/opt/hadoop/etc/hadoop/core-site.xml"));

		FileSystem dfs = FileSystem.get(hc);

		ParquetWriter<Object> avroParquetWriter = AvroParquetWriter
			.builder(new Path("hdfs://localhost:8020/eventstore/trackplay/2015/08/26/20150826230000000-20150827000000000.trackplay.parquet"))
			.withSchema(avroSchema)
			.build();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("20150826230000000-20150827000000000.trackplay.json")));
		String line;
		while((line = br.readLine()) != null) {
			if(notNullOrBlank(line)) {
				Obj o = (Obj)JSON_DESERIALIZER.deserialize(line);
				o.del("_id");
				Record r = struToAvro(o);
				avroParquetWriter.write(r);
				//System.err.println(o.toString());
			}
		}
		avroParquetWriter.close();
	}

	private Record struToAvro(Obj o) {
		GenericRecordBuilder b = new GenericRecordBuilder(avroSchema/*.getField("Track").schema()*/);
		for(Entry<String, Object> member : o) {
			b.set(member.getKey(), member.getValue());
		}
		Record r = b.build();
		return r;
	}
}
