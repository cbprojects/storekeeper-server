package com.cafe.storekeeper.platform.identity;

public class IdentityProblem extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int status;
    public IdentityProblem(int status, String message) {
        super(message);
        this.status = status;
    }
    public int status() { return status; }
}
