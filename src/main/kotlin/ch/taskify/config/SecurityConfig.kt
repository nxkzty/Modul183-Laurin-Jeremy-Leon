package ch.taskify.config

import ch.taskify.repository.UserRepository
import ch.taskify.service.UserDetailsServiceImpl
import ch.taskify.view.login.LoginView
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

/*
 * SecurityConfig.java  
 *
 * Creator:
 * 04.05.2026 08:30 laurin.ebnoether
 *
 * Maintainer:
 * 04.05.2026 08:30 laurin.ebnoether
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2026 ABACUS Research AG, All Rights Reserved
 */

@EnableWebSecurity
@Configuration
class SecurityConfig(private val userJpa: UserRepository) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.with(VaadinSecurityConfigurer.vaadin()) { vaadin ->
            vaadin.loginView(LoginView::class.java)
            vaadin.defaultSuccessUrl("/myTaskify")
            vaadin.anyRequest {
                it.permitAll()
            }
        }
            .logout { logout ->
                logout.logoutSuccessUrl("/login?logout")
                logout.invalidateHttpSession(true)

            }
            .userDetailsService(UserDetailsServiceImpl(userJpa))

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}