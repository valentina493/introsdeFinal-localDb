package localdbservices.soap;

import java.text.ParseException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.soap.SOAPException;
import javax.xml.ws.soap.SOAPFaultException;

import localdbservices.model.Goal;
import localdbservices.model.MeasureType;
import localdbservices.model.Measurement;
import localdbservices.model.Person;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface Entities {

	/* PEOPLE REQUESTS */
	//PEOPLE REQUEST #1
	@WebMethod(operationName = "readPersonList")
	@WebResult(name = "person")
	public List<Person> readPersonList();

	//PEOPLE REQUEST #2
	@WebMethod(operationName = "readPerson")
	@WebResult(name = "person")
	public Person readPerson(Long id) throws SOAPFaultException, SOAPException;

	//PEOPLE REQUEST #3
	@WebMethod(operationName = "createPerson")
	@WebResult(name = "person")
	public Person createPerson(Person p) throws SOAPFaultException, SOAPException;

	//PEOPLE REQUEST #4
	@WebMethod(operationName = "updatePerson")
	@WebResult(name = "person")
	public Person updatePerson(Person p) throws SOAPFaultException, SOAPException;

	//PEOPLE REQUEST #5
	@WebMethod(operationName = "deletePerson")
	@WebResult(name = "person")
	public void deletePerson(Long id) throws SOAPFaultException, SOAPException;

	/* GOAL REQUESTS */
	//GOAL REQUEST #1
	@WebMethod(operationName = "readGoal")
	@WebResult(name = "goal")
	public Goal readGoal(Long id) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #2
	@WebMethod(operationName = "readGoalsByPerson")
	@WebResult(name = "goal")
	public List<Goal> readGoalsByPerson(Long id) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #3
	@WebMethod(operationName = "readActiveGoalsByPerson")
	@WebResult(name = "goal")
	public List<Goal> readActiveGoalsByPerson(Long personId) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #4
	@WebMethod(operationName = "readActiveGoalByPersonByMeasureType")
	@WebResult(name = "goal")
	public Goal readActiveGoalByPersonByMeasureType(Long personId, String measureTypeName) throws SOAPFaultException,
			SOAPException;

	//GOAL REQUEST #5
	@WebMethod(operationName = "readExpiredGoalsByPerson")
	@WebResult(name = "goal")
	public List<Goal> readExpiredGoalsByPerson(Long id) throws ParseException, SOAPFaultException, SOAPException;

	//GOAL REQUEST #6
	@WebMethod(operationName = "createGoal")
	@WebResult(name = "goal")
	public Goal createGoal(Goal g, long personId, String measureTypeName) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #7
	@WebMethod(operationName = "updateGoal")
	@WebResult(name = "goal")
	public Goal updateGoal(Goal g) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #8
	@WebMethod(operationName = "deleteGoal")
	@WebResult(name = "goal")
	public void deleteGoal(Long id) throws SOAPFaultException, SOAPException;

	//GOAL REQUEST #9
	@WebMethod(operationName = "setEvaluatedGoals")
	@WebResult(name = "goal")
	public void setEvaluatedGoals(List<Goal> goals) throws SOAPFaultException, SOAPException;

	/* MEASUREMENT REQUESTS */

	//MEASUREMENT REQUEST #1
	@WebMethod(operationName = "readMeasurementById")
	@WebResult(name = "measurement")
	public Measurement readMeasurement(long id) throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #2
	@WebMethod(operationName = "readMeasurementsByPerson")
	@WebResult(name = "measurement")
	public List<Measurement> readMeasurementListByPerson(long personId) throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #3
	@WebMethod(operationName = "readMeasurementsByPersonAndMeasureType")
	@WebResult(name = "measurement")
	public List<Measurement> readMeasurementsByPersonAndMeasureType(long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #4
	@WebMethod(operationName = "readLastMeasurementForEachMeasureTypeByPerson")
	@WebResult(name = "measurement")
	public List<Measurement> readLastMeasurementForEachMeasureTypeByPerson(long personId) throws SOAPFaultException,
			SOAPException;

	//MEASUREMENT REQUEST #5
	@WebMethod(operationName = "readLastMeasurementByPersonAndMeasureType")
	@WebResult(name = "measurement")
	public Measurement readLastMeasurementByPersonAndMeasureType(long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #6
	@WebMethod(operationName = "createMeasurement")
	@WebResult(name = "measurement")
	public Measurement createMeasurement(Measurement m, long personId, String measureTypeName)
			throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #7
	@WebMethod(operationName = "updateMeasurement")
	@WebResult(name = "measurement")
	public Measurement updateMeasurement(Measurement m) throws SOAPFaultException, SOAPException;

	//MEASUREMENT REQUEST #8
	@WebMethod(operationName = "deleteMeasurement")
	@WebResult(name = "measurement")
	public void deleteMeasurement(Long id) throws SOAPFaultException, SOAPException;

	/* MEASURETYPE REQUESTS */

	//MEASURETYPE REQUEST #1
	@WebMethod(operationName = "readMeasureTypeList")
	@WebResult(name = "measureType")
	public List<MeasureType> readMeasureTypeList();

	//MEASURETYPE REQUEST #2
	@WebMethod(operationName = "readMeasureTypesById")
	@WebResult(name = "measureType")
	public MeasureType readMeasureTypeById(long id) throws SOAPFaultException, SOAPException;

	//MEASURETYPE REQUEST #3
	@WebMethod(operationName = "readMeasureTypesByName")
	@WebResult(name = "measureType")
	public MeasureType readMeasureTypeByName(String name) throws SOAPFaultException, SOAPException;

}
