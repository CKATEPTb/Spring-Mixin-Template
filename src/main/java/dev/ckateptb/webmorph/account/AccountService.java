package dev.ckateptb.webmorph.account;

import dev.ckateptb.webmorph.account.exception.AccountAlreadyExistsException;
import dev.ckateptb.webmorph.account.exception.BadCredentialsException;
import dev.ckateptb.webmorph.account.model.Account;
import dev.ckateptb.webmorph.account.repository.AccountRepository;
import dev.ckateptb.webmorph.configuration.security.bearer.BearerReactiveAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    @Delegate
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final BearerReactiveAuthenticationManager authenticationManager;

    public Mono<String> generateToken(String username, String password, boolean rememberMe) {
        return this.accountRepository.findByUsername(username)
                .flatMap(account -> {
                    if (!account.passwordMatches(password)) return Mono.empty();
                    return this.authenticationManager.generateJWT(account, rememberMe);
                })
                .switchIfEmpty(Mono.error(Throwable::new))
                .onErrorMap(throwable -> new BadCredentialsException());
    }

    public Mono<String> createAccount(String username, String password) {
        return this.accountRepository.existsByUsername(username)
                .filter(Boolean::booleanValue)
                .flatMap(exists -> Mono.error(new AccountAlreadyExistsException()))
                .switchIfEmpty(this.accountRepository.findOrCreate(UUID.randomUUID(), username.toLowerCase()))
                .cast(Account.class)
                .flatMap(account -> {
                    account.setMetadata("username", username.toLowerCase());
                    account.setMetadata("password", this.passwordEncoder.encode(password));
                    return account.save().then(Mono.defer(() -> this.authenticationManager.generateJWT(account, true)));
                });
    }
}
