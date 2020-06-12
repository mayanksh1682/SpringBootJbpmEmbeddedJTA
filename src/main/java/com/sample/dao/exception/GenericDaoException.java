package com.sample.dao.exception;

/**
 * GenericDaoException is thrown when any persistence interaction via Entitymanager fails.
 * 
 * @author ms99658
 */
public class GenericDaoException extends RuntimeException {

    private static final long serialVersionUID = 9196909501487285407L;

    public GenericDaoException(String message) {
        super(message);
    }

    public GenericDaoException(Throwable t) {
        super(t);
    }

    public GenericDaoException(String message, Throwable t) {
        super(message, t);
    }

}
