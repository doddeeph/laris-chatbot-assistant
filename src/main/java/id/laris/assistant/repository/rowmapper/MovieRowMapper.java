package id.laris.assistant.repository.rowmapper;

import id.laris.assistant.domain.Movies;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Movies}, with proper type conversions.
 */
@Service
public class MovieRowMapper implements BiFunction<Row, String, Movies> {

    private final ColumnConverter converter;

    public MovieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Movies} stored in the database.
     */
    @Override
    public Movies apply(Row row, String prefix) {
        Movies entity = new Movies();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setGenres(converter.fromRow(row, prefix + "_genres", String.class));
        return entity;
    }
}
