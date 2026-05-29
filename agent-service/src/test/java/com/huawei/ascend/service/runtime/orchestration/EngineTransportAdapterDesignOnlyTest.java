package com.huawei.ascend.service.runtime.orchestration;

import com.huawei.ascend.bus.spi.engine.EnginePort;
import com.huawei.ascend.bus.spi.engine.ExecutorDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The networked {@link EnginePort} realizations are design_only: until a live
 * transport is wired they MUST fail fast rather than silently return a no-op, so a
 * deployment that selects Form 1 / federation without provisioning the transport
 * surfaces the misconfiguration immediately. When a real transport lands, these
 * adapters graduate to {@code EnginePortConformanceTck} subclasses.
 */
class EngineTransportAdapterDesignOnlyTest {

    private static final ExecutorDefinition DEF = new ExecutorDefinition.GraphDefinition(
            Map.of("n", (c, p) -> "x"), Map.of(), "n");

    @Test
    void rpc_adapter_fails_fast_until_wired() {
        EnginePort port = new RpcEngineAdapter();
        assertThatThrownBy(() -> port.execute(null, DEF, null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("engine_rpc_transport_not_wired");
    }

    @Test
    void a2a_adapter_fails_fast_until_wired() {
        EnginePort port = new A2aEngineAdapter();
        assertThatThrownBy(() -> port.execute(null, DEF, null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("engine_a2a_transport_not_wired");
    }
}
