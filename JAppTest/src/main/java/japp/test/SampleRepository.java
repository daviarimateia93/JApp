package japp.test;

import javax.persistence.EntityManager;

import japp.model.jpa.repository.Repository;

public class SampleRepository extends Repository<SampleEntity, Long> {
	
	public SampleRepository(final EntityManager entityManager) {
		super(SampleEntity.class, entityManager);
	}
}
