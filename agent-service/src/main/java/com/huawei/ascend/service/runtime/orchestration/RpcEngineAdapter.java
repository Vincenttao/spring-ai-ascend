package com.huawei.ascend.service.runtime.orchestration;

import com.huawei.ascend.bus.spi.engine.EnginePort;
import com.huawei.ascend.bus.spi.engine.ExecutorDefinition;
import com.huawei.ascend.bus.spi.engine.RunContext;
import com.huawei.ascend.bus.spi.engine.SuspendSignal;

/**
 * Internal-RPC realization of {@link EnginePort} — deployment Form 1, where the
 * Service and engine run as separate microservices you own. The Service sends a
 * {@link com.huawei.ascend.bus.spi.engine.DefinitionRef} (capability name) plus the
 * input and opaque correlation; the remote engine resolves the name against its own
 * registry, streams events back, and surfaces suspension as an INTERRUPT_REQUEST
 * checkpoint token resumed by a fresh execute. The wire shape both ends mirror is
 * {@code docs/contracts/engine-port.v1.yaml}.
 *
 * <p>This adapter is the Service-side "engine adapter layer": selecting it (instead
 * of the in-process port) is how a deployment chooses Form 1 without either module
 * depending on the other beyond the neutral contract.
 *
 * <p>design_only — no live transport is wired; the gRPC/REST stack is a separate
 * deployment concern. {@link #execute} fails fast so a misconfigured deployment
 * cannot silently degrade to a no-op: co-deploy the engine in-process, or provision
 * an RPC server, before selecting Form 1.
 */
public final class RpcEngineAdapter implements EnginePort {

    @Override
    public Object execute(RunContext ctx, ExecutorDefinition def, Object input) throws SuspendSignal {
        throw new UnsupportedOperationException(
                "engine_rpc_transport_not_wired: the internal-RPC EnginePort is design_only "
                        + "(see docs/contracts/engine-port.v1.yaml). Co-deploy the engine in-process "
                        + "or provision an RPC server before selecting deployment Form 1.");
    }
}
