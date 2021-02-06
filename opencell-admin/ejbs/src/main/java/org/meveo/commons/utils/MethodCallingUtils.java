package org.meveo.commons.utils;

import java.util.concurrent.Callable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.jpa.JpaAmpNewTx;

@Stateless
public class MethodCallingUtils {

    /**
     * Execute runnable method in a new transaction
     * 
     * @param runnable Runnable to execute
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callMethodInNewTx(Runnable runnable) {

        runnable.run();
    }

    /**
     * Execute a callable method in a new transaction
     * 
     * @param <T> A result class
     * 
     * @param function Callable method to execute
     * @return Method return value
     * @throws Exception Execution failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T callCallableInNewTx(Callable<T> function) throws Exception {

        return function.call();
    }
}