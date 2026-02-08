Placeholder for Application Insights configuration files.

**Current approach (as of 2026-02):**
- Application Insights agent is NO LONGER bundled in the Docker image
- Instead, an init container downloads the agent at runtime to an ephemeral volume
- The main container mounts the same volume and uses JAVA_TOOL_OPTIONS to load the agent
- This approach keeps the Docker image clean and allows for easier agent version updates

**Previous approach (deprecated):**
- Agent was downloaded during CI build using wget and included in the Docker image
- Agent was activated via Spring Boot buildpack environment variables (BPE_PREPEND_JAVA_TOOL_OPTIONS)

**Why the change:**
- Smaller Docker images without the agent JAR (~6MB saved)
- Easier to update agent version without rebuilding the entire application
- Better separation of concerns between application code and monitoring infrastructure
- Follows Azure Container Apps best practices for sidecar/init container patterns
