package id.laris.assistant.repository.rowmapper;

import id.laris.assistant.domain.Users;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Users}, with proper type conversions.
 */
@Service
public class UserRowMapper implements BiFunction<Row, String, Users> {

    private final ColumnConverter converter;

    public UserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Users} stored in the database.
     */
    @Override
    public Users apply(Row row, String prefix) {
        Users entity = new Users();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        return entity;
    }
}
