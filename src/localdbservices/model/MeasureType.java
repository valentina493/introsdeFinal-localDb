package localdbservices.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;

import localdbservices.dao.MyDatabaseDao;

/**
 * The persistent class for the "MeasureType" database table.
 * 
 */
@Entity
@Table(name = "MeasureType")
@NamedQueries({ @NamedQuery(name = "MeasureType.findAll", query = "SELECT m FROM MeasureType m"),
		@NamedQuery(name = "MeasureType.findById", query = "SELECT m FROM MeasureType m WHERE m._measureTypeId = :id"),
		@NamedQuery(name = "MeasureType.findByName", query = "SELECT m FROM MeasureType m WHERE m.name = :name") })
@XmlRootElement(name = "MeasureType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "_measureTypeId", "name", "unit"})
public class MeasureType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "sqlite_measuret")
	@TableGenerator(name = "sqlite_measuret", table = "sqlite_sequence", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "MeasureType")
	@Column(name = "_measureTypeId")
	private long _measureTypeId;

	@Column(name = "name")
	private String name;

	@Column(name = "unit")
	private String unit;

	public MeasureType() {
	}

	public long get_measureTypeId() {
		return this._measureTypeId;
	}

	public void set_measureTypeId(long _measureTypeId) {
		this._measureTypeId = _measureTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public static List<MeasureType> getAll() {

		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		List<MeasureType> list = em.createNamedQuery("MeasureType.findAll", MeasureType.class).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return list;
	}

	public static MeasureType getById(long id) {

		EntityManager em = MyDatabaseDao.instance.createEntityManager();
		MeasureType mt = em.find(MeasureType.class, id);
		MyDatabaseDao.instance.closeConnections(em);
		return mt;
	}

	public static MeasureType getByName(String name) {

		EntityManager em = MyDatabaseDao.instance.createEntityManager();

		List<MeasureType> mtList = em.createNamedQuery("MeasureType.findByName", MeasureType.class).setParameter("name", name).getResultList();
		MyDatabaseDao.instance.closeConnections(em);
		return mtList.size() > 0 ? mtList.get(0) : null;
	}

	@Override
	public String toString() {
		return this.name + " (" + this.unit + ")";
	}
}