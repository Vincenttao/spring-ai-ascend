package com.huawei.ascend.engine.runtime;

import com.huawei.ascend.bus.spi.engine.EnginePort;
import com.huawei.ascend.bus.spi.engine.ExecutorDefinition;
import com.huawei.ascend.bus.spi.engine.RunContext;
import com.huawei.ascend.bus.spi.engine.SuspendSignal;

import java.util.Objects;

/**
 * In-process realization of {@link EnginePort}: the engine exposes itself as a port by
 * delegating to its internal {@link EngineRegistry} strict dispatch. This is the realization
 * used when the Service and engine share a JVM (co-deployed, or SDK-embedded) — the boundary
 * is a direct call. A networked deployment wraps this server-side behind an RPC/A2A transport.
 *
 * <p>The engine-internal {@code EngineRegistry} (engineType&#8594;ExecutorAdapter dispatch,
 * hook surface, S2C transport) stays internal; this class is its northbound projection as the
 * neutral port the Service drives.
 */
public final class InProcessEnginePort implements EnginePort {

    private final EngineRegistry registry;

    public InProcessEnginePort(EngineRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry is required");
    }

    @Override
    public Object execute(RunContext ctx, ExecutorDefinition def, Object input) throws SuspendSignal {
        return registry.resolveByPayload(def).execute(ctx, def, input);
    }
}
