## Problem Statement

The repository structure places shared API build assets under a nested `api` directory, which creates friction in build scripts, Docker contexts, and CI workflows that consume these assets from multiple services. We need to extract these shared assets one level higher and standardize naming so the platform remains easy to reason about and the full system compiles reliably.

From the user perspective, the required target state is:
- current `api/client-java` becomes top-level `lib-api-java`
- current `api/schema` becomes top-level `schema`
- all dependent projects and GitHub Actions continue to build and run without manual path fixes

## Solution

Introduce a coordinated repository-wide path migration that moves the shared API client build project and schema definitions to top-level directories, then updates every consumer path in Maven configuration, Docker build definitions, and GitHub Actions workflows.

The migration will be implemented as a compatibility-safe, deterministic refactor where:
- all compile-time path references are updated in one cohesive change set
- CI triggers and cache keys are aligned with new directory paths
- Docker-based local/e2e build flows resolve the new paths consistently
- verification gates ensure Java builds and smoke-test build paths still pass

## User Stories

1. As a platform maintainer, I want shared API assets moved to clearer top-level names, so that repository navigation is more intuitive.
2. As a backend developer, I want Java module build paths to reference stable top-level locations, so that local builds do not fail after folder moves.
3. As a CI maintainer, I want workflow working directories updated to the new structure, so that pipeline jobs keep executing without manual intervention.
4. As a CI maintainer, I want workflow path filters updated, so that relevant changes still trigger the correct workflows.
5. As a CI maintainer, I want Docker cache key inputs updated to new schema/client locations, so that cache invalidation remains correct.
6. As a service developer, I want Dockerfiles for services to copy dependencies from the new folders, so that image builds continue to resolve shared artifacts.
7. As a service developer, I want service Dockerfiles to build the shared Java client from its new location, so that dependent services can still compile.
8. As a Java developer, I want generated-code source directories to remain unaffected functionally, so that protobuf/avro generation still succeeds.
9. As a Java developer, I want proto and avro source directory properties updated to new relative paths, so that code generation does not break.
10. As a release engineer, I want cyclic build jobs to reference new paths, so that scheduled integrity checks remain meaningful.
11. As a release engineer, I want e2e smoke workflow references updated, so that end-to-end stack builds stay reproducible.
12. As a developer running local k3d stack, I want Docker build contexts to work with the renamed folders, so that local integration remains one-command.
13. As a repository contributor, I want workflow naming and documentation to match the new folder names, so that maintenance overhead is lower.
14. As a maintainer, I want no hidden dependency on the old `api/client-java` path, so that future cleanup does not break unexpectedly.
15. As a maintainer, I want no hidden dependency on the old `api/schema` path, so that schema evolution can proceed from the new root location.
16. As a quality owner, I want migration validation to cover both direct Maven builds and Docker-based builds, so that regressions are caught early.
17. As a quality owner, I want regression checks around artifact publishing workflow, so that package publication remains intact.
18. As a developer, I want a clear failure mode if a stale path remains, so that fixing any missed reference is quick.
19. As a project lead, I want this migration to be done in one coherent change, so that no partial repository state exists on main.
20. As a reviewer, I want implementation decisions documented up front, so that code review focuses on correctness rather than intent discovery.
21. As a consumer of shared API packages, I want artifact coordinates to remain stable unless explicitly changed, so that downstream usage is not disrupted.
22. As a contributor, I want migration scope clearly defined, so that unrelated refactors do not get mixed into this change.
23. As a DevEx owner, I want future onboarding docs to point to top-level shared directories, so that new developers find core assets quickly.
24. As an operations engineer, I want smoke and cyclic workflows to remain green after migration, so that deployment confidence is preserved.
25. As a team, we want all required builds to compile after path migration, so that feature work can continue uninterrupted.

## Implementation Decisions

- Shared directory rename/move decisions:
  - Move shared Java client build project from nested path to top-level `lib-api-java`.
  - Move schema directory from nested path to top-level `schema`.
- Keep Maven artifact identities unchanged unless required by a separate explicit decision; this migration is primarily physical path relocation.
- Update all path-based Maven references in shared client submodules for proto/avro source location resolution.
- Update all Dockerfiles that copy/build shared client and schema assets so they consume `lib-api-java` and `schema`.
- Update all GitHub workflow job working directories, path filters, and cache key hash inputs that currently point to old directories.
- Update CI workflow labels/names where needed to avoid misleading references to obsolete paths.
- Maintain existing build order dependency:
  - shared Java client build first
  - service builds second
  - frontend/e2e stack build remains unchanged functionally but references updated paths
- Perform migration as an atomic change set to avoid transient broken references on default branch.
- Avoid unrelated refactors and avoid changing runtime behavior; this is a build-system and repository-structure migration.

Proposed deep modules for implementation:
- Repository Path Migration Map module:
  - Single source of truth mapping old paths to new paths, used by scripts/config updates.
  - Deep value: reduces scattered hardcoded path edits and future rename risk.
- Build Reference Adapter module:
  - Encapsulates path consumption in build tooling (Maven/Docker/CI) with a minimal interface.
  - Deep value: centralizes path policy while leaving service logic untouched.
- Migration Verification module:
  - A focused validation matrix for compile checks and workflow-sensitive path checks.
  - Deep value: codifies correctness criteria independent of implementation detail.

## Testing Decisions

- Good tests for this migration assert external behavior only:
  - builds succeed
  - code generation succeeds
  - Docker images build
  - workflows trigger and run against expected paths
  - no assertions about internal implementation ordering beyond contract requirements
- Modules and surfaces to test:
  - Shared Java client build after relocation
  - Shared schema-driven code generation in client submodules
  - Service Docker builds that import shared client/schema
  - CI workflows that reference working directories and hashFiles path globs
  - Cyclic build and e2e smoke workflow path-dependent steps
- Prior art in codebase:
  - Existing cyclic build workflow validating Java component builds
  - Existing e2e smoke workflow with Docker build cache keys including shared API/schema paths
  - Existing service Dockerfiles that currently copy and build shared client/schema before service compilation
- Recommended validation sequence:
  - compile shared Java client from new location
  - compile timeentries and webapi with existing versioning patterns
  - run Docker builds for both services using updated copy/build paths
  - execute CI workflow dry-run validation (or PR run) to verify path filters and cache inputs

## Out of Scope

- Changing domain logic in services or frontend.
- Altering gRPC/Avro schema contents.
- Changing package coordinates, group IDs, or artifact IDs unless a separate decision mandates it.
- Refactoring unrelated workflows not impacted by old path references.
- Infrastructure redesign beyond path updates required for successful build and CI operation.

## Further Notes

- This migration is path-centric and should be merged with strong compile verification to prevent hidden stale references.
- Generated output directories may still contain historical absolute paths during local development; those are non-source artifacts and should not define migration success criteria.
- Documentation updates should align with new top-level directory naming to keep repository guidance accurate.
