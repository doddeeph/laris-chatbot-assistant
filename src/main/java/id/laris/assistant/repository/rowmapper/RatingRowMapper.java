package id.laris.assistant.repository.rowmapper;

import id.laris.assistant.domain.Ratings;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ratings}, with proper type conversions.
 */
@Service
public class RatingRowMapper implements BiFunction<Row, String, Ratings> {

    private final ColumnConverter converter;

    public RatingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ratings} stored in the database.
     */
    @Override
    public Ratings apply(Row row, String prefix) {
        Ratings entity = new Ratings();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Double.class));
        entity.setTimestamp(converter.fromRow(row, prefix + "_timestamp", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setMovieId(converter.fromRow(row, prefix + "_movie_id", Long.class));
        return entity;
    }
}
