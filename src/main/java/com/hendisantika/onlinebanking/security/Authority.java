package com.hendisantika.onlinebanking.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by IntelliJ IDEA.
 * Project : online-banking
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 06/08/18
 * Time: 20.47
 * To change this template use File | Settings | File Templates.
 */
public class Authority implements GrantedAuthority {

    private final String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}