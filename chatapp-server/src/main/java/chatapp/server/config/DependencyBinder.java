package chatapp.server.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import chatapp.server.service.UserService;

import javax.inject.Singleton;

public class DependencyBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(UserService.class)
                .to(UserService.class)
                .in(Singleton.class);
    }
}
