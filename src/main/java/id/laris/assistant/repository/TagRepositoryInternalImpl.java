package id.laris.assistant.repository;

import id.laris.assistant.domain.Tags;
import id.laris.assistant.repository.rowmapper.MovieRowMapper;
import id.laris.assistant.repository.rowmapper.TagRowMapper;
import id.laris.assistant.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Tags entity.
 */
@SuppressWarnings("unused")
class TagRepositoryInternalImpl extends SimpleR2dbcRepository<Tags, Long> implements TagRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper usersMapper;
    private final MovieRowMapper moviesMapper;
    private final TagRowMapper tagsMapper;

    private static final Table entityTable = Table.aliased("tags", EntityManager.ENTITY_ALIAS);
    private static final Table usersTable = Table.aliased("users", "users");
    private static final Table moviesTable = Table.aliased("movies", "movies");

    public TagRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper usersMapper,
        MovieRowMapper moviesMapper,
        TagRowMapper tagsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Tags.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.usersMapper = usersMapper;
        this.moviesMapper = moviesMapper;
        this.tagsMapper = tagsMapper;
    }

    @Override
    public Flux<Tags> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Tags> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TagSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(usersTable, "users"));
        columns.addAll(MovieSqlHelper.getColumns(moviesTable, "movies"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(usersTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", usersTable))
            .leftOuterJoin(moviesTable)
            .on(Column.create("movie_id", entityTable))
            .equals(Column.create("id", moviesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Tags.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Tags> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Tags> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Tags process(Row row, RowMetadata metadata) {
        Tags entity = tagsMapper.apply(row, "e");
        entity.setUsers(usersMapper.apply(row, "users"));
        entity.setMovies(moviesMapper.apply(row, "movies"));
        return entity;
    }

    @Override
    public <S extends Tags> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
