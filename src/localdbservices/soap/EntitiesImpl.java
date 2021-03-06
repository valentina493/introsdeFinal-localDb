package localdbservices.soap;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.joda.time.DateTime;
import org.joda.time.Days;

import localdbservices.model.Goal;
import localdbservices.model.MeasureType;
import localdbservices.model.Measurement;
import localdbservices.model.Person;

@WebService(endpointInterface = "localdbservices.soap.Entities", serviceName = "EntitiesService")
public class EntitiesImpl implements Entities {

	/* PEOPLE REQUESTS */
	//PEOPLE REQUEST #1
	@Override
	public List<Person> readPersonList() {
		return Person.getAll();
	}

	//PEOPLE REQUEST #2
	@Override
	public Person readPerson(Long id) throws SOAPFaultException, SOAPException {
		Person p = Person.getPersonById(id);
		if (p == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + id + " not found"));
		}
		return p;
	}

	//PEOPLE REQUEST #3
	@Override
	public Person createPerson(Person p) throws SOAPFaultException, SOAPException {
		if (p.getFirstname() == null || p.getLastname() == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 firstname and lastname are mandatory"));
		}

		p.set_personId(0);
		return Person.createPerson(p);
	}

	//PEOPLE REQUEST #4
	@Override
	public Person updatePerson(Person p) throws SOAPFaultException, SOAPException {
		if (p.get_personId() == 0 || p.getFirstname() == null || p.getLastname() == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 _personId, firstname and lastname are mandatory"));
		}

		Person existing = Person.getPersonById(p.get_personId());
		if (existing == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + p.get_personId()
					+ " not found"));
		}

		Person.updatePerson(p);
		return p;
	}

	//PEOPLE REQUEST #5
	@Override
	public void deletePerson(Long id) throws SOAPFaultException, SOAPException {
		Person existing = Person.getPersonById(id);
		if (existing == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + id + " not found"));
		}
		Person.removePerson(existing);
	}

	/* GOAL REQUESTS */

	//GOAL REQUEST #1
	@Override
	public Goal readGoal(Long id) throws SOAPFaultException, SOAPException {
		Goal g = Goal.findGoal(id);
		if (g == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 goal with id " + id + " not found"));
		}

		return g;
	}

	//GOAL REQUEST #2
	@Override
	public List<Goal> readGoalsByPerson(Long personId) throws SOAPFaultException, SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}
		return Goal.getAllByPerson(personId);
	}

	//GOAL REQUEST #3
	@Override
	public List<Goal> readActiveGoalsByPerson(Long personId) throws SOAPFaultException, SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		return Goal.getActiveGoalByPerson(personId);
	}

	//GOAL REQUEST #4
	@Override
	public Goal readActiveGoalByPersonByMeasureType(Long personId, String measureTypeName) throws SOAPFaultException,
			SOAPException {
		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + measureTypeName
					+ " not found"));
		}

		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		return Goal.getActiveGoalByPersonByMeasureType(personId, existingMeasureType);
	}

	//GOAL REQUEST #5
	@Override
	public List<Goal> readExpiredGoalsByPerson(Long personId) throws ParseException, SOAPFaultException, SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		return Goal.getExpiredGoals(personId);
	}

	//GOAL REQUEST #6
	@Override
	public Goal createGoal(Goal g, long personId, String measureTypeName) throws SOAPFaultException, SOAPException {
		// a goal for height, heart rate, blood pressure min and blood pressure max cannot be created 
		// because its outcome cannot be controlled
		if (measureTypeName.contentEquals("height") || measureTypeName.contentEquals("blood pressure min")
				|| measureTypeName.contentEquals("blood pressure max") || measureTypeName.contentEquals("heart rate")) {

			throw new SOAPFaultException(constructSoapFault("Sender", "400 a goal for " + measureTypeName
					+ " can never be created"));
		}

		if (g.getDeadline() == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the deadline must be specified"));
		}

		if (g.getMinvalue() == 0.0 && g.getMaxvalue() == 0.0) {
			throw new SOAPFaultException(constructSoapFault("Sender",
					"400 either the minimum or the maximum value must be specified"));
		}

		g.setEvaluated(false);
		if (g.getMaxvalue() == 0.0) {
			g.setMaxvalue(Double.POSITIVE_INFINITY);
		}

		if (g.getMaxvalue() <= g.getMinvalue()) {
			throw new SOAPFaultException(constructSoapFault("Sender",
					"400 the maximum value must be greater than the minimum value"));
		}

		// you cannot create a goal that expires today
		if (Days.daysBetween(new DateTime(g.getDeadline()), new DateTime(new Date()).withTimeAtStartOfDay()).getDays() >= 0) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the deadline chosen (" + g.getDeadline()
					+ ") is past or present"));
		}

		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + measureTypeName
					+ " not found"));
		}

		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		// retrieve the active goal for the chosen measuretype
		// active, i.e. not expired
		Goal activeGoalForThisMeasureType = Goal.getActiveGoalByPersonByMeasureType(personId, existingMeasureType);
		if (activeGoalForThisMeasureType != null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 goal for " + measureTypeName
					+ " exists already. You cannot create another one"));
		}

		g.set_goalId(0);
		g.setCreated(new Date());
		g.setPersonId(personId);
		g.setMeasureType(existingMeasureType);
		return Goal.createGoal(g);
	}

	//GOAL REQUEST #7
	@Override
	public Goal updateGoal(Goal g) throws SOAPFaultException, SOAPException {
		// you cannot change person, measuretype and created, therefore they do not need to be specified
		// deadline && (minvalue || maxvalue) must be specified

		if (g.getDeadline() == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the deadline must be specified"));
		}

		if (g.getMinvalue() == 0.0 && g.getMaxvalue() == 0.0) {
			throw new SOAPFaultException(constructSoapFault("Sender",
					"400 either the minimum or the maximum value must be specified"));
		}

		if (g.getMaxvalue() == 0.0) {
			g.setMaxvalue(Double.POSITIVE_INFINITY);
		}

		if (g.getMaxvalue() <= g.getMinvalue()) {
			throw new SOAPFaultException(constructSoapFault("Sender",
					"400 the maximum value must be greater than the minimum value"));
		}

		// you cannot create a goal that expires today or before
		if (Days.daysBetween(new DateTime(g.getDeadline()), new DateTime(new Date()).withTimeAtStartOfDay()).getDays() >= 0) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the deadline chosen (" + g.getDeadline()
					+ ") is past or present"));
		}

		Goal existingGoal = Goal.findGoal(g.get_goalId());
		if (existingGoal == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 goal with id " + g.get_goalId()
					+ " not found"));
		}

		// neither measureType or created can be changed
		g.setCreated(existingGoal.getCreated());
		g.setEvaluated(existingGoal.isEvaluated());
		g.setPersonId(existingGoal.getPersonId());

		if (g.getMeasureType() == null) {
			//if no measuretype was specified -> then set its original one
			g.setMeasureType(existingGoal.getMeasureType());
		} else if (existingGoal.getMeasureType().get_measureTypeId() != g.getMeasureType().get_measureTypeId()) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the measure type "
					+ existingGoal.getMeasureType().getName() + " cannot be changed (" + g.getMeasureType().getName()
					+ ")"));
		}

		return Goal.updateGoal(g);
	}

	//GOAL REQUEST #8
	@Override
	public void deleteGoal(Long id) throws SOAPFaultException, SOAPException {
		Goal existing = Goal.findGoal(id);
		if (existing == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 goal with id " + id + " not found"));
		}
		Goal.removeGoal(existing);
	}
	
	//GOAL REQUEST #9
	@Override
	public void setEvaluatedGoals(List<Goal> goals) throws SOAPFaultException, SOAPException {
		Goal.setEvaluated(goals);
		
	}

	/* MEASUREMENT REQUESTS */

	//MEASUREMENT REQUEST #1
	@Override
	public Measurement readMeasurement(long id) throws SOAPFaultException, SOAPException {
		Measurement m = Measurement.findMeasurement(id);

		if (m == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measurement with id " + id + " not found"));
		}

		return m;
	}

	//MEASUREMENT REQUEST #2
	@Override
	public List<Measurement> readMeasurementListByPerson(long personId) throws SOAPFaultException, SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		return Measurement.getListByPerson(personId);
	}

	//MEASUREMENT REQUEST #3
	@Override
	public Measurement readLastMeasurementByPersonAndMeasureType(long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + measureTypeName
					+ " not found"));
		}

		return Measurement.findLastMeasurement(personId, existingMeasureType);
	}

	//MEASUREMENT REQUEST #4
	@Override
	public List<Measurement> readLastMeasurementForEachMeasureTypeByPerson(long personId) throws SOAPFaultException,
			SOAPException {
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		return Measurement.findLastMeasurementForEachMeasuretype(personId);
	}

	//MEASUREMENT REQUEST #5
	@Override
	public List<Measurement> readMeasurementsByPersonAndMeasureType(long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException {

		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + measureTypeName
					+ " not found"));
		}

		return Measurement.findMeasurementsByPersonByMeasureType(personId, existingMeasureType);
	}

	//MEASUREMENT REQUEST #6
	@Override
	public Measurement createMeasurement(Measurement m, long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException {
		if(m.getValue() == 0.0){
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the value must be specified"));
		}
		
		Person existingPerson = Person.getPersonById(personId);
		if (existingPerson == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 person with id " + personId + " not found"));
		}

		// the value is the only field that must be entered
		if (m.getMeasuringDate() == null) {
			m.setMeasuringDate(new Date());
		} else if (m.getMeasuringDate().after(new Date())) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the measuring date ("+m.getMeasuringDate()+") is in the future"));
		}

		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + measureTypeName
					+ " not found"));
		}

		// you can only enter one measurement per day per measureType
		Measurement lastMeasurement = Measurement.findLastMeasurement(personId, existingMeasureType);
		if (lastMeasurement != null
				&& Days.daysBetween(new DateTime(lastMeasurement.getMeasuringDate()),
						new DateTime(m.getMeasuringDate())).getDays() == 0) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 a measurement for " + measureTypeName
					+ " exists already for this date"));
		}

		m.set_measurementId(0);
		m.setPersonId(personId);
		m.setMeasureType(existingMeasureType);
		return Measurement.createMeasurement(m);
	}

	//MEASUREMENT REQUEST #7
	@Override
	public Measurement updateMeasurement(Measurement m) throws SOAPFaultException, SOAPException {
		if (m.getValue() == 0.0) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the value must be specified"));
		}
		if (m.getMeasuringDate() == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 the measuring date must be specified"));
		}

		Measurement existingMeasurement = Measurement.findMeasurement(m.get_measurementId());
		if (existingMeasurement == null) {
			throw new SOAPFaultException(constructSoapFault("Sender",
					"404 measurement with id " + m.get_measurementId() + " not found"));
		}

		// person is ignored 
		m.setPersonId(existingMeasurement.getPersonId());

		if (m.getMeasureType() != null) {
			MeasureType mt = MeasureType.getByName(m.getMeasureType().getName());

			if (mt == null) {
				throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type "
						+ m.getMeasureType().getName() + " not found"));
			} else {
				// I do this in case the inner data of measureType (e.g. id, name, ..) are not the same as standard ones
				m.setMeasureType(mt); 			}
		} else {
			throw new SOAPFaultException(constructSoapFault("Sender", "400 measure type must be specified"));
		}

		return Measurement.updateMeasurement(m);
	}

	//MEASUREMENT REQUEST #8
	@Override
	public void deleteMeasurement(Long id) throws SOAPFaultException, SOAPException {
		Measurement existingMeasurement = Measurement.findMeasurement(id);
		if (existingMeasurement == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measurement with id " + id + " not found"));
		}
		Measurement.removeMeasurement(existingMeasurement);
	}

	/* MEASURETYPE REQUESTS */

	//MEASURETYPE REQUEST #1
	@Override
	public List<MeasureType> readMeasureTypeList() {
		return MeasureType.getAll();
	}

	//MEASURETYPE REQUEST #2
	@Override
	public MeasureType readMeasureTypeById(long id) throws SOAPFaultException, SOAPException {
		MeasureType mt = MeasureType.getById(id);
		if (mt == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type with id " + id + " not found"));
		}
		return mt;
	}

	//MEASURETYPE REQUEST #3
	@Override
	public MeasureType readMeasureTypeByName(String name) throws SOAPFaultException, SOAPException {
		MeasureType mt = MeasureType.getByName(name);

		if (mt == null) {
			throw new SOAPFaultException(constructSoapFault("Sender", "404 measure type " + name + " not found"));
		}
		return mt;
	}

	private SOAPFault constructSoapFault(String faultcode, String faultstring) throws SOAPException {
		SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createFault();
		soapFault.setFaultString(faultstring);
		soapFault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, faultcode));

		return soapFault;
	}

	
}
