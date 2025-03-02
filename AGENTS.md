## General rules (critical)

- After changes are done make sure code compiles successfully. Bash command: `mvnw compile`.

## MCP Servers (critical)

- **`serena`** — Semantic code nav/editing. Prefer over reading entire files.
- **`context7`** — Up-to-date library docs. Prefer over training knowledge.

**ALWAYS** use `context7` for library docs: `resolve-library-id` then `query-docs`. Do NOT guess API signatures or use training knowledge.

## Code-style

### Whitespace and alignment

- Line length: **140 chars**. Imports and package declarations are exempt.
- Indent: **4 spaces**. Tabs (`\t`) are forbidden.
- Continuation indent: **8 spaces** (two levels) when wrapping an expression.
- Empty lines must not contain whitespace.
- Opening brace sits on the same line as the statement.
- Method arguments / constructor parameters that don't fit on one line → one per line. Commas stay on the previous line; `.` and `::` go on the new line.

```java
service = new Service(
        property1,
        property2,
        property3
);

stages.get(idx).stream()
        .map(this::convert)
        .forEach(list::add);
```

### Braces

- `if` / `else` / `for` / `while` / `do` always require braces, even for a single statement.
- Empty blocks must contain at least a comment or statement. Exceptions allowing bare `{}`: empty constructors, lambdas, methods, class declarations, empty `for` loops.
- Opening brace is on the same line (`if (x) {`), never on a new line.

### Class contents

#### Javadoc

- Extensible classes (non-`final` `public`) must have Javadoc.
- Overridable methods must have Javadoc: `public` / `protected`, non-`static`, non-`final`, non-`abstract`. Exempt: `@Override`, `@Test`, methods up to 2 lines.
- Javadoc text is in **Russian** (comment language rule, see `## Comments`). Auto-generated headers like `Created by` are forbidden.
- Names of code objects(classes, variables, methods) should be wrapped into `{@code }` or `{@link }` if mentioned first time. Exceptions are `true`, `false`, `null`.
- What methods do should be described in personal form e.g. `Авторизует пользователя.` not  `Авторизовать пользователя.`.
- If method starts with `get` prefix it description should start with `Вовзращает`.
- Description of every variable using `@param` should start after 2 empty spaces. Example: `@param name  имя пользователя`.
- Methods of `interface` that extends `JpaRepository` should not have a Javadoc.

#### Utility classes

- Utility classes (static-only members) must have a private constructor.
- A class with only a private constructor and no subclasses must be declared `final`.

#### Modifiers and members

- Modifier order as enforced by checkstyle: `private static final int X` (not `private final static int X`).
- No blank line between member declarations.
- Group fields by meaning and modifiers — statics together, finals together, etc.
- `@Override` methods are grouped together; no private helpers between them.

#### Visibility
- Fields are `private` by default. `public` fields are forbidden; exception: `public static final` constants.
- If class used as a parameter of `IdClass` annotation, its fields must be `protected`.

### Comments

- All Java comments (`//`, `/* */`, `/** */`) are written in **Russian**. Identifiers (class / method / variable names) stay in English.
- A comment explains **why**, not **what**. If you need a "what does this do" comment, rename the method or variable instead.
- Valid reasons to add a comment:
    - non-trivial business logic with motivation
    - warning about a non-obvious side effect
- Javadoc is added only to public methods, private methods don't need Javadoc.

### Variables and naming

- Classes, interfaces, enums, annotations: `PascalCase`.
- Methods, parameters, variables, local constants: `camelCase`.
- Class-level constants: `SCREAMING_SNAKE_CASE`.
- Package names: lowercase Latin letters and digits; digits not allowed in the first position. Use the singular form (`component`, not `components`; `service`, not `services`).
- Utility class names are singular (`EntityUtil`, not `EntityUtils`). 
- `var` is used for non-primitive types whenever it is possible (`var text = "123"`, `var obj = new Object()`).
- `var` is **not** used for primitives — use the explicit type (`int number = 123`).
- DTOs should be `record`'s not `class`'es.
- If DTO is used within http its name should either end with `ResponseDto` or `RequestDto` depending on for what object is used.
- Name of `@Entity` classes should be taken from DB table name and transformed to `PascalCase`: `user_to_game_session` → `UserToGameSession`.

