# Publication Quality System - Current Coding Skill

This document describes the architecture and coding patterns that actually exist in the current codebase. It is intended for another AI or developer to generate new modules that match this project without inventing unrelated structure.

## Section 1 - Project Architecture

The project is a Spring Boot layered application under the root package `publication_quality_system`.

Current implemented flow:

```text
HTTP request
  -> controllers
  -> services interfaces
  -> services.impl classes
  -> repositories
  -> entities
  -> PostgreSQL/H2 test database

entities
  -> mapped to DTOs by MapStruct mappers

services
  -> throw AppException(errorCode)

GlobalExceptionHandler
  -> converts exceptions to BaseResponse

controllers
  -> return ResponseEntity<BaseResponse<T>>
```

Implemented module boundaries:

- User management: `User`, `UserDto`, `UserRepository`, `UserMapper`, `UserService`, `UserServiceImpl`, `UserController`.
- Role and permission management: `Role`, `Permission`, `RoleDto`, repositories, mapper, service, controller, seeders.
- Research profile management: `ResearchProfile`, `ResearchProfileDto`, repository, mapper, service, controller.
- Research group membership management: `ResearchGroup`, `ResearchGroupMember`, DTOs, repositories, mappers, service, controller.

Dependency direction is mostly one-way:

```text
controller -> service -> repository -> entity
controller/service -> dto
service -> mapper
mapper -> entity + dto
exception handler -> base response
seeder -> repositories + enums + entities
```

Controllers do not access repositories directly in the current code. Business validation such as duplicate usernames, duplicate group names, member uniqueness, and leader rules lives in services.

RBAC flow currently implemented:

```text
PermissionName enum
  -> PermissionSeeder stores permission names as String
  -> RoleSeeder assigns permissions to roles
  -> Role.permissions relationship
  -> @PreAuthorize("hasAuthority('...')") on controllers
```

The scanned source contains permission storage, role assignment, method security, and controller-level authority checks. It does not currently contain a `CustomUserDetails` or JWT converter class that maps persisted permissions into Spring Security authorities.

## Section 2 - Package Layout

Actual package layout:

```text
publication_quality_system
+-- Application.java
+-- base
+-- config
|   +-- seeder
+-- controllers
+-- dtos
+-- entities
+-- enums
+-- exceptions
+-- mappers
+-- repositories
+-- services
    +-- impl
```

Package responsibilities:

- `base`: reusable controller/service/response/entity/error abstractions.
- `config`: Spring configuration, OpenAPI config, security config, data initializer.
- `config.seeder`: startup seeders for permissions and roles.
- `controllers`: REST controllers. Existing controllers extend `BaseCrudController` when CRUD shape fits.
- `dtos`: API-facing data objects. Current project uses `XxxDto`; no `Request` or `Response` suffix currently exists.
- `entities`: JPA entities extending `BaseEntity`.
- `enums`: role, permission, member status, rank, and group member role enums.
- `exceptions`: `AppException`, module-specific error code enums, global exception handler.
- `mappers`: MapStruct mappers. Package name is `mappers`, not `mapper`.
- `repositories`: Spring Data JPA repositories.
- `services`: service interfaces.
- `services.impl`: service implementations.

There is no current `security` package. Security configuration currently lives in `config` (`SecurityConfig`), and the scanned source does not contain a `security` directory.

## Section 3 - Base System Analysis

### BaseEntity

`BaseEntity` is a `@MappedSuperclass` for all entities.

Fields:

- `id: Long`
- `createdAt: LocalDateTime`
- `updatedAt: LocalDateTime`
- `deleted: boolean`
- `createdBy: String`
- `updatedBy: String`

Lifecycle hooks:

- `@PrePersist` sets `createdAt` and `updatedAt`.
- `@PreUpdate` updates `updatedAt`.

Use this for new entities:

```java
public class Xxx extends BaseEntity {
}
```

### BaseResponse

All API responses are wrapped in `BaseResponse<T>`.

Fields:

- `success`
- `code`
- `message`
- `data`

Factory methods:

- `BaseResponse.created(data)` returns code `201`.
- `BaseResponse.success(data, message)` returns code `200`.
- `BaseResponse.error(code, message)` returns error payload.

### BaseController

`BaseController` provides helpers:

