package dev.ckateptb.webmorph.account.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class Account {
    @Getter
    private final User user;
    private final UserManager userManager;
    private final PasswordEncoder passwordEncoder;

    public Collection<Group> getGroups() {
        return this.user.getInheritedGroups(QueryOptions.nonContextual());
    }

    public boolean hasGroup(InheritanceNode group) {
        return this.hasPermission("group." + group.getGroupName());
    }

    public boolean addGroup(InheritanceNode group) {
        return this.user.data().add(group).wasSuccessful();
    }

    public boolean removeGroup(InheritanceNode group) {
        return this.user.data().remove(group).wasSuccessful();
    }

    public boolean hasPermission(String permission) {
        return this.user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public boolean addPermission(PermissionNode permission) {
        return this.user.data().add(permission).wasSuccessful();
    }

    public boolean removePermission(PermissionNode permission) {
        return this.user.data().remove(permission).wasSuccessful();
    }

    public String getMetadata(String key) {
        return this.user.getCachedData().getMetaData().getMetaValue(key);
    }

    public String getMetadata(String key, String defaultValue) {
        String metadata = this.getMetadata(key);
        return metadata == null ? defaultValue : metadata;
    }

    public boolean setMetadata(String key, String value) {
        this.removeMetadata(key);
        return user.data().add(MetaNode.builder(key, value).build()).wasSuccessful();
    }

    public void removeMetadata(String key) {
        NodeMap data = user.data();
        for (Node node : data.toCollection()) {
            if (node instanceof MetaNode meta && meta.getMetaKey().equals(key)) {
                data.remove(meta);
            }
        }
    }

    public boolean passwordMatches(String password) {
        return this.passwordEncoder.matches(this.getMetadata("password"), password);
    }

    public boolean setPassword(String password) {
        return this.setMetadata("password", password);
    }

    public boolean setUsername(String username) {
        return this.setMetadata("username", username);
    }

    public UUID getUuid() {
        return this.user.getUniqueId();
    }

    public String getUsername() {
        return this.getMetadata("username");
    }

    public Mono<Void> save() {
        return Mono.fromFuture(this.userManager.saveUser(user));
    }
}
