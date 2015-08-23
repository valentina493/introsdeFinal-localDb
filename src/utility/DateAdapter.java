package utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date>{

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String marshal(Date d) throws Exception {
		// from date to xml string
		return dateFormat.format(d);
	}

	@Override
	public Date unmarshal(String s) {
		// from xml string to date
		if (s == null) {
			return null;
		}
		Date date = null;
		try {
			date = dateFormat.parse(s);
		} catch (Exception ex) {
			return null;
		}
		return date;
	}
}