```java
protected <T> ResponseEntity<BaseResponse<T>> success(T data, String message)
protected <T> ResponseEntity<BaseResponse<T>> created(T data)
```

`created(data)` returns HTTP `201 CREATED`.

### BaseCrudService

Current generic service contract:

```java
public interface BaseCrudService<D, ID> {
    D create(D dto);
    D getById(ID id);
    D update(ID id, D dto);
    void delete(ID id);
    List<D> getAll(Pageable pageable);
}
```

New service interfaces may extend this when one DTO is used for CRUD.

### BaseCrudController

`BaseCrudController<D, ID>` extends `BaseController` and delegates CRUD calls to `BaseCrudService<D, ID>`.

Implemented endpoints:

- `POST /`
- `GET /{id}`
- `PUT /{id}`
- `DELETE /{id}`

It does not implement `GET /` for list; controllers add list endpoints manually where needed.

## Section 4 - Entity Design Pattern

Current entities:

- `User`
- `Role`
- `Permission`
- `ResearchProfile`
- `ResearchGroup`
- `ResearchGroupMember`

Common patterns:

- Entities are in `publication_quality_system.entities`.
- Entities extend `BaseEntity`.
- Entities use JPA annotations: `@Entity`, `@Table`, relationship annotations.
- Lombok is used with `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`; some entities also use `@Builder`.
- Long text fields use `@Column(columnDefinition = "TEXT")`.
- Enums use `@Enumerated(EnumType.STRING)` where entity field type is enum.
- IDs are inherited from `BaseEntity`.

Soft delete is partially implemented:

- `ResearchGroup` has `@SQLDelete` and `@SQLRestriction`.
- `ResearchGroupMember` has `@SQLDelete` and `@SQLRestriction`.
- `BaseEntity` has the `deleted` field.
- `User`, `Role`, `Permission`, and `ResearchProfile` currently do not all consistently declare `@SQLDelete` / `@SQLRestriction` in the scanned source, despite importing these annotations in some files.

Relationship patterns:

- `ResearchGroup` -> `ResearchGroupMember`: `@OneToMany(mappedBy = "researchGroup", cascade = CascadeType.ALL, orphanRemoval = false)`.
- `ResearchGroupMember` -> `ResearchGroup`: `@ManyToOne(fetch = FetchType.LAZY, optional = false)`.
- `ResearchGroupMember` -> `User`: `@ManyToOne(fetch = FetchType.LAZY, optional = false)`.
- `ResearchProfile` -> `User`: `@OneToOne(fetch = FetchType.LAZY, optional = false)`.
- `User` -> `ResearchProfile`: `@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`.
- `User` -> `ResearchGroupMember`: `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)`.
- `User` -> `Role`: current code uses `@ManyToMany(fetch = FetchType.EAGER)`.
- `Role` -> `Permission`: current code uses `@ManyToMany(fetch = FetchType.EAGER)`.

Research group membership uses a join entity because membership stores domain data:

- member role
- status
- joined/left dates
- contribution score
- assigned/completed reviews
- responsibilities

Standard entity template matching current style:

```java
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "xxx")
@SQLDelete(sql = "UPDATE xxx SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Xxx extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
```

Use this template only if the new entity should support soft delete like `ResearchGroup` and `ResearchGroupMember`.

## Section 5 - DTO Pattern

Current DTOs:

- `UserDto`
- `RoleDto`
- `ResearchProfileDto`
- `ResearchGroupDto`
- `ResearchGroupMemberDto`

Current naming convention actually present:

- Use `XxxDto`.
- No current DTO uses `Request` suffix.
- No current DTO uses `Response` suffix.
- No current create/update DTO classes are present in the scanned source.

Validation appears directly on DTO fields:

- `@NotBlank` on `UserDto.username`.
- `@NotBlank` and `@Email` on `UserDto.email`.
- `@NotBlank` on `ResearchGroupDto.name`.
- `@NotNull` on `ResearchGroupMemberDto.userId`.

Nested DTO pattern:

- `UserDto` contains `Set<RoleDto> roles`.
- `ResearchGroupDto` contains `ResearchGroupMemberDto leader` and `List<ResearchGroupMemberDto> members`.

DTOs generally use:

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxxDto {
}
```

`RoleDto` currently uses only `@Data` without `@JsonInclude`.

DTO template matching current style:

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxxDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;
}
```

## Section 6 - Service Pattern

