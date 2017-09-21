/**
 * Copyright (c) 2014 Wteamfly.  All rights reserved. 网飞公司 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.helper;

import java.lang.reflect.Field;

/**
 * null对象工具类.
 * 
 * @since 5.0.0
 */
public final class NullHelper {

    /**
     * 私有构造方法，其作用是为了防止用户显式生成工具类的实例对象.
     * 
     */
    private NullHelper() {
    }

    /**
     * 判断对象是否代表null.
     * 
     * @param obj
     *            待判断是否代表null的对象.
     * @param <T>
     *            对象类型.
     * @return true 该对象代表null false 该对象不代表null.
     */
    public static <T> Boolean isNull(final T obj) {
        Boolean result = false;
        Class<? extends Object> clazz = obj.getClass();

        Class<NullConstant> nullConstant = NullConstant.class;
        Field[] nullObjects = nullConstant.getFields();
        for (int i = 0; i < nullObjects.length; i++) {
            if (clazz.isAssignableFrom(nullObjects[i].getType())) {
                try {
                    result = nullObjects[i].get(nullConstant) == obj;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