### Null and Optional

- Nullability is annotated on public APIs (fields, parameters, return values) with `@Nullable`.
- Default — no annotation — means **not null**.
- `Optional<T>` is used **only as a return type** of a method that genuinely may return "nothing", and that is part of the contract.
    - `Optional` fields are forbidden.
    - `Optional` parameters are forbidden. Use method overloading or `@Nullable` instead.
- Always return an empty collection instead of `null`.

### Collections and streams

- Immutable literal collections — `List.of(...)`, `Set.of(...)`, `Map.of(...)`. For empty — `List.of()`, not `Collections.emptyList()`.
- Streams — for transformations and aggregations (`map` / `filter` / `collect` / `reduce`).
- For side effects (`forEach`) prefer a plain `for`-each loop — easier to read, easier to debug, does not hide control flow.

### Strings

- Concatenation with `+` is fine for short one-off expressions.
- In a loop — `StringBuilder` only.
- For multi-line literals — text blocks (`"""`).
- For parameterized templates — `String.format` or `MessageFormat`.

### Generics

- Raw types are forbidden (`List` → `List<?>` or `List<ConcreteType>`).
- On public APIs — apply PECS (Producer Extends, Consumer Super):

  ```java
  void addAll(Collection<? extends T> source);   // producer
  void drainTo(Collection<? super T> target);    // consumer
  ```

- `Object` as a collection element or parameter type is a smell. Use a generic or a sealed type instead.

### Annotations

- Spring boot annotations should go first.
  ```java
  // good
  @RestController
  @RequestMapping("/game-session")
  @RequiredArgsConstructor
  @Slf4j
  
  // bad
  @Slf4j
  @RequiredArgsConstructor
  @RestController
  @RequestMapping("/game-session")
  ```

### Tests

- Test name should start with name of method that would be tested, followed by method result(`failure`/`success`) and then followed by brief(~1-2 words) description of test specification.
```java
void loginUser_failure_userNotFound(String login, String password) {
    // test code
}

void loginUser_success_admin(String login, String password) {
    // test code
}
```
- If there are no different test cases for `success` test result then followed part can be skipped, e.g. `loginUser_success`.
- Every `@Test` has `@DisplayName`. Classes should not have `@DisplayName` unless they are `@Nested`.
  - More detailed description should be placed into `@DisplayName` and not placed into method name, e.g. what exception is thrown.
- Default format: `@DisplayName("methodName | expected behavior")`.
- Multiple tests for the same method → wrap in a `@Nested` class named after the method.
    - Class name should be the same as method name. There should be no `@DisplayName` on the class.
    - Inside `@Nested`, omit the method name from `@DisplayName` and do not add method name into test method names.
- Description inside `@DisplayName` should go in Russian.

### Misc

- Magic numbers are forbidden. Allowed: `0`, `1`, `-1`, `2` (for `/ 2`), math constants in a single place (`Math.PI`), array indices in context (`arr[0]`, `arr[1]`). Anything else — a named `private static final` constant or an enum.
- Array declarations use Java-style (`int[] array`), not C-style (`int array[]`).
- `long` literals end with uppercase `L` (`10L`, never `10l`).
- Non-exhaustive `switch` must have a `default` branch.
    - Fall-through on non-empty branches is forbidden (use `break` / `return` / `yield` / `throw` / `continue`).
    - Empty case branches may fall through to the next case.
    - The last branch may omit the terminating statement.
- Import order: third-party libraries → special imports → standard (`java` / `javax`) → static imports.

### Docker
- Dependencies downloading should go before `mvn clean package`, so they can be cached.
