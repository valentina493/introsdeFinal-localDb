package localdbservices.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public enum MyDatabaseDao {
	instance;
	String persistenceUnitName = "introsde_finalassignment";
	private EntityManagerFactory emf;

	private MyDatabaseDao() {
		if (emf != null) {
			emf.close();
		}
		try {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	public EntityManager createEntityManager() {

		try {
			return emf.createEntityManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closeConnections(EntityManager em) {
		em.close();
	}

	public EntityTransaction getTransaction(EntityManager em) {
		return em.getTransaction();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
}
