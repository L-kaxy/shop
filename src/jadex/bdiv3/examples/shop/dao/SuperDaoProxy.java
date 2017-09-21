/**
 * Copyright (c) 2014 Wteamfly.  All rights reserved. 网飞公司 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.beanutils.BeanMap;

import jadex.bdiv3.examples.shop.helper.JPAHelper;
import jadex.bdiv3.examples.shop.helper.NullHelper;

/**
 * 超级DaoProxy类.
 * 
 * @since 3.0.0
 */
public final class SuperDaoProxy {

	/**
	 * 单例对象.
	 */
	private static SuperDaoProxy instance = new SuperDaoProxy();

	/**
	 * 单例模式的私有构造方法.
	 */
	private SuperDaoProxy() {
	}

	/**
	 * 获取单例.
	 * 
	 * @return 单例
	 */
	public static SuperDaoProxy getInstance() {
		return instance;
	}

	/**
	 * 添加实体.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @param currentUser
	 *            当前操作用户，数据库记录该用户进行添加，不得为null.
	 * @throws Exception
	 *             IllegalArgumentException entityPo，currentUser为null时抛出此异常.
	 * 
	 * @since 3.0.0
	 */
	public <T> void addEntity(final T entityPo) throws Exception {
		if (entityPo == null) {
			throw new IllegalArgumentException("The entityPo must not be null");
		}
		EntityManager entityManager = JPAHelper.getEntityManager();
		entityManager.persist(entityPo);
	}

