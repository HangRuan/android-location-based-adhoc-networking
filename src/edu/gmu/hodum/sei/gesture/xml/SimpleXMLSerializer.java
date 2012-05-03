package edu.gmu.hodum.sei.gesture.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SimpleXMLSerializer<T> {


	private Serializer serializer;
	public SimpleXMLSerializer(){
		serializer = new Persister();
	}


	public byte[] serialize (Object source) throws Exception{
		//serializes an object annotated with SimpleXML to a byte array 
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;

		serializer.write(source, bos);

		// Get the bytes of the serialized object
		byte[] bytes = bos.toByteArray();
		bos.close();

		//serializeToFile(source, "broadcast.xml");

		return bytes;
	}
	public void serializeToFile(Object source, String fileName) throws Exception{
		//serializes an object annotated with SimpleXML to a file
		File testFile = new File(fileName);
		serializer.write(source, testFile);
	}
	public T deserialize (Class<T> type,  byte[] source) throws Exception{
		//turns a flattened, SimpleXML object represented as bytes back into the object

		// Deserialize from a byte array
		T object = serializer.read(type, (new ByteArrayInputStream(source)));

		return object;
	}
	public boolean validate(Class<T> type, byte[]source) throws Exception{
		//returns true if the byte array properly validates against the class XML object
		return serializer.validate(type, (new ByteArrayInputStream(source)));
	}

}

