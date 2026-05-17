---
level: L1
view: logical
module: agent-runtime-core
status: active
freeze_id: null
covers_views: [logical]
spans_levels: [L1]
authority: "ADR-0079 (T2.B2 engine extraction with shared runtime-core); ADR-0078 (agent-service consolidation)"
---

# agent-runtime-core — L1 architecture (2026-05-18 T2.B2 wave)

> Owner: AgentService team | Wave: W1+ | Maturity: shipped (T2.B2 extraction)

## 1. Purpose

`agent-runtime-core` hosts the **pure-Java domain entities and SPI surfaces**
shared between `agent-service` (which implements the runtime adapters) and
`agent-execution-engine` (which owns the engine envelope + executor SPIs).

It exists to break the back-dep cycle that T2.B2 surfaced: a naive engine
extraction would create `agent-execution-engine → agent-runtime → agent-execution-engine`.
By hoisting `Run`, `RunContext`, `SuspendSignal`, `IdempotencyRecord`, and
the `Orchestrator`/`Checkpointer`/`RunRepository` SPI interfaces into a
shared core module, both sides depend downward only.

## 2. Contents

- `runs/Run.java`, `RunMode.java`, `RunStatus.java`, `RunStateMachine.java` — Run lifecycle DFA.
- `runs/RunRepository.java` — SPI interface.
- `orchestration/spi/Orchestrator.java`, `RunContext.java`, `SuspendSignal.java`, `Checkpointer.java` — orchestration SPI.
- `idempotency/IdempotencyRecord.java` — contract-spine entity.

## 3. Forbidden imports

Pure-Java only. No Spring, no Jackson, no Reactor, no middleware. Enforced by
ArchUnit `SpiPurityGeneralizedArchTest` (E48) in agent-service test scope.

## 4. Consumers

- `agent-service` (full runtime adapter + HTTP edge).
- `agent-execution-engine` (engine envelope + executor SPI).
- `spring-ai-ascend-graphmemory-starter` (transitively via agent-service).
