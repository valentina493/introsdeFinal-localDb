package localdbservices.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.WebServiceException;

import localdbservices.dao.MyDatabaseDao;
import utility.DateAdapter;
import utility.DatePersistenceConverter;

/**
 * The persistent class for the "Goals" database table.
 * 
 */
@Entity
@Table(name = "Goals")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "_goalId", "personId", "measureType", "minvalue", "maxvalue", "deadline", "created" })
@NamedQueries({
		//@NamedQuery(name = "Goal.findAll", query = "SELECT g FROM Goal g"),
		@NamedQuery(name = "Goal.findActiveGoalByPersonAndMeasureType", query = "SELECT g FROM Goal g WHERE g.personId = :person AND g.measureType = :mtype AND g.deadline >= :date AND g.evaluated = false"),
		@NamedQuery(name = "Goal.findActiveGoalByPerson", query = "SELECT g FROM Goal g WHERE g.personId = :person AND g.deadline >= :date AND g.evaluated = false"),
		@NamedQuery(name = "Goal.findGoalByPerson", query = "SELECT g FROM Goal g WHERE g.personId = :person"),
		@NamedQuery(name = "Goal.findExpiredGoalsByPerson", query = "SELECT g FROM Goal g WHERE g.personId = :person AND g.deadline <= :date AND g.evaluated = false")})
		//@NamedQuery(name = "Goal.findNonExpiredGoalsByPerson", query = "SELECT g FROM Goal g WHERE g.person = :person AND g.deadline > :date ") })
public class Goal implements Serializable {
	private static final long serialVersionUID = 7894574996689231403L;

	@Id
	@Column(name = "_goalId")
	@GeneratedValue(generator = "sqlite_goal")
	@TableGenerator(name = "sqlite_goal", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Goal")
	private long _goalId;

	@Temporal(TemporalType.DATE)
	@Column(name = "created")
	@Convert(converter = DatePersistenceConverter.class)
	private Date created;

	@Temporal(TemporalType.DATE)
	@Column(name = "deadline")
	@Convert(converter = DatePersistenceConverter.class)
	private Date deadline;

	@Column(name = "minvalue")
	private double minvalue;

	@Column(name = "maxvalue")
	private double maxvalue;

	//bi-directional many-to-one association to MeasureType
	@ManyToOne
	@JoinColumn(name = "measureTypeId", referencedColumnName = "_measureTypeId", insertable = true, updatable = true)
	private MeasureType measureType;

//	//bi-directional many-to-one association to Person
//	@ManyToOne
//	@JoinColumn(name = "personId", referencedColumnName = "_personId", insertable = true, updatable = true)
//	private Person person;

	@Column(name = "personId")
	private long personId;

	@Column(name = "evaluated")
	private boolean evaluated;

	public Goal() {
	}

	public long get_goalId() {
		return this._goalId;
	}

	public void set_goalId(long _goalId) {
		this._goalId = _goalId;
	}

	/* public String getComparator() { return this.comparator; }
	 * 
	 * public void setComparator(String comparator) { this.comparator =
	 * comparator; }
	 * 
	 * /*@XmlJavaTypeAdapter(DateAdapter.class) public Date getDeadline() {
	 * return this.deadline; }
	 * 
	 * public void setDeadline(Date deadline) { this.deadline = deadline; } */

	public double getMinvalue() {
		return this.minvalue;
	}

	public void setMinvalue(double value) {
		this.minvalue = value;
	}

	public double getMaxvalue() {
		return this.maxvalue;
	}

	public void setMaxvalue(double value) {
		this.maxvalue = value;
	}

	public MeasureType getMeasureType() {
		return this.measureType;
	}

	public void setMeasureType(MeasureType measureType) {
		this.measureType = measureType;
	}

//	public Person getPerson() {
//		return this.person;
//	}
//
//	public void setPerson(Person person) {
//		this.person = person;
//	}

	public long getPersonId() {
		return this.personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@XmlTransient
	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	/*
	public static List<Goal> getAll() {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findAll", Goal.class).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}*/
		
	public static Goal findGoal(long goalId) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		Goal g = em.find(Goal.class, goalId);
		MyDatabaseDao.instance.closeConnections(em);
		return g;
	}

	public static void setEvaluated(List<Goal> evalGoals){
		for(Goal g : evalGoals){
			g.evaluated = true;
			updateGoal(g);
		}
	}
	
	public static List<Goal> getAllByPerson(long person) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findGoalByPerson", Goal.class).setParameter("person", person)
				.getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}

	public static Goal getActiveGoalByPersonByMeasureType(long person, MeasureType mt) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findActiveGoalByPersonAndMeasureType", Goal.class)
				.setParameter("person", person).setParameter("mtype", mt).setParameter("date", new Date())
				.getResultList();

		//Goal g = em.createNamedQuery("Goal.findActiveGoalByPersonAndMeasureType", Goal.class)
		//	.setParameter("person", personId).setParameter("mtype", mt).setParameter("date", new Date()).getSingleResult();

		MyDatabaseDao.instance.closeConnections(em);
		//return g;

		//if there is more than one element, return the first one
		return (list.size() > 0) ? list.get(0) : null; // USO QUESTO E NON UN SINGLERESULT PERCHè è PIù SICURO --> ALTRIMENTI Dà ERRORE
	}

	public static List<Goal> getActiveGoalByPerson(long person) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findActiveGoalByPerson", Goal.class)
				.setParameter("person", person).setParameter("date", new Date()).getResultList();

		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}

	public static List<Goal> getExpiredGoals(long person) throws ParseException {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findExpiredGoalsByPerson", Goal.class)
				.setParameter("person", person).setParameter("date", new Date()).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}

	/*public static List<Goal> getNotExpiredGoals(Person person) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Goal> list = em.createNamedQuery("Goal.findNonExpiredGoalsByPerson", Goal.class)
				.setParameter("person", person).setParameter("date", new Date()).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}*/

	public static Goal updateGoal(Goal g) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		g = em.merge(g);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
		return g;
	}

	public static Goal createGoal(Goal g) {

		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(g);
			tx.commit();
		} catch (RollbackException ex) {
			throw new WebServiceException("ERROR: the goal could not be inserted in the database: " + ex.getCause());
		} finally {
			MyDatabaseDao.instance.closeConnections(em);
		}
		return g;
	}

	public static void removeGoal(Goal g) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		g = em.merge(g);
		em.refresh(g);
		em.remove(g);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
	}

	@Override
	public String toString() {
		return this._goalId + " in [" + this.minvalue + ", " + this.maxvalue + "], created: " + this.created
				+ "; expiration date: " + this.deadline;
	}

}