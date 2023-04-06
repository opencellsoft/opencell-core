package org.meveo.commons.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.jpa.JpaAmpNewTx;

@Stateless
public class MethodCallingUtils {

    private final static AtomicIntegerWithEquals zero = new AtomicIntegerWithEquals(0);
    private final static ConcurrentMap<Long, AtomicIntegerWithEquals> identifierToLockCounter = new ConcurrentHashMap<>();

    /**
     * Execute runnable method in a NEW transaction
     * 
     * @param runnable Runnable to execute
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callMethodInNewTx(Runnable runnable) {

        runnable.run();
    }

    /**
     * Execute runnable method in a NO transaction
     * 
     * @param runnable Runnable to execute
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void callMethodInNoTx(Runnable runnable) {

        runnable.run();
    }

    /**
     * Execute runnable method asynchronously
     * 
     * @param runnable Runnable to execute
     */
    @Asynchronous
    public void callMethodAsync(Runnable runnable) {

        runnable.run();
    }

    /**
     * Execute a callable method in a NEW transaction
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

    /**
     * Execute a callable method in a NO transaction
     * 
     * @param <T> A result class
     * 
     * @param function Callable method to execute
     * @return Method return value
     * @throws Exception Execution failure
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <T> T callCallableInNoTx(Callable<T> function) throws Exception {

        return function.call();
    }

    /**
     * Execute a callable method asynchronously
     * 
     * @param <T> A result class
     * 
     * @param function Callable method to execute
     * @throws Exception Execution failure
     */
    @Asynchronous
    public <T> void callCallableAsync(Callable<T> function) throws Exception {

        function.call();
    }

    /**
     * Execute a method with a lock for a given value
     * 
     * @param lockId Value to lock for
     * @param runnable A runnable method to execute once lock is obtained
     */
    public static void executeMethodLocked(Long lockId, Runnable runnable) {
        AtomicIntegerWithEquals counterAndLock = identifierToLockCounter.compute(lockId, (key, existing) -> {
            if (existing == null) {
                return new AtomicIntegerWithEquals(1);
            }
            existing.atomicValue.incrementAndGet();
            return existing;
        });

        synchronized (counterAndLock) {
            try {
                runnable.run();
            } finally {
                counterAndLock.atomicValue.decrementAndGet();
                identifierToLockCounter.remove(lockId, zero);
            }
        }
    }

    /**
     * Execute a method with a lock for a given value
     * 
     * @param lockId Value to lock for
     * @param runnable A runnable method to execute once lock is obtained
     */
    public static <T> T executeFunctionLocked(Long lockId, Callable<T> function) throws Exception {
        AtomicIntegerWithEquals counterAndLock = identifierToLockCounter.compute(lockId, (key, existing) -> {
            if (existing == null) {
                return new AtomicIntegerWithEquals(1);
            }
            existing.atomicValue.incrementAndGet();
            return existing;
        });

        synchronized (counterAndLock) {
            try {
                return function.call();
            } finally {
                counterAndLock.atomicValue.decrementAndGet();
                identifierToLockCounter.remove(lockId, zero);
            }
        }
    }

    // AtomicInteger does not implement equals() properly so there is a need for such wrapper
    private static class AtomicIntegerWithEquals {

        private final AtomicInteger atomicValue;

        AtomicIntegerWithEquals(int value) {
            this.atomicValue = new AtomicInteger(value);
        }

        // Used internally by remove()
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AtomicIntegerWithEquals)) {
                return false;
            }
            return atomicValue.get() == ((AtomicIntegerWithEquals) o).atomicValue.get();
        }

        // Not really used, but when implementing custom equals() it is a good practice to implement also hashCode()
        @Override
        public int hashCode() {
            return atomicValue.get();
        }
    }
}