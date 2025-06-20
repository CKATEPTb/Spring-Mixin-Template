package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ckateptb.webmorph.account.model.Account;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Setter
public class BearerAuthenticationToken implements Authentication {
    private final Account principal;
    private final DecodedJWT credentials;
    private boolean authenticated;

    public BearerAuthenticationToken(Account principal, DecodedJWT credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.principal.getGroups()
                .stream()
                .map(group -> new SimpleGrantedAuthority(group.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<MetaNode> getDetails() {
        return this.principal.getUser().getNodes(NodeType.META);
    }

    @Override
    public String getName() {
        return this.principal.getUsername();
    }
}