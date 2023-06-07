package org.meveo.admin.async;

import java.io.Serializable;
import java.util.Iterator;

import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a one at a time access to iterator.getNext() function
 * 
 * @author Andrius Karpavicius
 *
 * @param <T> Element class
 */
public class QueueBasedIterator<T> implements Iterator<T> {

    private JMSConsumer jmsConsumer;

    /**
     * Number of miliseconds to wait to receive a first message
     */
    private long readTimeout = 20000L;

    private int nbMessagesRead = 0;

    public QueueBasedIterator() {
    }

    /**
     * Constructor
     * 
     * @param jmsConsumer JMS queue consumer
     */
    public QueueBasedIterator(JMSConsumer jmsConsumer) {
        this.jmsConsumer = jmsConsumer;
    }

    /**
     * A synchronized implementation of Iterator.next(). Will return null if no more values are available
     * 
     * @return Returns the next element, or null if no more elements are found
     */
    @SuppressWarnings("unchecked")
    @Override
    public T next() {

        if (jmsConsumer != null) {

            Message msg = jmsConsumer.receive(readTimeout);
            // Once the first message is read, change timeout to 5s
            readTimeout = 5000L;

            if (msg != null) {
                nbMessagesRead++;
                T data;
                try {
                    data = (T) msg.getBody(Serializable.class);
                    return data;
                } catch (JMSException e) {
                    Logger log = LoggerFactory.getLogger(this.getClass());
                    log.error("Failed to read JMS JOB processing message body.", e);
                }
            } else {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.debug("No more messages in a queue. Received {} messages", nbMessagesRead);
            }
            return null;

        } else {
            return null;
        }
    }

    /**
     * Do not use this method - use next() instead. This method will always return false.
     */
    public boolean hasNext() {
        return false;
    }
}