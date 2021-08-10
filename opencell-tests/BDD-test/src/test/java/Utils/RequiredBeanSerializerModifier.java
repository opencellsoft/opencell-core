package Utils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;

public class RequiredBeanSerializerModifier extends BeanSerializerModifier {

    /**
     * Method called by {@link BeanSerializerFactory} with tentative set
     * of discovered properties.
     * Implementations can add, remove or replace any of passed properties.
     *
     * Properties <code>List</code> passed as argument is modifiable, and returned List must
     * likewise be modifiable as it may be passed to multiple registered
     * modifiers.
     */
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        return beanProperties.stream().filter(property ->{
            AnnotationMap annotations = property.getMember().getAllAnnotations();
            XmlElement annotation = annotations.get(XmlElement.class);
            XmlAttribute attribute = annotations.get(XmlAttribute.class);
            return (annotation != null && annotation.required()) || (attribute != null && attribute.required());
        }).collect(Collectors.toList());
    }

}
