package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

// todo
@Getter
@Setter
public class BearerAuthenticationToken implements Authentication {
    private final String token;
    private final Long identifier;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public BearerAuthenticationToken(String token) {
        this.token = token;
        DecodedJWT decode = JWT.decode(token);
        this.identifier = decode.getClaim("sub").asLong();
        this.username = decode.getClaim("username").asString();
        this.password = decode.getClaim("password").asString();
        this.authorities = Set.of(decode.getClaim("typ").asArray(SimpleGrantedAuthority.class));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return "";
    }
}