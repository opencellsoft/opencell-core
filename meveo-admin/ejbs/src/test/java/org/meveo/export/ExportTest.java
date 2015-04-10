package org.meveo.export;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

public class ExportTest {

    @Test
    public void testExport() throws ClassNotFoundException, IOException {

        // List<Class<?>> clazzes = find("org.meveo.model");
        // clazzes.addAll(find("org.meveo.model.admin"));
        // clazzes.addAll(find("org.meveo.model.bi"));
        // clazzes.addAll(find("org.meveo.model.billing"));
        // clazzes.addAll(find("org.meveo.model.catalog"));
        // clazzes.addAll(find("org.meveo.model.communication"));
        // clazzes.addAll(find("org.meveo.model.communication.contact"));
        // clazzes.addAll(find("org.meveo.model.communication.email"));
        // clazzes.addAll(find("org.meveo.model.communication.postalmail"));
        // clazzes.addAll(find("org.meveo.model.crm"));
        // clazzes.addAll(find("org.meveo.model.datawarehouse"));
        // clazzes.addAll(find("org.meveo.model.jobs"));
        // clazzes.addAll(find("org.meveo.model.mediation"));
        // clazzes.addAll(find("org.meveo.model.notification"));
        // clazzes.addAll(find("org.meveo.model.payments"));
        // clazzes.addAll(find("org.meveo.model.rating"));
        // clazzes.addAll(find("org.meveo.model.security"));
        // clazzes.addAll(find("org.meveo.model.shared"));
        // clazzes.addAll(find("org.meveocrm.model.dwh"));

        List<Class> clazzes = ReflectionUtils.getClasses("org.meveo.model");

        for (Class clazz : clazzes) {

            if (!IEntity.class.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            if (clazz.isAnnotationPresent(ExportIdentifier.class)) {
                continue;
            }
            System.out.println("AKK classes with no anotation are " + clazz.getName());

        }

    }

 
}