package dev.ckateptb.webmorph.account;

import dev.ckateptb.webmorph.account.exception.AccountAlreadyExistsException;
import dev.ckateptb.webmorph.configuration.security.bearer.BearerReactiveAuthenticationManager;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final LuckPerms luckPerms;
    private final UserManager userManager = this.luckPerms.getUserManager();
    private final PasswordEncoder passwordEncoder;
    private final BearerReactiveAuthenticationManager authenticationManager;

    public Mono<String> tryAuthentication(String username, String password, boolean rememberMe) {
        UserManager userManager = this.luckPerms.getUserManager();
        final String lowerCaseUsername = username.toLowerCase();
        return Mono.fromFuture(userManager.lookupUniqueId(lowerCaseUsername))
                .flatMap(uuid -> Mono.fromFuture(userManager.loadUser(uuid, lowerCaseUsername)))
                .flatMap(user -> {
                    this.validatePassword(user, password);
                    return this.authenticationManager.generateJWT(user, rememberMe);
                })
                .switchIfEmpty(Mono.error(new Error()))
                .onErrorMap(throwable -> new BadCredentialsException("Invalid username or password"));
    }

    private void validatePassword(User user, String password) {
        String bcrypt = user.getCachedData().getMetaData().getMetaValue("pwd");
        if (!this.passwordEncoder.matches(password, bcrypt)) {
            throw new BadCredentialsException("Invalid password");
        }
    }

    public Mono<String> tryCreate(String username, String password) {
        UserManager userManager = this.luckPerms.getUserManager();
        final String lowerCaseUsername = username.toLowerCase();
        return Mono.fromFuture(userManager.lookupUniqueId(lowerCaseUsername))
                .flatMap(uuid -> Mono.error(new AccountAlreadyExistsException()))
                .switchIfEmpty(Mono.fromFuture(() -> userManager.loadUser(UUID.randomUUID(), lowerCaseUsername)))
                .cast(User.class)
                .flatMap(user -> {
                    UUID uniqueId = user.getUniqueId();
                    userManager.savePlayerData(uniqueId, lowerCaseUsername);
                    return this.tryUpdateUser(user, (u) -> {
                        this.removeMetadata(user, "username");
                        this.removeMetadata(user, "password");
                        NodeMap data = u.data();
                        data.add(MetaNode.builder("username", lowerCaseUsername).build());
                        data.add(MetaNode.builder("password", this.passwordEncoder.encode(password)).build());
                    }).then(Mono.defer(() -> this.authenticationManager.generateJWT(user, true)));
                });
    }

    public Mono<String> tryChangePassword(String username, String oldPassword, String newPassword) {
        return this.tryChangePassword(username, oldPassword, newPassword, true);
    }

    public Mono<String> tryChangePassword(String username, String newPassword) {
        return this.tryChangePassword(username, null, newPassword, false);
    }

    private Mono<String> tryChangePassword(String username, String oldPassword, String newPassword, boolean validate) {
        UserManager userManager = this.luckPerms.getUserManager();
        final String lowerCaseUsername = username.toLowerCase();
        return Mono.fromFuture(userManager.lookupUniqueId(lowerCaseUsername))
                .flatMap(uuid -> Mono.fromFuture(() -> userManager.loadUser(uuid, lowerCaseUsername)))
                .flatMap(user -> {
                    if (validate) this.validatePassword(user, oldPassword);
                    return this.setMetadata(user.getUniqueId(), "password", newPassword)
                            .then(Mono.defer(() -> this.authenticationManager.generateJWT(user, true)));
                });
    }

    private Mono<String> tryChangeUsername(String oldUsername, String newUsername) {
        UserManager userManager = this.luckPerms.getUserManager();
        final String lowerCaseUsername = oldUsername.toLowerCase();
        return Mono.fromFuture(userManager.lookupUniqueId(lowerCaseUsername))
                .flatMap(uuid -> Mono.fromFuture(() -> userManager.loadUser(uuid, lowerCaseUsername)))
                .flatMap(user -> {
                    UUID uniqueId = user.getUniqueId();
                    String lowerCase = newUsername.toLowerCase();
                    userManager.savePlayerData(uniqueId, lowerCase);
                    return this.setMetadata(uniqueId, "username", lowerCase)
                            .then(Mono.defer(() -> this.authenticationManager.generateJWT(user, true)));
                });
    }

    public Mono<Void> tryUpdateUser(User user, Consumer<User> consumer) {
        consumer.accept(user);
        return Mono.fromFuture(this.luckPerms.getUserManager().saveUser(user));
    }

    public Mono<Void> tryUpdateUser(UUID uuid, Consumer<User> consumer) {
        UserManager userManager = this.luckPerms.getUserManager();
        return Mono.fromFuture(userManager.modifyUser(uuid, consumer));
    }

    private void removeMetadata(User user, String key) {
        NodeMap data = user.data();
        for (Node node : data.toCollection()) {
            if (node instanceof MetaNode meta && meta.getMetaKey().equals(key)) {
                data.remove(meta);
            }
        }
    }

    public Mono<String> getMetadata(UUID uuid, String key) {
        UserManager userManager = this.luckPerms.getUserManager();
        userManager.loadUser(uuid, key);
        return this.tryUpdateUser(uuid, user -> this.removeMetadata(user, key));
    }

    public Mono<Void> setMetadata(UUID uuid, String key, String value) {
        return this.tryUpdateUser(uuid, user -> {
            this.removeMetadata(user, key);
            user.data().add(MetaNode.builder(key, value).build());
        });
    }

    public Mono<Void> removeMetadata(UUID uuid, String key) {
        return this.tryUpdateUser(uuid, user -> this.removeMetadata(user, key));
    }

    public Mono<User> getUser(UUID uuid) {
        return this.userManager.isLoaded(uuid) ?
                Mono.justOrEmpty(this.userManager.getUser(uuid)) :
                Mono.fromFuture(this.userManager.loadUser(uuid));
    }
}
