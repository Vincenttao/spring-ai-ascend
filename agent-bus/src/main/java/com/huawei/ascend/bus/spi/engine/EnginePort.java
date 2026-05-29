package com.huawei.ascend.bus.spi.engine;

/**
 * The neutral Service&#8596;Engine boundary contract: agent-service drives an engine through
 * this port; agent-execution-engine implements it. The Service owns the transport-specific
 * implementation and selects it by deployment form, so neither module depends on the other
 * beyond this contract:
 * <ul>
 *   <li>in-process direct call — co-deployed service+engine, or SDK-embedded;</li>
 *   <li>internal RPC — separate microservices you own;</li>
 *   <li>A2A — federation to external / third-party agents.</li>
 * </ul>
 *
 * <p>The current realization is IN-PROCESS: {@link #execute} runs synchronously and
 * suspension propagates as the checked {@link SuspendSignal} (caught by the Orchestrator).
 * Suspend is modelled as a capability of the port — not hard-wired to the exception — so an
 * over-the-wire realization can re-express it as a checkpoint-token protocol without changing
 * this contract.
 *
 * <p>Pure Java — no Spring imports.
 */
public interface EnginePort {

    /**
     * Execute an engine definition within the given run context. The engine type is resolved
     * from {@code def} by the implementation with strict matching; there is no fallback — an
     * unmatched definition fails the run with {@code engine_mismatch}.
     *
     * @param ctx   per-run execution context (correlation + checkpointer + suspend entry-point)
     * @param def   what to execute
     * @param input starting payload, or the resume payload on a resumed leg
     * @return the execution result
     * @throws SuspendSignal when execution suspends (in-process realization)
     */
    Object execute(RunContext ctx, ExecutorDefinition def, Object input) throws SuspendSignal;
}
