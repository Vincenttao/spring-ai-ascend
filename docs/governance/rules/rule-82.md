---
rule_id: 82
title: "Baseline Metrics Single Source"
level: L1
view: logical
principle_ref: P-D
authority_refs: ["v2.0.0-rc4 cross-constraint review P1-1"]
enforcer_refs: [E115]
status: active
kernel_cap: 8
kernel: |
  **`docs/governance/architecture-status.yaml#architecture_sync_gate.baseline_metrics` MUST exist with required keys `active_engineering_rules`, `active_gate_checks`, `gate_executable_test_cases`, `enforcer_rows`, `architecture_graph_nodes`, `architecture_graph_edges`. Numeric baseline claims in `README.md` and `gate/README.md` MUST point to this structured block (substring `architecture_sync_gate.baseline_metrics` present). Operationalises the rc4 review P1-1 closure: entrypoint counts have one source.**
---

# Rule 82 — Baseline Metrics Single Source

## Motivation

Across the v2.0.0 release waves the repository accumulated four parallel ledgers for the same set of baseline counts (active engineering rules, active gate rules, self-tests, enforcer rows, graph nodes, graph edges, Maven tests):

- `README.md` — release-front-of-house counts.
- `AGENTS.md` — historical paragraph that was supposed to stop carrying counts.
- `gate/README.md` — gate's own description of its size.
- `docs/governance/architecture-status.yaml` — structured ledger.

The 2026-05-18 rc4 cross-constraint architecture review (finding P1-1 in `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md`) found six contradictions in the same release:

- `README.md:15` claimed 35 engineering rules / 64 gate rules / 94 self-tests / 94 enforcer rows / 306 Maven tests.
- `README.md:111` still said CLAUDE.md had 34 active engineering rules.
- `AGENTS.md:21` still carried "34 active engineering rules".
- `docs/governance/architecture-status.yaml:89` advertised rc4 counts for rules and tests but still said the architecture graph was `249 graph nodes / 326 edges`.
- `docs/governance/architecture-graph.yaml:21-22` and the build output both said `315 nodes / 433 edges`.
- `gate/README.md` contradicted itself across lines 3 / 18-20 / 51 / 68 with three different self-test totals (98, 92, 121).

Worse, the canonical gate passed, which meant the gate had a blind spot precisely in the entrypoint-vocabulary surface. The fix is structural: name one place as the single source, and require every prose count to point at it instead of restating the number.

## Details

### Required structured block

`docs/governance/architecture-status.yaml` MUST carry a block:

```
architecture_sync_gate:
  baseline_metrics:
    active_engineering_rules: <int>
    active_gate_checks: <int>
    gate_executable_test_cases: <int>
    enforcer_rows: <int>
    architecture_graph_nodes: <int>
    architecture_graph_edges: <int>
```

All six keys are required. Additional sibling keys (e.g. `release_baseline_self_tests`, `maven_tests`) are permitted but not enforced by Rule 82.

### Vocabulary discipline

The keys are deliberately separated so prose cannot conflate them:

- `active_engineering_rules` — `#### Rule NN` headings in `CLAUDE.md` (Layer-1 engineering rules).
- `active_gate_checks` — the numbered gate-script rules in `gate/check_architecture_sync.sh` (the small set referenced by `gate/README.md`).
- `gate_executable_test_cases` — the `TOTAL=...` declared by `gate/test_architecture_sync_gate.sh` (the self-test corpus that proves each gate rule's algorithm against inputs).
- `enforcer_rows` — row count of `docs/governance/enforcers.yaml` (the rule→test bridge).
- `architecture_graph_nodes` / `architecture_graph_edges` — graph build outputs.

The reviewer noted the existing vocabulary collision around "active gate rules" — Rule 82 enforces that the structured block names them distinctly and that entrypoint prose uses the same names.

### Pointer requirement

`README.md` and `gate/README.md` MUST contain at least one occurrence of the substring `architecture_sync_gate.baseline_metrics` (or `architecture_sync_gate.baseline_metrics.<key>` for a specific metric). This is the minimum link integrity check — it does not enforce that the numbers actually agree (that is the substantive work of the release process), only that the prose acknowledges the canonical source.

### Drift mode

A future numeric claim in `README.md` or `gate/README.md` that does NOT cite the structured block will fail the gate. Restating the number inline without the pointer is forbidden; pointing at the block and then quoting the number is fine.

## Activation

Activated 2026-05-18 by the v2.0.0-rc4 cross-constraint architecture review response wave. Enforcer E115. Closes P1-1 of `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md`.

## Cross-references

- Rule 25 (Architecture-Text Truth) — Rule 82 is the count-vocabulary specialisation of Rule 25; Rule 25 protects every prose claim that names an enforcer, Rule 82 protects every prose claim that names a baseline count.
- Rule 27 (Active Entrypoint Baseline Truth, deferred but referenced) — gate-rule complement that asserts README baseline counts match `allowed_claim`. Rule 82 strengthens that surface by enforcing the structured-source form: Rule 27 says "the number is right", Rule 82 says "the number must be pulled from one block".
- ADR-0047 (Active Entrypoint Truth and System Boundary Prose Convention) — origin of the entrypoint-baseline-truth invariant that Rule 82 operationalises.
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md` finding P1-1 — origin.
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review-response.en.md` — response document.
