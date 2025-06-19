package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class BearerAuthenticationToken implements Authentication {
    private final User principal;
    private final DecodedJWT credentials;
    private boolean authenticated;

    public BearerAuthenticationToken(User principal, DecodedJWT credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
         return Collections.emptyList();
//        return this.principal.getCachedData().getPermissionData().getPermissionMap().entrySet().stream()
//                .filter(Map.Entry::getValue)
//                .map(Map.Entry::getKey)
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public CachedMetaData getDetails() {
        return this.principal.getCachedData().getMetaData();
    }

    @Override
    public String getName() {
        return this.principal.getUsername();
    }
}