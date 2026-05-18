---
rule_id: 83
title: "Design-Only Contract Registered in Catalog"
level: L1
view: logical
principle_ref: P-D
authority_refs: [ADR-0032, ADR-0052, "v2.0.0-rc4 cross-constraint review P1-3"]
enforcer_refs: [E116]
status: active
kernel_cap: 8
kernel: |
  **Every `docs/contracts/*.v1.yaml` whose `status:` value is `design_only` OR whose `runtime_enforced:` is `false` MUST (a) be listed by file basename in `docs/contracts/contract-catalog.md`, AND (b) cite at least one `ADR-NNNN` whose file exists under `docs/adr/`. Operationalises the rc4 review P1-3 prevention: design-only contracts cannot drift unregistered, and cited ADRs cannot dangle.**
---

# Rule 83 — Design-Only Contract Registered in Catalog

## Motivation

The 2026-05-18 rc4 cross-constraint architecture review (finding P1-3 in `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md`) found that `docs/contracts/plan-projection.v1.yaml` was added to bridge ADR-0032 (scope-based run hierarchy + planner contract) and ADR-0052 (skill-topology scheduler + capability bidding), but:

- The yaml had `status: design_only` and `runtime_enforced: false`, declaring itself as a pre-implementation specification.
- The same yaml staged W2 promotion to schema-shipped AND a W2.x.1 orchestrator-consult trigger requiring Java `PlanProjection`, `PlanProjector` SPI, and `SkillResourceMatrix.admit(...)`.
- ADR-0032 (the planner ADR) said no `PlanState` or `RunPlanRef` code ships until W4, and warned the planner record shapes must not be treated as stable API before W4.
- `architecture-status.yaml` kept the full planner toolset in W4 while saying bidding-protocol Java types were deferred W2–W3.
- `docs/contracts/contract-catalog.md` did not list `plan-projection.v1.yaml` at all.

The result was a design-only contract that was both unregistered in the catalog and bound to two ADRs whose staging it appeared to contradict. A team could either overbuild the full planner early or underbuild the scheduler projection; either outcome would leave a dangling authority chain. ADR-0032 was amended in place on 2026-05-18 with a PlanProjection staging note to resolve the timing conflict; this rule guards against a future design-only contract repeating the same defect.

Rule 83 makes the prevention surface mechanical: any contract declaring itself as design-only or not-yet-runtime-enforced MUST be registered in the catalog AND cite at least one real ADR. The cited ADR existence check also prevents dangling references (e.g. `ADR-9999`).

## Details

### Algorithm

For each file matching `docs/contracts/*.v1.yaml`:

1. Parse the yaml header. Extract `status:` and `runtime_enforced:` scalar fields if present.
2. The rule applies (file is "design-only") iff `status: design_only` OR `runtime_enforced: false`. (Either signal is sufficient — they are independent dimensions.)
3. **Catalog registration** — `docs/contracts/contract-catalog.md` MUST contain the file basename (e.g. `plan-projection.v1.yaml`) as a literal substring. The match is by basename, not full path, so the catalog can use either form.
4. **ADR citation** — the yaml MUST contain at least one `ADR-NNNN` pattern (four-digit ADR reference). For every distinct `ADR-NNNN` cited, the corresponding file under `docs/adr/<NNNN>-*.yaml` OR `docs/adr/<NNNN>-*.md` MUST exist. A single dangling ADR reference fails the rule even if other references resolve.

### Excluded cases

- Contracts with `status: shipped` AND `runtime_enforced: true` (or no `runtime_enforced:` declaration where the surrounding ADR established runtime enforcement) are vacuously compliant — they sit outside Rule 83's scope and are governed instead by Rule 62 (`contract_yaml_declares_status`) for the status declaration itself.
- Contracts whose entire body is `status: deprecated` and have a `superseded_by:` link skip the catalog requirement (the catalog tracks current contracts; deprecated contracts live in a separate section per the catalog's own convention).

### Failure modes

The rule fails closed on three patterns:

1. Design-only yaml present but its basename absent from `docs/contracts/contract-catalog.md`.
2. Design-only yaml present, registered in the catalog, but cites no `ADR-NNNN` pattern at all (no authority chain).
3. Design-only yaml present, registered in the catalog, cites one or more `ADR-NNNN` patterns, but at least one cited ADR resolves to no file under `docs/adr/`.

### Why both halves matter

The catalog registration prevents a design-only contract from being added in isolation — every contributor has to update one well-known file (`contract-catalog.md`), which makes drift visible. The ADR-existence check prevents a stale or invented reference from satisfying the registration requirement on paper while leaving the authority chain broken.

## Activation

Activated 2026-05-18 by the v2.0.0-rc4 cross-constraint architecture review response wave. Enforcer E116. Closes the prevention half of P1-3 from `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md` (the substantive ADR-0032 amendment + catalog registration of `plan-projection.v1.yaml` are the closure half; Rule 83 makes the prevention permanent).

## Cross-references

- Rule 48 (Schema-First Domain Contracts) — Rule 83 specialises Rule 48's "yaml first, Java second" doctrine to the design-only phase of the contract lifecycle; Rule 48 names the staging order, Rule 83 enforces a registration + authority discipline during the pre-runtime stage.
- Rule 62 (`contract_yaml_declares_status`) — companion rule that asserts every contract yaml declares some `status:` value; Rule 83 picks up where Rule 62 ends by enforcing what `status: design_only` requires beyond mere declaration.
- ADR-0032 (Scope-Based Run Hierarchy and Planner Contract — Minimal) — origin authority cited by `plan-projection.v1.yaml`; amended 2026-05-18 with the PlanProjection staging note that resolves the W2/W4 timing ambiguity Rule 83 was authored against.
- ADR-0052 (Skill-Topology Scheduler and Capability Bidding) — the second authority bound to the projection design.
- `docs/contracts/plan-projection.v1.yaml` — the design-only contract whose unregistered state surfaced the defect family Rule 83 prevents.
- `docs/contracts/contract-catalog.md` — single registry consulted by Rule 83 part (a).
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review.en.md` finding P1-3 — origin.
- `docs/reviews/2026-05-18-l0-rc4-cross-constraint-architecture-review-response.en.md` — response document.
