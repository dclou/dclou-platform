package com.dripcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
access to service
curl -H "Authorization: Bearer $(curl "web-app:@localhost:9991/auth/oauth/token" -d "grant_type=password&username=reader&password=reader" | jq '.access_token' -r)" "http://localhost:9992/123"

get token
$(curl "web-app:@localhost:9991/auth/oauth/token" -d "grant_type=password&username=reader&password=reader" | jq '.access_token' -r)

w\o basic
curl -H "Authorization: Bearer $(curl "localhost:9991/auth/oauth/token" -d "grant_type=password&username=reader&password=reader&client_id=web-app" | jq '.access_token' -r)" "http://localhost:9992/123"

curl "localhost:9991/auth/oauth/token" -d "grant_type=password&username=reader&password=reader&client_id=web-app"

 */
@SpringCloudApplication
public class AuthServerApplication extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}


	@Configuration
	@Order(-20)
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {


		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http
					.formLogin().loginPage("/login").permitAll()
					.and()
					//.and().httpBasic().and() todo uncomment if basic auth needed
					.requestMatchers()
					//specify urls handled
					.antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
					.antMatchers("/fonts/**", "/js/**", "/css/**")
					.and()
					.authorizeRequests()
					.antMatchers("/fonts/**", "/js/**", "/css/**").permitAll()
					.anyRequest().authenticated();


		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication()
					.withUser("reader")
					.password("reader")
					.authorities("ROLE_READER")
					.and()
					.withUser("writer")
					.password("writer")
					.authorities("ROLE_READER", "ROLE_WRITER")
					.and()
					.withUser("guest")
					.password("guest")
					.authorities("ROLE_GUEST");
		}
	}

	@Configuration
	@EnableAuthorizationServer
	static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("authenticationManagerBean")
		AuthenticationManager authenticationManager;


		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory()
					.withClient("web-app")
					.scopes("read")
					.autoApprove(true)
					.accessTokenValiditySeconds(600)
					.refreshTokenValiditySeconds(600)
					.authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code");
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtTokenEnhancer()).authenticationManager(authenticationManager);
		}

		// todo - for disable basic auth
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients();
		}

		@Bean
		public TokenStore tokenStore() {
			return new JwtTokenStore(jwtTokenEnhancer());
		}

		@Bean
		protected JwtAccessTokenConverter jwtTokenEnhancer() {
			KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
					new ClassPathResource("jwt.jks"), "mySecretKey".toCharArray());
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
			return converter;
		}
	}


	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}
}
