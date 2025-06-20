package dev.ckateptb.webmorph.account.repository;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.ckateptb.webmorph.account.model.Account;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.UserManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountRepository {
    private final LuckPerms luckPerms;
    private final PasswordEncoder passwordEncoder;
    private final AsyncCache<UUID, Account> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .buildAsync();

    public Mono<Boolean> existsByUsername(String username) {
        final String lowerCase = username.toLowerCase();
        return Mono.fromFuture(this.luckPerms.getUserManager().lookupUniqueId(lowerCase))
                .flatMap(uuid -> Mono.just(true)
                        .contextWrite(context -> context.put("uuid", uuid)))
                .switchIfEmpty(Mono.just(false))
                .contextWrite(context -> context.put("username", lowerCase));
    }

    public Mono<Boolean> existsByUuid(UUID uuid) {
        return Mono.fromFuture(this.luckPerms.getUserManager().lookupUsername(uuid))
                .flatMap(username -> Mono.just(true)
                        .contextWrite(context -> context.put("username", username)))
                .switchIfEmpty(Mono.just(false))
                .contextWrite(context -> context.put("uuid", uuid));
    }

    public Mono<Account> findByUuid(UUID uuid) {
        return this.existsByUuid(uuid)
                .filter(Boolean::booleanValue)
                .transformDeferredContextual((ignored, context) ->
                        Mono.just(context.get("username")).cast(String.class))
                .flatMap(username -> this.findOrCreate(uuid, username));
    }

    public Mono<Account> findByUsername(String username) {
        return this.existsByUsername(username)
                .filter(Boolean::booleanValue)
                .transformDeferredContextual((ignored, context) ->
                        Mono.just(context.get("uuid")).cast(UUID.class))
                .flatMap(uuid -> this.findOrCreate(uuid, username));
    }

    public Mono<Account> findOrCreate(UUID uuid, String username) {
        UserManager userManager = this.luckPerms.getUserManager();
        final String lowerCaseUsername = username.toLowerCase();
        return Mono.fromFuture(this.cache.get(uuid, (key, ignored) ->
                Mono.fromFuture(userManager.loadUser(uuid, lowerCaseUsername))
                        .map(user -> new Account(user, userManager, this.passwordEncoder))
                        .toFuture()));
    }
}
