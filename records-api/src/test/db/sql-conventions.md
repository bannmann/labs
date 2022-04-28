# Conventions

## Column definitions

- Varchar sizes use powers of 2.
- As columns are nullable by default, omit the `null` keyword.

## Constraints

Constraints are always defined explicitly, not as part of a column definition. The name is always specified.

### Names
- Name is segmented with underscores.
- The first segment is the owning table name.
- The second segment is `pk`, `u` or `fk`.
- Further segments are added for each column. For readability, underscores from the original column names are omitted.

### Foreign key references
- Only specify the clauses that deviate from the default, e.g. include `on delete cascade` but don't add `on update no action`.
- If the referenced column is the primary key, specify only the table name without a column name.
- Omit the schema name (`public.`).

### Examples

```sql
constraint access_u_accountid_type_upstreamid
    unique (account_id, type, upstream_id)
```

```sql
constraint login_fk_accountid
    foreign key (account_id)
        references account
        on delete cascade
```

### Rationale

- Names are specified so that they are deterministic and kept identical between HSQLDB (for Java code generation & unit tests) and PostgreSQL. This allows the Records API to decide what exception to throw (e.g. `ConflictingEntityException` instead of `EntityReferenceException`).
- Naming scheme is intentionally different from PostgreSQL standard to ease finding constraints that were named automatically.
