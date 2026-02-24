Target structure:

Root packages:
- adapters: flat package for all Adapters (in term of Hexagonal architecture)
- app: Application Services (in term of Hexagonal architecture)
- ports/in: Input Ports (in term of Hexagonal architecture)
- ports/out: Output Ports (in term of Hexagonal architecture)- domain: Domain Services, Entities, Value Objects (in term of Hexagonal architecture)
- infra: Infrastructure Services (in term of Hexagonal architecture)

Domain subpackages (DDD structure):
- models: Domain Entities, Aggregates and Value Objects
- services: Domain Services
- repositories: Domain Repositories (interfaces)
- factories: Domain Factories
- specifications: Domain Specifications
- events: Domain Events
- exceptions: Domain Exceptions

