package util;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResultSetUtils {

    private ResultSetUtils() {
    }

//    private static final Logger LOG = LoggerFactory.getLogger(ResultSetUtils.class);

    public static List<Map<String, String>> getRowsOfQuery(final ResultSet result) throws SQLException {

        List<Map<String, String>> singleQueryResults = new ArrayList<>();
        List<String> columnNames = getColumnNames(result);

        while (result.next()) {
            Map<String, String> singleQueryResultRow = new HashMap<>();
            for (String columnName : columnNames) {
                try {
                    String finalValue = getValueByColumnName(result, columnName);
                    singleQueryResultRow.put(columnName, finalValue);
                } catch (SQLException e) {
                    if (e.getMessage().contains("can not be represented as java.sql.Date")) {
                        singleQueryResultRow.put(columnName, null);
                    } else {
                        throw new IllegalStateException(
                                String.format("SQL statement was not executed successfully: %s", e.getMessage()), e);
                    }
                }
            }
            singleQueryResults.add(singleQueryResultRow);
        }
        return singleQueryResults;
    }

    private static String getValueByColumnName(ResultSet result, String columnName) throws SQLException {
        String finalValue = null;
        if (result.getObject(columnName) != null) {
            Class<?> type = result.getObject(columnName).getClass();
            if (type.equals(Double.class) || type.equals(Timestamp.class)) {
                finalValue = StringUtils.removeEnd(result.getString(columnName), ".0");
            } else if (type.equals(Time.class)) {
                finalValue = StringUtils.removeEnd(result.getString(columnName), ".000000");
            } else {
                finalValue = result.getString(columnName);
            }
        }
        return finalValue;
    }

    private static List<String> getColumnNames(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int columnPosition = 1; columnPosition <= resultSetMetaData.getColumnCount(); columnPosition++) {
            columnNames.add(resultSetMetaData.getColumnLabel(columnPosition));
        }

        if (!columnNames.isEmpty()) {
            try {
                columnNames.remove("Sys_Audit_Created_Datetime");
                columnNames.remove("Sys_Audit_Updated_Datetime");
            } catch (ClassCastException | UnsupportedOperationException e) {
                throw new RuntimeException(
                        String.format("Audit columns exclusion from result set failed: %s", e.getMessage()), e);
            }
        }

        return columnNames;
    }
}
