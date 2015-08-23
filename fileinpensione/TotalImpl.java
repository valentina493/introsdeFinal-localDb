package localdbservices.soap;

import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;

import localdbservices.model.Goal;
import localdbservices.model.MeasureType;
import localdbservices.model.Measurement;
import localdbservices.model.Person;

@WebService(endpointInterface = "localdbservices.soap.People", serviceName = "PeopleService")
public class TotalImpl implements Total {

	@Override
	public List<Person> readPersonList() {
		return Person.getAll();
	}

	@Override
	public Person readPerson(Long id) {
		return Person.getPersonById(id);
	}

	@Override
	public Person updatePerson(Person p) {
		Person existing = Person.getPersonById(p.get_personId());
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + p.get_personId() + " could not be found.");
		}

		Person.updatePerson(p);
		return p;
	}

	@Override
	public Person createPerson(Person p) {
		p.set_personId(0);
		return Person.createPerson(p);
	}

	@Override
	public void deletePerson(Long id) {
		Person existing = Person.getPersonById(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + id + " could not be found.");
		}
		Person.removePerson(existing);
	}

	@Override
	public List<MeasureType> readMeasureTypeList() {
		return MeasureType.getAll();
	}

	@Override
	public MeasureType readMeasureTypeById(long id) {
		return MeasureType.getById(id);
	}

	@Override
	public MeasureType readMeasureTypeByName(String name) {
		return MeasureType.getByName(name);
	}

	@Override
	public List<Goal> readGoalsList() {
		return Goal.getAll();
	}

	@Override
	public List<Goal> readGoalsByPerson(Long id) {
		Person existing = Person.getPersonById(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + id + " could not be found.");
		}
		return Goal.getAllByPerson(existing);
	}

	@Override
	public List<Goal> readExpiredGoalsByPerson(Long id) {
		Person existing = Person.getPersonById(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + id + " could not be found.");
		}
		return Goal.getExpiredGoals(existing);
	}

	@Override
	public List<Goal> readNotExpiredGoalsByPerson(Long id) {
		Person existing = Person.getPersonById(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + id + " could not be found.");
		}
		return Goal.getNotExpiredGoals(existing);
	}

	@Override
	public Goal readGoal(Long id) {
		return Goal.findGoal(id);
	}

	@Override
	public Goal updateGoal(Goal g) {
		Goal existing = Goal.findGoal(g.get_goalId());

		if (existing == null) {
			throw new WebServiceException("ERROR: the goal with id " + g.get_goalId() + " could not be found.");
		}

		if (g.getMeasureType() != null) {

			MeasureType mt = MeasureType.getById(g.getMeasureType().get_measureTypeId());

			if (mt == null) {
				throw new WebServiceException("ERROR: the inserted measureType with id " + g.getMeasureType().get_measureTypeId() + " could not be found.");
			}
		} else {
			throw new WebServiceException("ERROR: no measureType was specified.");
		}

		return Goal.updateGoal(g);
	}

	@Override
	public Goal createGoal(Goal g, long personId, String measureTypeName) {
		Person existing = Person.getPersonById(personId);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + personId + " could not be found.");
		}
		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new WebServiceException("ERROR: the measure " + measureTypeName + " could not be found.");
		}

		g.set_goalId(0);
		g.setPerson(existing);
		g.setMeasureType(existingMeasureType);
		return Goal.createGoal(g);
	}

	@Override
	public void deleteGoal(Long id) {
		Goal existing = Goal.findGoal(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the goal with id " + id + " could not be found.");
		}
		Goal.removeGoal(existing);
	}

	@Override
	public List<Measurement> readMeasurementList() {
		return Measurement.getAll();
	}

	@Override
	public Measurement readMeasurement(long id) {
		return Measurement.findMeasurement(id);
	}

	@Override
	public List<Measurement> readMeasurementListByPerson(long personId) {
		Person existing = Person.getPersonById(personId);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + personId + " could not be found.");
		}
		return Measurement.getListByPerson(existing);
	}

	@Override
	public Measurement updateMeasurement(Measurement m) {
		Measurement existing = Measurement.findMeasurement(m.get_measurementId());
		if (existing == null) {
			throw new WebServiceException("ERROR: the measurement with id " + m.get_measurementId() + " could not be found.");
		}
		
		if (m.getMeasureType() != null) {
			MeasureType mt = MeasureType.getById(m.getMeasureType().get_measureTypeId());

			if (mt == null) {
				throw new WebServiceException("ERROR: the inserted measureType with id " + m.getMeasureType().get_measureTypeId() + " could not be found.");
			} else{
				m.setMeasureType(mt); // in caso mi dà id e altri dati del measuretype che non coincidono
			}
		} else {
			throw new WebServiceException("ERROR: no measureType was specified.");
		}
		
		return Measurement.updateMeasurement(m);
	}

	@Override
	public Measurement createMeasurement(Measurement m, long personId, String measureTypeName) {
		Person existing = Person.getPersonById(personId);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + personId + " could not be found.");
		}
		MeasureType existingMeasureType = MeasureType.getByName(measureTypeName);
		if (existingMeasureType == null) {
			throw new WebServiceException("ERROR: the measure " + measureTypeName + " could not be found.");
		}
		m.set_measurementId(0);
		m.setPerson(existing);
		m.setMeasureType(existingMeasureType);
		return Measurement.createMeasurement(m);
	}

	@Override
	public void deleteMeasurement(Long id) {
		Measurement existing = Measurement.findMeasurement(id);
		if (existing == null) {
			throw new WebServiceException("ERROR: the person with id " + id + " could not be found.");
		}
		Measurement.removeMeasurement(existing);
	}

}
