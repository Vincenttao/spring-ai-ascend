---
rule_id: 80
title: "S2cCallbackSignal Historical-Only in Authority"
level: L1
view: logical
principle_ref: P-M
authority_refs: [ADR-0074, "v2.0.0-rc4 cross-constraint review P0-1"]
enforcer_refs: [E113]
status: active
kernel_cap: 8
kernel: |
  **Across active accepted ADRs, `CLAUDE.md`, `README.md`, `agent-*/ARCHITECTURE.md`, and `docs/contracts/*.v1.yaml`, the deleted Java type name `S2cCallbackSignal` MUST appear only inside paragraphs (or yaml comment blocks) that explicitly mark the reference as historical via one of the markers `historical`, `deleted`, `refactored from`, `rc3-unification`, or `amendments`. Live current-state claims using `S2cCallbackSignal` are forbidden — S2C suspension now flows through the checked `SuspendSignal.forClientCallback(...)` variant per ADR-0074 (2026-05-18 amendment).**
---

# Rule 80 — S2cCallbackSignal Historical-Only in Authority

## Motivation

The v2.0.0-rc3 cross-constraint audit (alpha-2 / beta-5, 2026-05-17) deleted the parallel unchecked `S2cCallbackSignal` `RuntimeException` subtype and unified the executor-side S2C trigger into the checked-suspension variant `SuspendSignal.forClientCallback(callbackId, envelope)`. This preserves ADR-0019's compile-time-visible-suspension doctrine as a single source of truth — there is no longer a parallel `RuntimeException` hierarchy for suspension semantics.

The 2026-05-18 rc4 cross-constraint architecture review (`docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md` finding P0-1) found that the rc3 refactor swept `CLAUDE.md`, Rule 46, `SuspendSignal.java`, `SyncOrchestrator.java`, `agent-service/ARCHITECTURE.md`, and `architecture-status.yaml#allowed_claim`, but did NOT sweep:

- `docs/adr/0074-s2c-capability-callback.yaml` (still described the unchecked design as the current ship)
- `docs/contracts/s2c-callback.v1.yaml` (still said `SyncOrchestrator` catches `S2cCallbackSignal`)
- `docs/governance/enforcers.yaml` row E82 (still described the deleted exception path)

Because ADR-0074 is `status: accepted`, a downstream engine or transport implementer reading the ADR could legitimately re-introduce the unchecked exception path, contradicting Rule 46 and the actual Java SPI. This is a direct authority conflict — not a cosmetic stale comment. ADR-0074 was amended in place on 2026-05-18 with a top-level `amendments:` block; this rule freezes that closure as a permanent invariant so the next drift wave cannot reopen it.

## Details

### Scanned files

Rule 80's gate scans the following corpus for the literal token `S2cCallbackSignal`:

- `docs/adr/*.yaml` and `docs/adr/*.md` where `status:` is one of `accepted | proposed | superseded` (any active state).
- `CLAUDE.md` (root) and `docs/CLAUDE-deferred.md`.
- `README.md` (root) and `agent-*/README.md` per-module READMEs.
- `agent-*/ARCHITECTURE.md` per-module L1 architecture documents.
- `docs/contracts/*.v1.yaml` schema contracts (including yaml comment blocks).

### Historical-marker regex

A `S2cCallbackSignal` mention is admissible only when one of the following markers appears in the same paragraph (markdown) or within a `±5` line window (yaml + multi-line markdown blocks):

```
historical | deleted | refactored from | rc3-unification | amendments
```

Markers are case-insensitive. Yaml comment blocks (`# ...`) and yaml block scalars (`|`, `>`) are scanned as text. The `amendments:` key in ADR yaml front-matter is recognised as a structural marker — anything inside its block scalar is automatically admissible.

### Failure mode

Live current-state claims like `SyncOrchestrator catches S2cCallbackSignal` or `the S2C transport throws S2cCallbackSignal` outside a historical paragraph fail the gate with a finding pointing to the violating file:line range. The remediation is to rewrite the paragraph in terms of `SuspendSignal.forClientCallback(...)` and `isClientCallback()`, optionally with a parenthetical historical note.

## Activation

Activated 2026-05-18 by the v2.0.0-rc4 cross-constraint architecture review response wave. Enforcer E113. Closes P0-1 of `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md`.

## Cross-references

- ADR-0074 — accepted ADR for the S2C capability callback protocol. The `amendments:` block (2026-05-18) records the rc3 unification and is the authoritative narrative; Rule 80 protects it against re-introduction drift.
- Rule 46 (S2C Callback Envelope + Lifecycle Bound) — substantive rule defining the SPI surface; Rule 80 is the corpus-text-truth complement that prevents Rule 46's prose from being contradicted by stale authority.
- Rule 25 (Architecture-Text Truth) — Rule 80 specialises Rule 25 to the single deleted Java identifier; Rule 25 keeps the general invariant.
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md` finding P0-1 — origin of the rule.
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review-response.en.md` — response document recording the wave that activated Rules 80-83.
- ADR-0019 — compile-time-visible-suspension doctrine that the rc3 unification protects.