Current service interfaces are in `services` and implementations are in `services.impl`.

Implemented interface pattern:

```java
public interface XxxService extends BaseCrudService<XxxDto, Long> {
}
```

When module-specific operations exist, they are added to the service interface. Example: `ResearchGroupService` adds member and leader operations.

Implementation pattern:

```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {
    private final XxxRepository repository;
    private final XxxMapper mapper;
}
```

Transaction usage:

- Write methods use `@Transactional`.
- Some read list methods use `@Transactional(readOnly = true)`.
- Some read single methods currently do not declare read-only transaction, so new code should follow the stronger existing list pattern where possible.

Validation and business rules live in services:

- User duplicate username check in `UserServiceImpl.create`.
- Research group duplicate name check in `ResearchGroupServiceImpl.create/update`.
- Research group member duplicate active membership check in `addMember`.
- Leader assignment rule in `assignGroupLeader`.
- Leader cannot be removed/deactivated in `removeMember` and `updateMemberStatus`.

Exception pattern:

```java
throw new AppException(UserErrorCode.USER_NOT_FOUND);
```

Mapper usage:

- Entity creation: `mapper.toXxxEntity(dto)`.
- Entity update: `mapper.updateXxxFromDto(dto, entity)`.
- Response conversion: `mapper.toXxxDto(entity)`.

Service implementation template:

```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {

    private final XxxRepository repository;
    private final XxxMapper mapper;

    @Override
    @Transactional
    public XxxDto create(XxxDto dto) {
        Xxx entity = mapper.toXxxEntity(dto);
        entity = repository.save(entity);
        return mapper.toXxxDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public XxxDto getById(Long id) {
        Xxx entity = repository.findById(id)
                .orElseThrow(() -> new AppException(XxxErrorCode.XXX_NOT_FOUND));
        return mapper.toXxxDto(entity);
    }

    @Override
    @Transactional
    public XxxDto update(Long id, XxxDto dto) {
        Xxx entity = repository.findById(id)
                .orElseThrow(() -> new AppException(XxxErrorCode.XXX_NOT_FOUND));
        mapper.updateXxxFromDto(dto, entity);
        entity = repository.save(entity);
        return mapper.toXxxDto(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<XxxDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toXxxDto)
                .getContent();
    }
}
```

## Section 7 - Repository Pattern

Repositories:

- Extend `JpaRepository<Entity, Long>`.
- Are annotated with `@Repository`.
- Use derived query methods.
- Return `Optional<T>` for unique lookups.
- Use `Page<T>` with `Pageable` for paginated queries.

Examples:

```java
Optional<User> findByUsername(String username);
Optional<User> findByEmail(String email);
Page<User> findAllByDeletedFalse(Pageable pageable);
Optional<ResearchGroup> findByName(String name);
boolean existsByName(String name);
Optional<ResearchGroupMember> findByResearchGroupIdAndUserId(Long groupId, Long userId);
```

No custom JPQL or native queries are present in the scanned source.

Repository template:

```java
@Repository
public interface XxxRepository extends JpaRepository<Xxx, Long> {
    Optional<Xxx> findByName(String name);
    boolean existsByName(String name);
}
```

## Section 8 - Controller Pattern

Controllers:

- Are in `controllers`.
- Use `@RestController`.
- Use `@RequestMapping`.
- Return `ResponseEntity<BaseResponse<T>>`.
- Extend `BaseCrudController<D, Long>` for generic CRUD endpoints.
- Add module-specific endpoints manually.
- Use `@Valid @RequestBody` for validated DTO input.
- Use `@PathVariable` for URL IDs.
- Use `Pageable` directly as a controller method parameter for list endpoints.
- Use `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` for protected operations.

Existing base paths:

- `/api/lab-members/users`
- `/api/lab-members/roles`
- `/api/lab-members/profiles`
- `/api/lab-members/research-groups`

Generic CRUD endpoints are inherited from `BaseCrudController`; controllers do not always override each method.

Controller template:

```java
@RestController
@RequestMapping("/api/lab-members/xxx")
public class XxxController extends BaseCrudController<XxxDto, Long> {

    private final XxxService xxxService;

    public XxxController(XxxService xxxService) {
        super(xxxService);
        this.xxxService = xxxService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('XXX_READ')")
    public ResponseEntity<BaseResponse<List<XxxDto>>> getAll(Pageable pageable) {
        return success(xxxService.getAll(pageable), "Xxx retrieved successfully");
    }
}
```

