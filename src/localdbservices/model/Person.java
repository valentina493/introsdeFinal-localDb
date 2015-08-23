package localdbservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import localdbservices.dao.MyDatabaseDao;
import utility.DateAdapter;
import utility.DatePersistenceConverter;

@Entity
@Table(name = "Person")
@NamedQueries({ @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
		@NamedQuery(name = "Person.deleteGoalsOfPerson", query = "delete from Goal g where g.personId = :person"),
		@NamedQuery(name = "Person.deleteMeasurementsOfPerson", query = "delete from Measurement m where m.personId = :person")})
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "_personId", "firstname", "lastname", "birthdate" })
@XmlRootElement
public class Person implements Serializable {

	private static final long serialVersionUID = -544305745712329937L;

	@Id
	@GeneratedValue(generator = "sqlite_person")
	@TableGenerator(name = "sqlite_person", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Person")
	@Column(name = "_personId")
	private long _personId;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Temporal(TemporalType.DATE)
	@Column(name = "birthdate")
	@Convert(converter = DatePersistenceConverter.class)
	private Date birthdate;

	//	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	//	private List<Goal> goalList;
	//	
	//	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	//	private List<Measurement> measurementList;

	public long get_personId() {
		return this._personId;
	}

	public void set_personId(long _personId) {
		this._personId = _personId;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	//	@XmlTransient
	//	public List<Goal> getGoalList() {
	//		return goalList;
	//	}
	//
	//	public void setGoalList(List<Goal> goalList) {
	//		this.goalList = goalList;
	//	}
	//	
	//	@XmlTransient
	//	public List<Measurement> getMeasurementList() {
	//		return measurementList;
	//	}
	//
	//	public void setMeasurementList(List<Measurement> measurementList) {
	//		this.measurementList = measurementList;
	//	}

	public static List<Person> getAll() {

		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}

	public static Person getPersonById(long personId) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		Person p = em.find(Person.class, personId);
		MyDatabaseDao.instance.closeConnections(em);
		return p;
	}

	public static Person updatePerson(Person p) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p = em.merge(p);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
		return p;
	}

	public static Person createPerson(Person p) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
		return p;
	}

	public static void removePerson(Person p) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.createNamedQuery("Person.deleteGoalsOfPerson").setParameter("person", p.get_personId()).executeUpdate();
		em.createNamedQuery("Person.deleteMeasurementsOfPerson").setParameter("person", p.get_personId()).executeUpdate();

		p = em.merge(p);
		em.refresh(p);
		em.remove(p);

		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
	}

}
