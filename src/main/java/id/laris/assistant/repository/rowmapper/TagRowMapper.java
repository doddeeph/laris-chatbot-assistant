package id.laris.assistant.repository.rowmapper;

import id.laris.assistant.domain.Tags;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tags}, with proper type conversions.
 */
@Service
public class TagRowMapper implements BiFunction<Row, String, Tags> {

    private final ColumnConverter converter;

    public TagRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tags} stored in the database.
     */
    @Override
    public Tags apply(Row row, String prefix) {
        Tags entity = new Tags();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTag(converter.fromRow(row, prefix + "_tag", String.class));
        entity.setTimestamp(converter.fromRow(row, prefix + "_timestamp", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setMovieId(converter.fromRow(row, prefix + "_movie_id", Long.class));
        return entity;
    }
}