## Section 9 - Mapper Pattern

Mappers are MapStruct interfaces in `publication_quality_system.mappers`.

Common annotation:

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
```

Method naming is module-specific, not fully uniform:

- `toUserDto`, `toUserEntity`, `updateUserFromDto`.
- `toRoleDto`, `toRoleEntity`, `updateRoleFromDto`.
- `toProfileDto`, `toProfileEntity`, `updateProfileFromDto`.
- `toGroupDto`, `toGroupEntity`, `updateGroupFromDto`.
- `ResearchGroupMemberMapper.toDto`.

Update methods use `@MappingTarget` and ignore fields that should not be overwritten:

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "roles", ignore = true)
void updateUserFromDto(UserDto dto, @MappingTarget User user);
```

Nested mapping examples:

- `ResearchProfileMapper` maps `user.id` to `userId`.
- `ResearchGroupMemberMapper` maps `user.id` to `userId` and `user.email` to `email`.
- `ResearchGroupMemberMapper` computes `fullName` from username through a default helper method.

Recursion avoidance:

- `ResearchGroupMapper` ignores `memberCount`, `leader`, `members` when converting entity to DTO.
- `ResearchGroupMapper` ignores `memberships` when converting DTO to entity and updating entity.
- `UserMapper` ignores `profile` and `groupMemberships` on update.
- `RoleMapper` ignores `permissions` on update.

Some mappers disable Lombok builder mapping with:

```java
@BeanMapping(builder = @Builder(disableBuilder = true))
```

Mapper template:

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface XxxMapper {

    XxxDto toXxxDto(Xxx entity);

    @Mapping(target = "id", ignore = true)
    Xxx toXxxEntity(XxxDto dto);

    @Mapping(target = "id", ignore = true)
    void updateXxxFromDto(XxxDto dto, @MappingTarget Xxx entity);
}
```

## Section 10 - Security / RBAC

Current security implementation:

- `SecurityConfig` is in `config`.
- Uses `@Configuration`, `@EnableWebSecurity`, and `@EnableMethodSecurity`.
- CSRF is disabled.
- Swagger/OpenAPI paths are public.
- All other requests require authentication.
- `PasswordEncoder` bean is `BCryptPasswordEncoder`.

Security filter chain:

```java
http
    .csrf(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(PUBLIC_URLS).permitAll()
        .anyRequest().authenticated());
