package com.huawei.ascend.service.runtime.orchestration.inmemory;

import com.huawei.ascend.engine.exec.IterativeAgentLoopExecutor;
import com.huawei.ascend.engine.exec.SequentialGraphExecutor;
import com.huawei.ascend.engine.runtime.EngineRegistry;
import com.huawei.ascend.engine.spi.EngineMatchingException;
import com.huawei.ascend.bus.spi.engine.EnginePort;
import com.huawei.ascend.bus.spi.engine.ExecutorDefinition;
import com.huawei.ascend.bus.spi.engine.RunContext;
import com.huawei.ascend.bus.spi.engine.RunMode;
import com.huawei.ascend.bus.spi.engine.SuspendSignal;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Transport-conformance kit for {@link EnginePort}. Every transport realization —
 * in-process today; internal-RPC and A2A once a live transport is wired — MUST pass
 * this identical battery so the boundary is semantically transport-agnostic: same
 * dispatch results across engine types, same {@code engine_mismatch} on an unknown
 * definition, same suspension contract surfaced to the driver. A subclass supplies
 * the port under test for exactly one transport; that is the single seam a new
 * transport plugs into.
 */
abstract class EnginePortConformanceTck {

    /** The port under test, wired to dispatch the given registry's engines. */
    protected abstract EnginePort portUnderTest(EngineRegistry registry);

    private static EngineRegistry enginesRegistered() {
        return new EngineRegistry()
                .register(new SequentialGraphExecutor())
                .register(new IterativeAgentLoopExecutor());
    }

    private static RunContext ctx() {
        return new RunContextImpl("tck-tenant", UUID.randomUUID(), new InMemoryCheckpointer());
    }

    @Test
    void dispatches_a_graph_definition() throws SuspendSignal {
        EnginePort port = portUnderTest(enginesRegistered());
        ExecutorDefinition def = new ExecutorDefinition.GraphDefinition(
                Map.of("only", (c, p) -> "GRAPH-DONE"), Map.of(), "only");

        assertThat(port.execute(ctx(), def, null)).isEqualTo("GRAPH-DONE");
    }

    @Test
    void dispatches_an_agent_loop_definition() throws SuspendSignal {
        EnginePort port = portUnderTest(enginesRegistered());
        ExecutorDefinition def = new ExecutorDefinition.AgentLoopDefinition(
                (c, p, i) -> ExecutorDefinition.ReasoningResult.done("LOOP-DONE"), 5, Map.of());

        assertThat(port.execute(ctx(), def, null)).isEqualTo("LOOP-DONE");
    }

    @Test
    void unknown_definition_fails_with_engine_mismatch() {
        EnginePort port = portUnderTest(new EngineRegistry()); // no executors registered
        ExecutorDefinition def = new ExecutorDefinition.GraphDefinition(
                Map.of("only", (c, p) -> "X"), Map.of(), "only");

        assertThatThrownBy(() -> port.execute(ctx(), def, null))
                .isInstanceOf(EngineMatchingException.class);
    }

    @Test
    void suspension_is_surfaced_to_the_driver() {
        EnginePort port = portUnderTest(enginesRegistered());
        ExecutorDefinition child = new ExecutorDefinition.GraphDefinition(
                Map.of("c", (c, p) -> "child"), Map.of(), "c");
        ExecutorDefinition def = new ExecutorDefinition.GraphDefinition(
                Map.of("s", (c, p) -> {
                    c.suspendForChild("s", RunMode.GRAPH, child, p);
                    return null; // unreachable — suspendForChild always throws
                }), Map.of(), "s");

        // In-process surfaces suspension as the checked SuspendSignal; a networked
        // realization surfaces the same suspension as an INTERRUPT_REQUEST event.
        assertThatThrownBy(() -> port.execute(ctx(), def, null))
                .isInstanceOf(SuspendSignal.class);
    }
}
