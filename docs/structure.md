# Clean Architecture Implementation

This project has been refactored to follow the principles of Clean Architecture, which focuses on separating concerns into distinct layers with well-defined responsibilities.

## Architecture Overview

The application is structured according to the principles of Clean Architecture with the following layers:

### Domain Layer
The innermost layer that contains the core business logic and business rules.

- `domain/model`: Domain models that represent business entities
- `domain/repository`: Repository interfaces defining how to access domain models
- `domain/exception`: Domain-specific exceptions

### Application Layer
Contains the application-specific business rules and orchestrates the flow of data.

- `application/port/in`: Use case interfaces (input ports)
- `application/port/out`: Repository interfaces (output ports, if needed)
- `application/service`: Implementation of use cases
- `application/exception`: Application-level exception handling

### Infrastructure Layer
The outermost layer that deals with frameworks, databases, UI, etc.

- `infrastructure/adapter/persistence`: Database-related implementations
- `infrastructure/adapter/persistence/entity`: JPA entities
- `infrastructure/adapter/persistence/repository`: Spring Data JPA repositories
- `infrastructure/adapter/web`: Web controllers
- `infrastructure/adapter/web/dto`: Data Transfer Objects
- `infrastructure/config`: Configuration classes

## Clean Architecture Principles Applied

1. **Independence of Frameworks**: Domain and application layers are independent of frameworks.
2. **Testability**: The core business logic is isolated and can be tested without external dependencies.
3. **Independence of UI**: The business logic is separated from the presentation layer.
4. **Independence of Database**: The domain model is separate from the database entities.
5. **Independence of External Agencies**: External systems are integrated via adapters.

## Dependency Rule

The fundamental rule is that dependencies can only point inward. The outer layers can depend on inner layers, but inner layers cannot depend on outer layers.

- Domain layer has no dependencies on other layers
- Application layer depends only on the domain layer
- Infrastructure layer depends on the application and domain layers

## Benefits of This Architecture

1. **Maintainability**: Changes in one layer don't affect other layers
2. **Testability**: Business logic can be tested in isolation
3. **Flexibility**: External systems can be replaced without affecting the core business logic
4. **Independence**: Allows for parallel development of different layers
5. **Clarity**: Clear boundaries between different parts of the system 