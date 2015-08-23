package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DatePersistenceConverter implements AttributeConverter<java.util.Date, String> {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String convertToDatabaseColumn(Date date) {
		// from entity attribute to database column
		if (date == null) {
			return null;
		}
		return dateFormat.format(date);
	}

	@Override
	public Date convertToEntityAttribute(String date) {
		// from database column to entity attribute
		if(date == null){
			return null;
		}
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
}
