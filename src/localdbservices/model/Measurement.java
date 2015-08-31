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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.RollbackException;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.WebServiceException;

import localdbservices.dao.MyDatabaseDao;
import utility.DateAdapter;
import utility.DatePersistenceConverter;

@Entity
@Table(name = "Measurement")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "_measurementId", "personId", "value", "measureType", "measuringDate" })
@NamedQueries({
		@NamedQuery(name = "Measurement.findMeasuresByPersonAndMeasuretype", query = "SELECT m FROM Measurement m WHERE m.personId = :person AND m.measureType = :measureType order by m.measuringDate desc, m._measurementId desc"),
		@NamedQuery(name = "Measurement.findLastMeasurementForEachMeasuretypeByPerson", query = "SELECT m FROM Measurement m WHERE m.personId = :person GROUP BY m.measureType HAVING m.measuringDate = max(m.measuringDate)"),
		@NamedQuery(name = "Measurement.findMeasuresByPerson", query = "SELECT m FROM Measurement m WHERE m.personId = :person order by m.measuringDate desc, m._measurementId desc") })
public class Measurement implements Serializable {

	private static final long serialVersionUID = -5058245510824494780L;

	@Id
	@Column(name = "_measurementId")
	@GeneratedValue(generator = "sqlite_msm")
	@TableGenerator(name = "sqlite_msm", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "Measurement")
	private long _measurementId;

	@Column(name = "value")
	private double value;

	@Temporal(TemporalType.DATE)
	@Column(name = "measuringDate")
	@Convert(converter = DatePersistenceConverter.class)
	private Date measuringDate;

	@ManyToOne
	@JoinColumn(name = "measureTypeId", referencedColumnName = "_measureTypeId", insertable = true, updatable = true)
	private MeasureType measureType;

	@Column(name = "personId")
	private long personId;

	public long get_measurementId() {
		return _measurementId;
	}

	public void set_measurementId(long _measurementId) {
		this._measurementId = _measurementId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getMeasuringDate() {
		return measuringDate;
	}

	public void setMeasuringDate(Date measuringDate) {
		this.measuringDate = measuringDate;
	}

	public MeasureType getMeasureType() {
		return measureType;
	}

	public void setMeasureType(MeasureType measureType) {
		this.measureType = measureType;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public static Measurement findMeasurement(long measurementId) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		Measurement measurement = em.find(Measurement.class, measurementId);
		MyDatabaseDao.instance.closeConnections(em);
		return measurement;
	}

	public static Measurement findLastMeasurement(long person, MeasureType measureType) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Measurement> measurements = em
				.createNamedQuery("Measurement.findMeasuresByPersonAndMeasuretype", Measurement.class)
				.setParameter("person", person).setParameter("measureType", measureType).setMaxResults(1)
				.getResultList();

		MyDatabaseDao.instance.closeConnections(em);
		if (measurements.isEmpty()) {
			return null;
		} else {
			return measurements.get(0);
		}
	}

	public static List<Measurement> findLastMeasurementForEachMeasuretype(long person) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();

		List<Measurement> measurements = em
				.createNamedQuery("Measurement.findLastMeasurementForEachMeasuretypeByPerson", Measurement.class)
				.setParameter("person", person).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return measurements;
	}

	public static List<Measurement> findMeasurementsByPersonByMeasureType(long person, MeasureType measureType) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Measurement> measurements = em
				.createNamedQuery("Measurement.findMeasuresByPersonAndMeasuretype", Measurement.class)
				.setParameter("person", person).setParameter("measureType", measureType).getResultList();

		MyDatabaseDao.instance.closeConnections(em);
		return measurements;
	}

	public static List<Measurement> getListByPerson(long person) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<Measurement> measurements = em.createNamedQuery("Measurement.findMeasuresByPerson", Measurement.class)
				.setParameter("person", person).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return measurements;
	}

	public static Measurement updateMeasurement(Measurement m) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		m = em.merge(m);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
		return m;
	}

	public static Measurement createMeasurement(Measurement m) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(m);
			tx.commit();
		} catch (RollbackException ex) {
			throw new WebServiceException("ERROR: the measurement could not be inserted in the database: "
					+ ex.getCause());
		} finally {
			MyDatabaseDao.instance.closeConnections(em);
		}
		return m;
	}

	public static void removeMeasurement(Measurement m) {
		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		m = em.merge(m);
		em.refresh(m);
		em.remove(m);
		tx.commit();
		MyDatabaseDao.instance.closeConnections(em);
	}

	@Override
	public String toString() {
		return this._measurementId + " " + this.value + " " + this.measureType + " " + this.personId;
	}

}
