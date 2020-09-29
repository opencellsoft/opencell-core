package org.meveo.api.rest.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.ws.impl.ExceptionProcessorWs;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionProcessorWsTest {

    private ConstraintViolation constraintViolation1;
    private ConstraintViolationMock constraintViolation2;
    private ExceptionProcessorWs exceptionProcessorWs;

    @Before
    public void setUp() {
        exceptionProcessorWs = new ExceptionProcessorWs(Mockito.mock(ResourceBundle.class));
        constraintViolation1 = new ConstraintViolationMock("path1","invalid value 1", "a message 1");
        constraintViolation2 = new ConstraintViolationMock("path2","invalid value 2", "a message 2");
    }


    @Test
    public void can_process_a_constraint_violation_exception_with_one_cause() {
        String message = exceptionProcessorWs.buildErrorMessage(Set.of(constraintViolation1));
        assertThat(message).isEqualTo("Invalid values passed:     ConstraintViolationMock.path1: value 'invalid value 1' - a message 1;");
    }

    @Test
    public void can_process_many_constraints_violation_exception_with_one_cause() {
        String message = exceptionProcessorWs.buildErrorMessage(Set.of(constraintViolation1, constraintViolation2));
        assertThat(message).isEqualTo("Invalid values passed:     ConstraintViolationMock.path1: value 'invalid value 1' - a message 1;    ConstraintViolationMock.path2: value 'invalid value 2' - a message 2;");
    }
}
