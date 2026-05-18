---
rule_id: 84
title: "Active Module ARCHITECTURE Path Truth"
level: L1
view: development
principle_ref: P-C
authority_refs: [ADR-0078, ADR-0079, "v2.0.0-rc5 post-response review P0-1"]
enforcer_refs: [E117]
status: active
kernel_cap: 8
kernel: |
  **For every `agent-*/ARCHITECTURE.md` file whose front-matter `status:` token does NOT contain `skeleton` or `deferred`, every inline path claim of the shape `<module>/src/main/java/...` MUST resolve to a real path on disk OR the surrounding paragraph (within ±3 lines) MUST carry one of the markers `historical`, `historical,`, `moved`, `extracted per ADR-NNNN`, `extracted at`, `was rooted`, `formerly`, `deferred`, `superseded`, `pre-ADR-NNNN`. Operationalises the rc5 post-response review P0-1 closure: module-level architecture path claims cannot lag behind real code locations after a refactor (the rc5 wave caught the bidirectional skeleton case via Rule 81; Rule 84 catches the active-module case Rule 81 cannot reach).**
---

# Rule 84 — Active Module ARCHITECTURE Path Truth

## Motivation

ADR-0078 (Phase C — `agent-platform` + `agent-runtime` merged into `agent-service`) and ADR-0079 (T2.B2 — engine SPI extracted to `agent-execution-engine`, shared core to `agent-runtime-core`, S2C SPI moved with it) both completed in v2.0.0-rc5 (2026-05-18). rc5's prevention wave shipped Rule 81 to keep skeleton-status modules truthful when production code arrives. That rule is one-directional in practice: it fires only when `status:` literally contains `skeleton`.

The 2026-05-18 rc5 post-response architecture review (finding P0-1 in `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md`) found the symmetric defect Rule 81 does not catch:

- `agent-service/ARCHITECTURE.md:44` declared every Java path was rooted at `agent-service/src/main/java/...`.
- `agent-service/ARCHITECTURE.md:306` listed `EngineRegistry`, `EngineEnvelope`, and `ExecutorAdapter` under `agent-service/src/main/java/.../runtime/engine/`, even though ADR-0079 had moved `EngineRegistry` + `EngineEnvelope` to `agent-execution-engine` and `ExecutorAdapter` to `ascend.springai.engine.spi.*`.
- `agent-service/ARCHITECTURE.md:321` claimed `S2cCallbackEnvelope` and `S2cCallbackTransport` lived under `agent-service/src/main/java/.../s2c/spi/`, even though ADR-0079 moved them to `agent-runtime-core`.
- `agent-service/ARCHITECTURE.md:496–498` and `:585–588` still spoke of engine extraction as future work scheduled "in the next wave."

The status of `agent-service` is `active`, not `skeleton`, so Rule 81 was vacuous. A contributor or agent starting from the module ARCHITECTURE would be told to modify the wrong module and would believe ADR-0079 had not happened.

Rule 84 makes the prevention surface mechanical: every inline path claim in an active module ARCHITECTURE.md MUST resolve to disk, OR the surrounding paragraph MUST carry a historical / deferred / moved-from marker (the same marker convention Rule 80 uses for S2C historical-only paragraphs). Stale future-tense prose adjacent to a delivered refactor fails the gate.

## Details

### Algorithm

For each `agent-*/ARCHITECTURE.md` in the reactor:

1. Parse the YAML front-matter and extract the `status:` field.
2. If `status:` contains the token `skeleton`, skip — Rule 81 already governs this case.
3. If `status:` contains the token `deferred` (whole module is paused), skip.
4. Walk the file line-by-line. For each line:
   - Regex-extract path-claim phrases matching `(agent-[a-z-]+/src/main/java/[a-zA-Z0-9_/.-]+)`.
   - For each captured path `P`:
     - If `test -e "$repo_root/$P"` succeeds, the claim resolves — accept.
     - Otherwise, scan lines `[lineno-3 .. lineno+3]` of the same file for the markers listed in the kernel. If any marker is present, accept.
     - Otherwise, fail the gate with `file:line` and the unresolved path.

### Marker convention

The accepted markers are intentionally the same family Rule 80 uses for the `S2cCallbackSignal` historical-only exemption — `historical`, `moved`, `formerly`, `extracted per ADR-NNNN`, `superseded`, `deferred`, `pre-ADR-NNNN`. A paragraph that names the ADR responsible for the move (and uses an explicit historical-tense verb) is exempt; a paragraph that merely fails to mention the ADR while quoting a stale path is not.

### Path-claim shape

The regex deliberately matches only `<module>/src/main/java/...` style paths — full reactor-root-relative paths to Java sources. This keeps the rule cheap and avoids false positives on prose like "look in `docs/contracts/`" or "see `agent-service` for...". Test-source paths (`src/test/java/`) are out of scope because moved test files do not deceive contributors the same way moved SPI does.

### Failure modes

The rule fails closed on two patterns:

1. Active module ARCHITECTURE.md cites a `<module>/src/main/java/...` path that does not exist on disk, AND no marker keyword appears within ±3 lines.
2. (Symmetric) An active module ARCHITECTURE.md describes ownership of an SPI surface that has moved to another module, but uses present-tense language without a historical marker. Detected as a special case of (1) — the moved type's old path won't resolve.

### Why both halves matter

Rule 81 catches one direction of post-refactor drift (skeleton claim but production code present). Rule 84 catches the other (active claim that points at code now living elsewhere). Together they cover the full bidirectional invariant Rule 33 (Layered 4+1 Discipline) names: module identity + module architecture + actual code tree must agree.

## Activation

Activated 2026-05-18 by the v2.0.0-rc5 post-response architecture review response wave (v2.0.0-rc6). Enforcer E117. Closes P0-1 of `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md`.

## Cross-references

- Rule 25 (Architecture-Text Truth) — Rule 84 is the module-level path specialisation of Rule 25; Rule 25 protects every prose claim that names an enforcer, Rule 84 protects every prose claim that names a Java source path inside a module ARCHITECTURE.md.
- Rule 33 (Layered 4+1 Discipline) — Rule 84 guards the truthfulness of L1 path claims so module identity stays coherent.
- Rule 80 (S2cCallbackSignal Historical-Only in Authority) — shares the marker convention (`historical`, `moved`, `extracted per ADR-NNNN`, `superseded`, `deferred`).
- Rule 81 (Skeleton Module Has No Production Java) — companion bidirectional gate; Rule 84 handles the symmetric "active module, stale path" case Rule 81 does not reach.
- ADR-0078 (Phase C consolidation) — origin of one half of the rc5 drift surface (`agent-platform` + `agent-runtime` → `agent-service`).
- ADR-0079 (Engine SPI + S2C SPI extraction) — origin of the other half (engine and S2C surfaces moved out of `agent-service`).
- `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md` finding P0-1 — origin.
- `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review-response.en.md` — response document recording this wave.