	/**
	 * 删除实体.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 3.0.0
	 */
	public <T> void deleteEntity(final T entityPo) throws Exception {
		if (entityPo == null) {
			throw new IllegalArgumentException("The entityPo must not be null");
		}

		EntityManager entityManager = JPAHelper.getEntityManager();
		// 根据参数查询待删除条目
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> query = criteriaBuilder.createQuery(entityPo.getClass());
		EntityType<?> model = entityManager.getMetamodel().entity(entityPo.getClass());
		Root<?> from = query.from(model);
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		// 找非空参数
		BeanMap beanMap = new BeanMap(entityPo);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				predicatesList.add(criteriaBuilder.equal(from.get(propertyName), parm.getValue()));
			}
		}
		query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));

		for (Object temp : entityManager.createQuery(query).getResultList()) {
			entityManager.remove(temp);
		}
	}

	/**
	 * 编辑实体.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @param currentUser
	 *            当前操作用户，数据库记录该用户进行编辑，不得为null.
	 * @throws Exception
	 *             IllegalArgumentException entityPo，currentUser为null时抛出此异常.
	 * 
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> void editEntity(final T entityPo) throws Exception {
		if (entityPo == null) {
			throw new IllegalArgumentException("The entityPo must not be null");
		}

		EntityManager entityManager = JPAHelper.getEntityManager();
		Class<?> entityClass = entityPo.getClass();

		Long entityId = null;
		for (Method m : entityClass.getMethods()) {
			if (m.isAnnotationPresent(Id.class)) {
				entityId = (Long) m.invoke(entityPo);
				break;
			}
		}

		Object rEntityPo = entityManager.find(entityClass, entityId);

		// 找非空参数
		BeanMap beanMap = new BeanMap(entityPo);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		BeanMap rBeanMap = new BeanMap(rEntityPo);
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				if (NullHelper.isNull(parm.getValue())) {
					rBeanMap.put(propertyName, null);
				} else {
					rBeanMap.put(propertyName, parm.getValue());
				}
			}
		}
		entityManager.merge(rEntityPo);

	}

	/**
	 * 根据实体主键编号查询实体，queryEntity(T)也可以实现该方法的功能， 但它们的区别在于该方法执行效率更高.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param cls
	 *            实体类型，不得为null.
	 * @param id
	 *            实体主键编号，不得为null.
	 * @return 与实体主键编号对应的实体.
	 * @throws Exception
	 *             IllegalArgumentException cls，id为null时抛出此异常.
	 * 
	 * @since 4.0.4
	 */
	public <T> T getEntityById(final Class<T> cls, final Long id) throws Exception {
		if (cls == null) {
			throw new IllegalArgumentException("The cls must not be null");
		}
		if (id == null) {
			throw new IllegalArgumentException("The id must not be null");
		}

		T result = null;
		EntityManager entityManager = JPAHelper.getEntityManager();
		result = (T) entityManager.find(cls, id);
		return result;
	}

	/**
	 * 查询实体列表.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param aimParm
	 *            实体，作为精确搜索的查询条件，不得为null.
	 * @param likeParm
	 *            实体，作为模糊搜索的查询条件，可为null.
	 * @return 以实体类型为node的列表.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 5.0.0
	 */
	public <T> List<T> queryList(final T aimParm, final T likeParm) throws Exception {
		if (aimParm == null) {
			throw new IllegalArgumentException("The aimParm must not be null");
		}

		List<T> result = null;
		EntityManager entityManager = JPAHelper.getEntityManager();

		// 根据参数查询条目
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> query = criteriaBuilder.createQuery(aimParm.getClass());
		EntityType<?> model = entityManager.getMetamodel().entity(aimParm.getClass());
		Root<?> from = query.from(model);
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		// 找模糊搜索非空参数
		if (likeParm != null) {
			BeanMap likeBeanMap = new BeanMap(likeParm);
			String likePropertyName = null;
			Map.Entry<String, Object> likeEntityParm = null;
			for (Object temp : likeBeanMap.entrySet()) {
				likeEntityParm = (Entry<String, Object>) temp;
				likePropertyName = likeEntityParm.getKey();
				if (likeEntityParm.getValue() != null
						&& likeEntityParm.getValue().getClass().isAssignableFrom(String.class)) {
					predicatesList.add(criteriaBuilder.like(from.<String> get(likePropertyName),
							likeEntityParm.getValue().toString()));
				}
			}
		}

		// 找非空参数
		BeanMap beanMap = new BeanMap(aimParm);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				predicatesList.add(criteriaBuilder.equal(from.get(propertyName), parm.getValue()));
			}
		}
		query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));

		result = (List<T>) entityManager.createQuery(query).setHint("org.hibernate.cacheable", true).getResultList();

		return result;
	}

	/**
	 * 查询实体列表.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @return 以实体类型为node的列表.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 3.0.0
	 */
	public <T> List<T> queryList(final T entityPo) throws Exception {

		List<T> result = null;
		result = queryList(entityPo, null);

		return result;
	}

	/**
	 * 查询单一实体.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @return 实体结果.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 4.0.2
	 */
	public <T> T queryEntity(final T entityPo) throws Exception {
		if (entityPo == null) {
			throw new IllegalArgumentException("The entityPo must not be null");
		}

		T result = null;
		EntityManager entityManager = JPAHelper.getEntityManager();

		// 根据参数查询条目
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> query = criteriaBuilder.createQuery(entityPo.getClass());
		EntityType<?> model = entityManager.getMetamodel().entity(entityPo.getClass());
		Root<?> from = query.from(model);
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		// 找非空参数
		BeanMap beanMap = new BeanMap(entityPo);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				predicatesList.add(criteriaBuilder.equal(from.get(propertyName), parm.getValue()));
			}
		}
		query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));

		result = (T) entityManager.createQuery(query).setHint("org.hibernate.cacheable", true).getSingleResult();
		return result;
	}

	/**
	 * 查询条目数列表.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param aimParm
	 *            实体，作为精确搜索的查询条件，不得为null.
	 * @param likeParm
	 *            实体，作为模糊搜索的查询条件，可为null.
	 * @return 查询结果条目数.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @author 侯骏雄
	 * @since 3.0.0
	 */
	public <T> Long queryCount(final T aimParm, final T likeParm) throws Exception {
		if (aimParm == null) {
			throw new IllegalArgumentException("The aimParm must not be null");
		}

		Long result = null;
		EntityManager entityManager = JPAHelper.getEntityManager();

		// 根据参数查询条目
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		EntityType<?> model = entityManager.getMetamodel().entity(aimParm.getClass());
		Root<?> from = query.from(model);
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		// 找模糊搜索非空参数
		if (likeParm != null) {
			BeanMap likeBeanMap = new BeanMap(likeParm);
			String likePropertyName = null;
			Map.Entry<String, Object> likeEntityParm = null;
			for (Object temp : likeBeanMap.entrySet()) {
				likeEntityParm = (Entry<String, Object>) temp;
				likePropertyName = likeEntityParm.getKey();
				if (likeEntityParm.getValue() != null
						&& likeEntityParm.getValue().getClass().isAssignableFrom(String.class)) {
					predicatesList.add(criteriaBuilder.like(from.<String> get(likePropertyName),
							likeEntityParm.getValue().toString()));
				}
			}
		}

		// 找非空参数
		BeanMap beanMap = new BeanMap(aimParm);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				predicatesList.add(criteriaBuilder.equal(from.get(propertyName), parm.getValue()));
			}
		}
		query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));

		query.select(criteriaBuilder.countDistinct(from));
		result = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true).getSingleResult();

		return result;
	}

	/**
	 * 查询条目数列表.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @return 查询结果条目数.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 3.0.0
	 */
	public <T> Long queryCount(final T entityPo) throws Exception {

		Long result = null;
		result = queryCount(entityPo, null);

		return result;
	}

	/**
	 * 查询是否有查询结果.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param aimParm
	 *            实体，作为精确搜索的查询条件，不得为null.
	 * @param likeParm
	 *            实体，作为模糊搜索的查询条件，可为null.
	 * @return true-有结果 false-无结果.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 5.0.0
	 */
	public <T> Boolean hasEntity(final T aimParm, final T likeParm) throws Exception {
		if (aimParm == null) {
			throw new IllegalArgumentException("The aimParm must not be null");
		}

		Boolean result = false;
		Long count = null;
		EntityManager entityManager = JPAHelper.getEntityManager();

		// 根据参数查询条目
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		EntityType<?> model = entityManager.getMetamodel().entity(aimParm.getClass());
		Root<?> from = query.from(model);
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		// 找模糊搜索非空参数
		if (likeParm != null) {
			BeanMap likeBeanMap = new BeanMap(likeParm);
			String likePropertyName = null;
			Map.Entry<String, Object> likeEntityParm = null;
			for (Object temp : likeBeanMap.entrySet()) {
				likeEntityParm = (Entry<String, Object>) temp;
				likePropertyName = likeEntityParm.getKey();
				if (likeEntityParm.getValue() != null
						&& likeEntityParm.getValue().getClass().isAssignableFrom(String.class)) {
					predicatesList.add(criteriaBuilder.like(from.<String> get(likePropertyName),
							likeEntityParm.getValue().toString()));
				}
			}
		}

		// 找非空参数
		BeanMap beanMap = new BeanMap(aimParm);
		String propertyName = null;
		Map.Entry<String, Object> parm = null;
		for (Object temp : beanMap.entrySet()) {
			parm = (Entry<String, Object>) temp;
			propertyName = parm.getKey();
			if (!propertyName.equals("class") && parm.getValue() != null) {
				predicatesList.add(criteriaBuilder.equal(from.get(propertyName), parm.getValue()));
			}
		}
		query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));

		query.select(criteriaBuilder.countDistinct(from));
		count = entityManager.createQuery(query).setMaxResults(1).setHint("org.hibernate.cacheable", true)
				.getSingleResult();
		if (count > 0) {
			result = true;
		}

		return result;
	}

	/**
	 * 查询是否有查询结果.
	 * 
	 * @param <T>
	 *            实体类型.
	 * @param entityPo
	 *            实体，不得为null.
	 * @return true-有结果 false-无结果.
	 * @throws Exception
	 *             IllegalArgumentException entityPo为null时抛出此异常.
	 * 
	 * @since 5.0.0
	 */
	public <T> Boolean hasEntity(final T entityPo) throws Exception {
		Boolean result = false;

		result = hasEntity(entityPo, null);

		return result;
	}

}
