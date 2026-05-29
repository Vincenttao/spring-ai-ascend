package com.huawei.ascend.service.runtime.orchestration;

import com.huawei.ascend.bus.spi.engine.EnginePort;
import com.huawei.ascend.bus.spi.engine.ExecutorDefinition;
import com.huawei.ascend.bus.spi.engine.RunContext;
import com.huawei.ascend.bus.spi.engine.SuspendSignal;

/**
 * A2A federation realization of {@link EnginePort} — drives an external / third-party
 * agent as an engine via the agent-bus {@code FederationGateway} (Mode B). Used when
 * the "engine" is an agent outside this platform's trust boundary. The A2A envelope
 * maps a suspension to {@code input_required} per {@code docs/contracts/a2a-envelope.v1.yaml};
 * the neutral wire shape is {@code docs/contracts/engine-port.v1.yaml}.
 *
 * <p>External agents are outside M6 jurisdiction: only the outbound A2A envelope is
 * audited, and the attested ancestor chain travels with cross-instance spawns.
 *
 * <p>design_only — the federation broker (Kafka / NATS / in-house) is a deferred ADR
 * and no live transport is wired. {@link #execute} fails fast so federation cannot
 * silently degrade to a no-op.
 */
public final class A2aEngineAdapter implements EnginePort {

    @Override
    public Object execute(RunContext ctx, ExecutorDefinition def, Object input) throws SuspendSignal {
        throw new UnsupportedOperationException(
                "engine_a2a_transport_not_wired: the A2A federation EnginePort is design_only "
                        + "(see docs/contracts/engine-port.v1.yaml + a2a-envelope.v1.yaml). The federation "
                        + "broker is a deferred deployment concern.");
    }
}
