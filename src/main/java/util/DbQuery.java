package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public final class DbQuery {
    private DbQuery() {
    }

//    private static final Logger LOG = Utils.getLoggerForClass();
    private static final String STATEMENT = "%s = '%s'";
    private static final String AND = " AND ";

    public static int update(final String updateQuery, final Object... parameters) {
        int resultCode;
        try (final PreparedStatement updateStatement = DbManager.getDbConnection().prepareStatement(updateQuery)) {
            if (updateStatement.toString().contains("?")) {
                fillPreparedStatementWithParameters(updateStatement, Arrays.asList(parameters));
            }
//            LOG.info(updateQuery);
            resultCode = updateStatement.executeUpdate();
//            LOG.info("DB manipulation. Affected rows: {}", resultCode);
        } catch (final SQLException exc) {
            throw new IllegalStateException("SQL statement for update rows was not executed successfully", exc);
        }
        return resultCode;
    }

    public static int update(final String updateQuery) {
        int resultCode;
        try (final PreparedStatement updateStatement = DbManager.getDbConnection().prepareStatement(updateQuery)) {
//            LOG.info("Query: \n{}", updateQuery);
            resultCode = updateStatement.executeUpdate();
//            LOG.info("DB manipulation. Affected rows: {}", resultCode);
        } catch (final SQLException exc) {
            throw new IllegalStateException("SQL statement for update rows was not executed successfully", exc);
        }
        return resultCode;
    }

    /**
     * Returns List of Rows. <br>
     * Each Row is represented by Map which contains entries: Column-name - Value
     */

    public static List<Map<String, String>> select(String selectQuery, final Object... parameters) {
        List<Map<String, String>> finalListOfMaps;
        Connection dbConnection = DbManager.getDbConnection();
        selectQuery = selectQuery.trim();
        try {
            dbConnection.setAutoCommit(false);
            String[] listOfExecution = selectQuery.split(";");
            if (listOfExecution.length > 1) {
                for (int queryIdx = 0; queryIdx < listOfExecution.length - 1; queryIdx++) {
                    try (PreparedStatement preparedStatement = dbConnection
                            .prepareStatement(listOfExecution[queryIdx])) {
                        fillPreparedStatementWithParameters(preparedStatement, Arrays.asList(parameters));
                        preparedStatement.executeQuery();
//                        LOG.info("Query: \n{}", preparedStatement);
                    }
                }
            }
            String lastQuery = listOfExecution[listOfExecution.length - 1];
            try (PreparedStatement preparedStatement = dbConnection.prepareStatement(lastQuery,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                fillPreparedStatementWithParameters(preparedStatement, Arrays.asList(parameters));
                ResultSet resultSet = preparedStatement.executeQuery();
//                LOG.info("Query: \n{}", preparedStatement);
                finalListOfMaps = ResultSetUtils.getRowsOfQuery(resultSet);
            }
            dbConnection.commit();
            dbConnection.setAutoCommit(true);

            return finalListOfMaps;

        } catch (final SQLException e) {
            if (e instanceof SQLSyntaxErrorException){
                throw new RuntimeException(e);
            } else if (e instanceof SQLTimeoutException){
                throw new RuntimeException(e);
            } else {
                throw new IllegalStateException(
                        String.format("SQL statement was not executed successfully: %s", e.getMessage()), e);
            }
        }
    }

    private static void fillPreparedStatementWithParameters(final PreparedStatement preparedStatement,
            final List<Object> parameterSet) throws SQLException {
        if (!preparedStatement.toString().contains("?")) {
            return;
        }
//        LOG.debug("parameters: {}", parameterSet);
        if (parameterSet == null) {
            throw new IllegalStateException(
                    "Query parameterSet == null, possible problem: you didn't set array(s) with params in query_params OR just check commas in query_params array");
        }
        for (int paramIdx = 0; paramIdx < parameterSet.size(); paramIdx++) {
            if (parameterSet.get(paramIdx) == null) {
                preparedStatement.setNull((paramIdx + 1), Types.VARCHAR);
            } else if (parameterSet.get(paramIdx).getClass().equals(Integer.class)) {
                preparedStatement.setInt((paramIdx + 1), Integer.parseInt(String.valueOf(parameterSet.get(paramIdx))));
            } else {
                preparedStatement.setString((paramIdx + 1), String.valueOf(parameterSet.get(paramIdx)));
            }
        }
    }

    public static String formatClause(final Map<String, String> clause) {
        List<String> clauseItems = clause.entrySet().stream()
                .map(entry -> String.format(STATEMENT, entry.getKey(), entry.getValue())).collect(Collectors.toList());
        return StringUtils.join(clauseItems, AND);
    }
}
