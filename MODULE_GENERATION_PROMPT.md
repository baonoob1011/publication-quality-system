# Module Generation Prompt

Use this prompt when asking another AI/developer to generate a new module for this project.

```text
You are working in the Publication Quality System Spring Boot project.

Read PROJECT_CODING_SKILL.md first and follow only the patterns that actually exist in the current codebase.

Generate a new module named: <MODULE_NAME>

Requirements:

1. Use package root publication_quality_system.
2. Follow the existing layered flow:
   controller -> service interface -> service impl -> repository -> entity.
3. Use DTOs from publication_quality_system.dtos.
4. Use MapStruct mappers from publication_quality_system.mappers.
5. Use repositories from publication_quality_system.repositories.
6. Use service interfaces from publication_quality_system.services.
7. Use service implementations from publication_quality_system.services.impl.
8. Use entities from publication_quality_system.entities.
9. Use enums from publication_quality_system.enums.
10. Use exception classes from publication_quality_system.exceptions.

Controller rules:
- Return ResponseEntity<BaseResponse<T>> only.
- Do not return entities directly.
- Extend BaseCrudController<XxxDto, Long> if the module fits the generic CRUD style.
- Add GET list endpoint manually using Pageable and List<XxxDto>.
- Use @PreAuthorize("hasAuthority('MODULE_ACTION')").
- Do not hardcode role names.
- Keep business logic out of controller.

Service rules:
- Interface should extend BaseCrudService<XxxDto, Long> when appropriate.
- Implementation should use @Service and @RequiredArgsConstructor.
- Write methods should use @Transactional.
- Read list methods should use @Transactional(readOnly = true).
- Use repositories for persistence.
- Use MapStruct mapper for DTO/entity conversion.
- Throw AppException(ErrorCode.XYZ), not raw RuntimeException.

Repository rules:
- Extend JpaRepository<Entity, Long>.
- Use derived query methods such as findBy..., existsBy..., findAllByDeletedFalse(...).
- Prefer Optional<T> for unique lookups.
- Avoid native query unless absolutely needed.

Entity rules:
- Extend BaseEntity.
- Use @Entity and @Table(name = "snake_case_table").
- Use Lombok style already used in the project.
- Use @Column(columnDefinition = "TEXT") for long text.
- Use EnumType.STRING for enum entity fields.
- For soft-deletable entities, use @SQLDelete and @SQLRestriction like ResearchGroup.
- Avoid direct ManyToMany for relationships that need extra fields; create a join entity.

DTO rules:
- Use XxxDto naming.
- Do not use Request or Response suffix unless the project is explicitly refactored.
- Use @Data.
- Use @JsonInclude(JsonInclude.Include.NON_NULL) where matching existing DTO style.
- Put validation annotations on DTO fields.

Mapper rules:
- Use @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE).
- Provide toXxxDto, toXxxEntity, and updateXxxFromDto methods.
- Use @MappingTarget for updates.
- Ignore id and relationship collections on update.
- Avoid recursive mapping.

RBAC rules:
- Add new permission constants to PermissionName using MODULE_ACTION format.
- Update RoleSeeder for non-admin roles if needed.
- ADMIN receives all PermissionName values automatically.
- Secure controller methods with hasAuthority('MODULE_ACTION').

Exception rules:
- Create XxxErrorCode implementing BaseErrorCode if the module needs module-specific errors.
- Let GlobalExceptionHandler produce BaseResponse.error.

Seeder rules:
- Seeders implement DataSeeder.
- Use getOrder() for deterministic startup order.
- Use @Transactional.
- Make seed logic idempotent.

Database/config rules:
- Main DB is PostgreSQL using DB_URL, DB_USERNAME, DB_PASSWORD.
- Tests use H2 in PostgreSQL mode.
- Run mvn clean compile after changes.

Before final response:
- List created/changed files.
- Report whether mvn clean compile passed.
- Mention any existing project inconsistency you did not change.
```
