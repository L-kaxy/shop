/**
 * Copyright (c) 2014 Wteamfly.  All rights reserved. 网飞公司 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.helper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * JPA工具类.
 * 
 * @author 侯骏雄
 * @since 4.0.0
 */
public final class JPAHelper {

    /**
     * 实体管理工厂.
     */
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = buildEntityManagerFactory();

    /**
     * 绑定线程的实体管理器.
     */
    private static final DemuxEntityManager DEMUX_ENTITY_MANAGER = new DemuxEntityManager();

    /**
     * 私有构造方法.
     * 
     */
    private JPAHelper() {
    }

    /**
     * 创建实体管理工厂.
     * 
     * @return 实体管理工厂.
     * @author 侯骏雄
     * @since 4.0.0
     */
    private static EntityManagerFactory buildEntityManagerFactory() {
        EntityManagerFactory result = Persistence
                .createEntityManagerFactory("mysqlJPA");
        return result;
    }

    /**
     * 获取的实体管理工厂.
     * 
     * @return 获取的实体管理工厂
     * @author 侯骏雄
     * @since 4.0.0
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    /**
     * 首先会判断当前线程是否已有实体管理器，如果没有则创建并返回，如果有则判断是否已关闭如果已关闭则创建并返回，否则直接返回.
     * 
     * @return 获取的实体管理器
     * @author 侯骏雄
     * @since 5.0.0
     */
    public static EntityManager getEntityManager() {
        EntityManager result = null;

        Boolean isBind = false;
        Boolean isOpen = false;
        isBind = DEMUX_ENTITY_MANAGER.isBind();
        if (isBind) {
            isOpen = DEMUX_ENTITY_MANAGER.isOpen();
        }

        if (!isBind || !isOpen) {
            EntityManager entityManager = ENTITY_MANAGER_FACTORY
                    .createEntityManager();
            DEMUX_ENTITY_MANAGER.bindManager(entityManager);
        }
        result = DEMUX_ENTITY_MANAGER;
        return result;
    }

}
