package com.app.footballticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type {
    private long typeId;
    private String name;

    public Type(ResultSet rs) throws SQLException {
        this(rs.getLong("type_id"), rs.getString("name"));
    }
}
