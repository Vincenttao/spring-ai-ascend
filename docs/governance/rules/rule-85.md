---
rule_id: 85
title: "Catalog SPI Row Matches Module SPI Metadata"
level: L1
view: development
principle_ref: P-D
authority_refs: [ADR-0030, ADR-0070, ADR-0080, "v2.0.0-rc5 post-response review P1-2"]
enforcer_refs: [E118]
status: active
kernel_cap: 8
kernel: |
  **Every row in `docs/contracts/contract-catalog.md` §2 "Active SPI interfaces" table whose `Status` column does NOT contain the token `(internal)` MUST have its `Module` column value resolve to a module whose `module-metadata.yaml#spi_packages:` list contains the row's `Package` column value (exact entry OR a `.spi.`-prefix entry that contains the row's package as a sub-package), AND the same module's `docs/dfx/<module>.yaml#spi_packages:` list MUST contain the same package. Operationalises rc5 post-response review P1-2 closure: a catalog row that claims SPI status MUST be backed by SPI metadata; the alternative is an explicit `(internal)` mark.**
---

# Rule 85 — Catalog SPI Row Matches Module SPI Metadata

## Motivation

The 2026-05-18 rc5 post-response architecture review (finding P1-2 in `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md`) found `ResilienceContract` simultaneously treated as a shipped SPI and excluded from SPI governance:

- `docs/contracts/contract-catalog.md:22-31` listed `ResilienceContract` in "Active SPI interfaces (11 total)" with package `ascend.springai.service.runtime.resilience` — a package with no `.spi` token.
- `docs/contracts/contract-catalog.md:43` counted `agent-service` as having two SPI interfaces (`GraphMemoryRepository`, `ResilienceContract`).
- `docs/governance/architecture-status.yaml#resilience_contract.allowed_claim` called it an "L1: ResilienceContract SPI".
- `agent-service/ARCHITECTURE.md` resilience section called it an "Operation-routing SPI (W0)".
- `agent-service/module-metadata.yaml:13-14` declared only `ascend.springai.service.runtime.memory.spi` under `spi_packages` — `resilience` was missing.
- `docs/dfx/agent-service.yaml:14-15` mirrored only `memory.spi`.

Rule 77 (every `spi_packages:` entry must end in `.spi` or contain `.spi.`) therefore passed vacuously — `ResilienceContract` was *called* SPI in the catalog but its package was never declared as SPI in the metadata, so the package convention check had no rows to test. The corpus advertised one classification while metadata declared another, and no gate caught the divergence.

The rc4 response's hidden-defects audit had logged this as a known edge case ("the one shipped SPI not under a .spi package — out of scope for this wave, but logged for future audit"). The rc5 reviewer escalated it: a contract catalog row that calls something a shipped SPI IS a published-SPI commitment; the package home must match.

The rc6 wave closes the substantive half via ADR-0080 (move `ResilienceContract` + value types to `...resilience.spi.*`) and adds Rule 85 to prevent the dual-classification defect from recurring on any future SPI.

## Details

### Algorithm

For each row in the SPI table of `docs/contracts/contract-catalog.md` §2 (between the header `**Active SPI interfaces (N total):**` and the next bold-heading separator):

1. Skip header rows and table separators (`|---|`).
2. Parse the four columns: `Interface`, `Module`, `Package`, `Status`.
3. If `Status` contains the literal substring `(internal)` (case-insensitive), the row is exempt: it MUST NOT be counted in the `(N total)` header, but it MAY exist in the table as historical context.
4. Otherwise, the row is a shipped-SPI commitment. Resolve `<module>/module-metadata.yaml`:
   - Fail if the metadata file does not exist.
   - Fail if `spi_packages:` is absent.
   - Fail if `Package` is not listed in `spi_packages:` as either an exact match OR a parent package (the catalog row's package contains the metadata entry as a sub-package — e.g., row package `ascend.springai.service.runtime.resilience.spi` matches metadata entry `ascend.springai.service.runtime.resilience.spi`).
5. Resolve `docs/dfx/<module>.yaml`:
   - Fail if the DFX file does not exist or has no top-level `spi_packages:` block.
   - Fail if the same package is not listed there. (Rule 78 set-match already enforces metadata ↔ DFX agreement; Rule 85 inherits that property by requiring the package in both files.)

### Header-count consistency

The catalog's `**Active SPI interfaces (N total):**` header MUST equal the number of non-`(internal)` rows in the table. If a row is exempted via `(internal)`, the header MUST be decremented. This prevents the `(11 total)` count from silently shadowing a hidden-non-SPI row.

### Excluded cases

- Rows in deprecated-SPI subtables or appendices below a separator marker like `**Deprecated SPI:**`. These are not "active" and Rule 85 does not apply.
- `package-info.java`-only SPI scaffolds — covered by Rule 75's placeholder waiver, not by Rule 85.

### Why both files

Rule 78 already enforces that `module-metadata.yaml#spi_packages` and `docs/dfx/<module>.yaml#spi_packages` set-match. Rule 85 piles on a third constraint — the contract catalog must also point at the same set — closing the triangle. Without Rule 85, the catalog could drift from metadata silently (the rc5 defect); with Rule 85, all three artefacts must agree.

## Activation

Activated 2026-05-18 by the v2.0.0-rc5 post-response architecture review response wave (v2.0.0-rc6). Enforcer E118. Closes P1-2 of `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md`.

## Cross-references

- Rule 32 (SPI + DFX + TCK Co-Design) — origin authority for the `.spi` package convention and DFX requirement. Rule 85 enforces the catalog as the third corner of the SPI-truth triangle.
- Rule 75 (SPI Packages Populated) — each metadata SPI package must have real Java content (or an ADR-waived placeholder).
- Rule 76 (No Split SPI Packages) — no two modules can co-declare the same SPI package.
- Rule 77 (SPI Packages Dot-Spi Convention) — every metadata SPI package must end in `.spi` or contain `.spi.`.
- Rule 78 (DFX SPI Packages Match Module Metadata) — metadata ↔ DFX set-match; Rule 85 extends this to catalog ↔ metadata.
- ADR-0030 (Skill-capacity arbitration) — original authority that published `ResilienceContract` as the architectural boundary.
- ADR-0070 (Tenant-aware `resolve(tenant, skill)` two-arg signature) — Rule 41.b's signature evolution that confirmed cross-module SPI status.
- ADR-0080 (ResilienceContract `.spi` package alignment) — substantive closure of the rc5 P1-2 defect; Rule 85 makes the prevention permanent.
- `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review.en.md` finding P1-2 — origin.
- `docs/reviews/2026-05-18-l0-rc5-post-response-architecture-review-response.en.md` — response document recording this wave.
