package com.cafe.storekeeper.platform.business;

/** Only the authorization service in this package can mint a scope. */
public final class BusinessScope {
    private final Business business;
    BusinessScope(Business business) { this.business = business; }
    public String id() { return business.id(); }
    public Business business() { return business; }
}
