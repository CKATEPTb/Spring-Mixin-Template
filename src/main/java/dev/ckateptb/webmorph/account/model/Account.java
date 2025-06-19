package dev.ckateptb.webmorph.account.model;

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
import reactor.core.publisher.Mono;

import java.util.Collection;

@RequiredArgsConstructor
//todo использовать Account вместо User в security
public class Account {
    private final User user;
    private final UserManager userManager;

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

    public void setMetadata(String key, String value) {
        this.removeMetadata(key);
        user.data().add(MetaNode.builder(key, value).build());
    }

    public void removeMetadata(String key) {
        NodeMap data = user.data();
        for (Node node : data.toCollection()) {
            if (node instanceof MetaNode meta && meta.getMetaKey().equals(key)) {
                data.remove(meta);
            }
        }
    }

    public Mono<Void> save() {
        return Mono.fromFuture(this.userManager.saveUser(user));
    }

    // todo username, password
}
