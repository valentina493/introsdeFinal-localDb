package localdbservices.soap;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import localdbservices.model.Goal;
import localdbservices.model.MeasureType;
import localdbservices.model.Measurement;
import localdbservices.model.Person;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface Total {

	//REQUEST #1
	@WebMethod(operationName = "readPersonList")
	@WebResult(name = "people")
	public List<Person> readPersonList();

	//REQUEST #2
	@WebMethod(operationName = "readPerson")
	@WebResult(name = "person")
	public Person readPerson(Long id);

	//REQUEST #3
	@WebMethod(operationName = "updatePerson")
	@WebResult(name = "person")
	public Person updatePerson(Person p);

	//REQUEST #4
	@WebMethod(operationName = "createPerson")
	@WebResult(name = "person")
	public Person createPerson(Person p);

	//REQUEST #5
	@WebMethod(operationName = "deletePerson")
	@WebResult(name = "person")
	public void deletePerson(Long id);

	//REQUEST #5
	@WebMethod(operationName = "readMeasureTypeList")
	@WebResult(name = "measureList")
	public List<MeasureType> readMeasureTypeList();

	//REQUEST #5
	@WebMethod(operationName = "readMeasureTypesById")
	@WebResult(name = "measureList")
	public MeasureType readMeasureTypeById(long id);

	//REQUEST #5
	@WebMethod(operationName = "readMeasureTypesByName")
	@WebResult(name = "measure")
	public MeasureType readMeasureTypeByName(String name);

	//REQUEST #1
	@WebMethod(operationName = "readGoalsList")
	@WebResult(name = "goals")
	public List<Goal> readGoalsList();

	//REQUEST #2
	@WebMethod(operationName = "readGoalsByPerson")
	@WebResult(name = "goals")
	public List<Goal> readGoalsByPerson(Long id);

	//REQUEST #2
	@WebMethod(operationName = "readExpiredGoalsByPerson")
	@WebResult(name = "goals")
	public List<Goal> readExpiredGoalsByPerson(Long id);

	@WebMethod(operationName = "readNotExpiredGoalsByPerson")
	@WebResult(name = "goals")
	public List<Goal> readNotExpiredGoalsByPerson(Long id);

	@WebMethod(operationName = "readGoal")
	@WebResult(name = "goal")
	public Goal readGoal(Long id);

	//REQUEST #3
	@WebMethod(operationName = "updateGoal")
	@WebResult(name = "goal")
	public Goal updateGoal(Goal g);

	//REQUEST #4
	@WebMethod(operationName = "createGoal")
	@WebResult(name = "goal")
	public Goal createGoal(Goal g, long personId, String measureTypeName);

	//REQUEST #5
	@WebMethod(operationName = "deleteGoal")
	@WebResult(name = "goal")
	public void deleteGoal(Long id);

	@WebMethod(operationName = "readMeasurementList")
	@WebResult(name = "measurements")
	public List<Measurement> readMeasurementList();
	
	@WebMethod(operationName = "readMeasurementById")
	@WebResult(name = "measurement")
	public Measurement readMeasurement(long id);
	
	@WebMethod(operationName = "readMeasurementListByPerson")
	@WebResult(name = "measurements")
	public List<Measurement> readMeasurementListByPerson(long personId);
	
	@WebMethod(operationName = "updateMeasurement")
	@WebResult(name = "measurement")
	public Measurement updateMeasurement(Measurement m);

	//REQUEST #4
	@WebMethod(operationName = "createMeasurement")
	@WebResult(name = "measurement")
	public Measurement createMeasurement(Measurement m, long personId, String measureTypeName);

	//REQUEST #5
	@WebMethod(operationName = "deleteMeasurement")
	@WebResult(name = "measurement")
	public void deleteMeasurement(Long id);
}
