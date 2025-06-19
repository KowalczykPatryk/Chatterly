package chatapp.server.config;

import jakarta.inject.Inject;
import org.glassfish.hk2.api.Factory;
import chatapp.server.auth.JwtUtil;

public class JwtUtilFactory implements Factory<JwtUtil> {

    @Override
    public JwtUtil provide() {
        return new JwtUtil();
    }

    @Override
    public void dispose(JwtUtil instance) {
    }
}
