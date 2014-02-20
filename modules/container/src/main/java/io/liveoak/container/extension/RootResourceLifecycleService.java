package io.liveoak.container.extension;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.liveoak.container.util.ConversionUtils;
import io.liveoak.spi.RequestContext;
import io.liveoak.spi.resource.RootResource;
import io.liveoak.spi.resource.async.Resource;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import java.util.function.Consumer;

/**
 * @author Bob McWhirter
 */
public class RootResourceLifecycleService implements Service<RootResource> {

    public RootResourceLifecycleService() {
    }

    @Override
    public void start(StartContext context) throws StartException {
        try {
            this.resourceInjector.getValue().start();
        } catch (Exception e) {
            throw new StartException(e);
        }
    }

    @Override
    public void stop(StopContext context) {
        this.resourceInjector.getValue().stop();
    }

    @Override
    public RootResource getValue() throws IllegalStateException, IllegalArgumentException {
        return this.resourceInjector.getValue();
    }

    public Injector<RootResource> resourceInjector() {
        return this.resourceInjector;
    }

    private InjectedValue<RootResource> resourceInjector = new InjectedValue<>();

}
