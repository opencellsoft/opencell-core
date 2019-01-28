package org.meveo.service.custom;

import javax.ejb.Stateless;

import org.meveo.service.base.NativePersistenceService;

@Stateless
public class CustomTableService extends NativePersistenceService {

    // public void createClass(String customTableName) {
    //
    // ClassPool pool = ClassPool.getDefault();
    // ClassClassPath classPath = new ClassClassPath(this.getClass());
    // pool.insertClassPath(classPath);
    // log.error("AKK inserted classpath {}", classPath);
    // CtClass cc = pool.makeClass("org.meveo." + customTableName);
    //
    // try {
    // CtField f = new CtField(CtClass.intType, "z", cc);
    // cc.addField(f);
    //
    // cc.addMethod(CtNewMethod.getter("getZ", f));
    // cc.addMethod(CtNewMethod.setter("setZ", f));
    // cc.addMethod(CtNewMethod.make("public String toString() {return \" \"+z;}", cc));
    //
    // cc.writeFile("C:\\andrius\\programs\\wildfly-10.1.0.Final\\standalone\\deployments\\opencell.war\\WEB-INF\\classes");
    //
    // Class clazz = cc.toClass();
    // Object instance = clazz.newInstance();
    // Field field = ReflectionUtils.getField(clazz, "z");
    // field.setAccessible(true);
    // field.set(instance, 10);
    // log.error("AKK field value is {}", field.get(instance));
    //
    // Object value = getEntityManager().createNativeQuery("select id from cust_cet", CustomTableRecord.class).getSingleResult();
    //
    // log.error("AKK Value from DB is {} {}", value);// , field.get(value));
    //
    // } catch (
    //
    // Exception e) {
    // log.error("AKK Failed to create a new Class {}", customTableName, e);
    // }
    //
    // }
}