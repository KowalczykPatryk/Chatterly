package chatapp.server.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import chatapp.server.service.UserService;
import chatapp.server.service.FriendService;

import jakarta.inject.Singleton;

public class DependencyBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(UserService.class)
                .to(UserService.class)
                .in(Singleton.class);
        bind(FriendService.class)
                .to(FriendService.class)
                .in(Singleton.class);
    }
}