```

Method security:

- Controllers use `@PreAuthorize`.
- Current authorization style is permission-based with `hasAuthority`.
- Current code does not use `hasRole` in scanned controllers.

Permission naming:

- Permissions are uppercase enum constants in `PermissionName`.
- Format is generally `MODULE_ACTION`, with ownership variants for paper/integrity permissions such as `PAPER_READ_OWN`.

RBAC persistence:

- `Permission.name` is currently a `String`, not an enum field, to avoid PostgreSQL enum CHECK constraint drift.
- `PermissionSeeder` iterates `PermissionName.values()` and inserts missing permissions.
- `RoleSeeder` assigns permission sets to each `RoleName`.
- `ADMIN` receives all permissions.
- The current scanned source does not include a user-details authority mapping class, so permission-to-authority conversion is an implementation gap to fill before relying on method permissions at runtime.

OpenAPI:

- `OpenApiConfig` defines a bearer JWT security scheme for API documentation.

Current gaps:

- The current scanned source does not contain a JWT decoder, Cognito integration class, custom authentication filter, or `security` package.
- Cognito and OAuth2 resource server dependencies exist in `pom.xml`, but the application code shown only configures method security and authenticated request enforcement.
- OpenAPI documents bearer JWT, but runtime JWT/Cognito authority extraction is not implemented in the scanned source.

RBAC controller example:

```java
@PreAuthorize("hasAuthority('RESEARCH_GROUP_MEMBER_MANAGE')")
```

## Section 11 - Exception Handling

Exceptions are centralized through `GlobalExceptionHandler`.

`AppException`:

- Extends `RuntimeException`.
- Wraps a `BaseErrorCode`.
- Services throw `AppException` for business/domain errors.

`BaseErrorCode`:

- Provides HTTP status through `getHttpStatus`.
- Provides numeric code using HTTP status value.
- Provides error message.

Module error enums:

- `UserErrorCode`
- `RoleErrorCode`
- `ResearchProfileErrorCode`
- `ResearchGroupErrorCode`

Global handling:

- `AppException` maps to `BaseResponse.error(code, message)` with the error code HTTP status.
- `MethodArgumentNotValidException` maps to HTTP 400 and returns the first validation message.

New module exception template:

```java
@RequiredArgsConstructor
public enum XxxErrorCode implements BaseErrorCode {
    XXX_NOT_FOUND("Xxx not found", HttpStatus.NOT_FOUND),
    XXX_ALREADY_EXISTS("Xxx already exists", HttpStatus.CONFLICT),
    VALIDATION_ERROR("Validation error", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
```

## Section 12 - Database & Soft Delete

Database configuration:

- Main profile uses PostgreSQL.
- `spring.jpa.hibernate.ddl-auto=update`.
- Datasource values are read from `.env` variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
- Docker Compose provides PostgreSQL 16 Alpine with database `publication_quality_system`, user `postgres`, password `root`.
- Test profile uses H2 in PostgreSQL mode with `ddl-auto=create-drop`.

Soft delete:

- `BaseEntity.deleted` exists globally.
- `ResearchGroup` and `ResearchGroupMember` actively use Hibernate soft delete with `@SQLDelete` and `@SQLRestriction`.
- Some repositories define `findAllByDeletedFalse(Pageable pageable)`.
- Not all entities consistently apply Hibernate soft delete annotations in current source.

Permission schema drift handling:

- `Permission.name` is a `String` because PostgreSQL CHECK constraints generated from Java enums do not update automatically with `ddl-auto=update`.
- Permission values are still controlled by `PermissionName` in seeders.

## Section 13 - File/S3 Architecture

Current source status:

- `pom.xml` includes AWS SDK dependencies for S3, Bedrock runtime, Transcribe, Translate, Polly, CloudWatch Logs, and the older `aws-java-sdk-s3`.
- The scanned source does not contain implemented S3 config, upload controller, upload service, file metadata entity, presigned URL generator, or multipart upload flow.

Current documented extension pattern should therefore follow existing layers:

```text
UploadController
  -> UploadService
  -> S3 client/config
  -> optional FileMetadataRepository
  -> FileMetadata entity
  -> FileDto
```

When implementing uploads, keep the project's current style:

- Return `BaseResponse<FileDto>`.
- Put upload business logic in service.
- Persist metadata through repository.
- Throw `AppException(FileErrorCode.XYZ)`.
- Secure endpoints with `hasAuthority('FILE_UPLOAD')` or module-specific permission names.

## Section 14 - Redis / Cache

Current source status:

- `pom.xml` includes `spring-boot-starter-data-redis`, `spring-boot-starter-cache`, and embedded Redis for tests.
- The scanned source does not contain Redis configuration classes, `RedisTemplate`, `@Cacheable`, `@EnableCaching`, bitmap usage, or cache services.

Future cache code should follow existing layers and response style, but there is no current project-specific Redis implementation pattern beyond dependencies.

## Section 15 - AI Integration

Current source status:

- `pom.xml` contains AWS Bedrock runtime and OpenSearch/Elasticsearch-related dependencies.
- No scanned source contains OpenAI, OpenRouter, Spring AI service, embedding workflow, vector indexing code, semantic search service, conversation history model, or AI controller.

Therefore, AI integration is a planned or dependency-level capability, not an implemented code pattern in the current project.

If implemented later, follow existing module structure:

```text
AiController -> AiService -> external client / repository -> DTO response
```

## Section 16 - Coding Style

Observed style:

- Java 21.
- Spring Boot 3.3.9.
- Package name is lowercase snake style: `publication_quality_system`.
- Classes use PascalCase.
- Methods and fields use camelCase.
- Tables use snake_case.
- Endpoint paths use kebab-case or plural resource names.
- Constructor injection is primarily via Lombok `@RequiredArgsConstructor`; some controllers use explicit constructor because they call `super(service)`.
- Lombok:
  - DTOs use `@Data`.
  - Entities use `@Getter` and `@Setter`; some use `@Builder`.
  - Services and seeders use `@RequiredArgsConstructor`.
  - `BaseResponse` uses `@Builder`, getters/setters, all/no args constructors.
- Optional handling:
  - `findBy...().orElseThrow(() -> new AppException(...))`.
  - Seeders sometimes use `orElse(null)` then `if (role == null)`.
- Streams:
  - Used for mapping pages and lists to DTOs.
  - Used in seeders to collect permission sets.
- Validation:
  - DTO annotations plus `@Valid @RequestBody`.
- Logging:
  - No project logging pattern is implemented in scanned source.
- Comments:
  - Sparse comments, mostly in config/seeder and `pom.xml`.

## Section 17 - Anti-Patterns Observed or Avoided

Avoided in current implemented code:

- Controllers do not return entities directly.
- Controllers return `BaseResponse`.
- Controllers generally delegate business logic to services.
- Services use `AppException` for domain errors.
- Controllers use permission authorities instead of role names in `@PreAuthorize`.
- Research group membership avoids direct `ResearchGroup` to `User` `ManyToMany`; it uses `ResearchGroupMember`.
- MapStruct avoids recursive DTO mapping by ignoring relationship collections in key mappers.

Still present or inconsistent in current source:

- `AppException` extends `RuntimeException` by design; services should throw `AppException`, not raw `RuntimeException`.
- `User.roles` and `Role.permissions` use direct `ManyToMany(fetch = FetchType.EAGER)`.
- Soft delete annotations are not consistent across all entities.
- Some read methods lack `@Transactional(readOnly = true)`.
- `UserDto` still contains `password`, so be careful not to expose password values in response DTOs unless this is refactored.
- `RoleController` declares a `roleService` field but its constructor does not assign it in the scanned source; this should be fixed before relying on `assignRole`.

## Section 18 - How To Generate a New Module

Use this workflow to create a new module matching the current project.

Example module: `Paper`.

1. Create enum permissions in `PermissionName`.

```java
PAPER_CREATE, PAPER_READ, PAPER_UPDATE, PAPER_DELETE
```

2. Create entity in `entities`.

```java
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "papers")
@SQLDelete(sql = "UPDATE papers SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Paper extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String abstractText;
}
```

3. Create DTO in `dtos`.

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaperDto {
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String abstractText;
}
```

4. Create repository in `repositories`.

```java
@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    Page<Paper> findAllByDeletedFalse(Pageable pageable);
}
```

5. Create mapper in `mappers`.

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaperMapper {
    PaperDto toPaperDto(Paper paper);

    @Mapping(target = "id", ignore = true)
    Paper toPaperEntity(PaperDto dto);

    @Mapping(target = "id", ignore = true)
    void updatePaperFromDto(PaperDto dto, @MappingTarget Paper paper);
}
```

6. Create error code enum in `exceptions`.

```java
@RequiredArgsConstructor
public enum PaperErrorCode implements BaseErrorCode {
    PAPER_NOT_FOUND("Paper not found", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("Validation error", HttpStatus.BAD_REQUEST);
}
```

7. Create service interface in `services`.

```java
public interface PaperService extends BaseCrudService<PaperDto, Long> {
}
```

8. Create service implementation in `services.impl`.

Follow the CRUD implementation pattern from Section 6.

9. Create controller in `controllers`.

```java
@RestController
@RequestMapping("/api/lab-members/papers")
public class PaperController extends BaseCrudController<PaperDto, Long> {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        super(paperService);
        this.paperService = paperService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAPER_READ')")
    public ResponseEntity<BaseResponse<List<PaperDto>>> getAll(Pageable pageable) {
        return success(paperService.getAll(pageable), "Papers retrieved successfully");
    }
}
```

10. Update `RoleSeeder`.

- `ADMIN` automatically receives all permissions.
- Add module permissions to relevant non-admin roles by extending `getPermissionsForRole`.

11. Run compile.

```powershell
mvn clean compile
```

12. If database enum/check constraints are involved, prefer storing frequently changing permission names as strings, following current `Permission.name`.

## Section 19 - Generated Artifacts

This project-level coding skill is split into two root files:

- `PROJECT_CODING_SKILL.md`: architecture, current implementation patterns, extension rules, and known current gaps.
- `MODULE_GENERATION_PROMPT.md`: reusable prompt for generating future modules in this exact project style.

When generating new code, use both files together. Prefer `PROJECT_CODING_SKILL.md` for facts and `MODULE_GENERATION_PROMPT.md` for the execution prompt.
