package pl.pwl.samouczek.renderer;

import java.io.UnsupportedEncodingException;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XmlUtils {

	public static String prettyPrintedDocument(Document document) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
		LSSerializer serializer = impl.createLSSerializer();
		serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
		String utf16doc = serializer.writeToString(document);
		try {
			String utf8doc = new String(utf16doc.getBytes("UTF-8"), "UTF-8");
			utf8doc = utf8doc.replace("UTF-16", "UTF-8");
			return utf8doc;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
