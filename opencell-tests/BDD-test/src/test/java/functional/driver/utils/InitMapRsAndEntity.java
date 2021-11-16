package functional.driver.utils;

import functional.SQLite.SQLiteManagement;
import org.meveo.api.rest.IBaseRs;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.util.Set;

public class InitMapRsAndEntity {

    private static final String PATH_TO_ALL_ENTITY_RS = "org.meveo.api.rest";

    private static void persistHashMap(){
        // create a table mapping RS path and entity
        SQLiteManagement.createTableRsAndEntity();

        Reflections reflections = new Reflections( PATH_TO_ALL_ENTITY_RS );
        Set<Class<? extends IBaseRs>> classes = reflections.getSubTypesOf(IBaseRs.class);

        // insert mappings into table
        for ( Class<? extends IBaseRs> aClass : classes ) {
            if (aClass.isInterface()) {
                Annotation[] arrAnnotations = aClass.getAnnotations();
                for (Annotation anAnnotation : arrAnnotations) {
                    if (anAnnotation instanceof Path) {
                        SQLiteManagement.insertTableRsAndEntity(
                                aClass.getSimpleName().substring(0, aClass.getSimpleName().length()-2),
                                ((Path) anAnnotation).value()
                        );
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        persistHashMap();
    }

}
