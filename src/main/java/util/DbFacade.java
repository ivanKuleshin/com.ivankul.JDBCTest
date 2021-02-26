package util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class consists exclusively of static methods that return
 * selected results from database. Using <tt>DbQuery</tt> class methods
 * for executing and preparing <i>select</i> query string.
 *
 * <p>The methods of this class all return data from db and if no data
 * present it will return empty <tt>List</tt> or empty <tt>Map</tt> or
 * <tt>null</tt> in case of getting field value by key
 *
 */

public final class DbFacade {

    private DbFacade() {
    }

    /**
     * Select db data using query string
     *
     * @param queryString formatted query string
     * @param queryParameters array of query arguments
     * @return <i>list</i> of corresponding table records
     */
    public static List<Map<String, String>> selectRecordsFromTableByQuery(final String queryString,
            final Object... queryParameters) {
        return DbQuery.select(String.format(queryString, queryParameters));
    }

    /**
     * Select db data using query string from <i>*.sql</i> file
     *
     * @param queryFileName name of file with query string with extension
     * @param queryParameters array of query arguments
     * @return <i>list</i> of corresponding table records
     */
    public static List<Map<String, String>> getTableRecords(final String queryFileName,
            final Object... queryParameters) {
        return DbQuery.select(ResourcesReader.getSqlAsString(queryFileName), queryParameters);
    }

    /**
     * Get record by index from db using query string from <i>*.sql</i> file.
     * Method returns empty <i>Map</i> if corresponding record not present
     *
     * @param queryFileName name of file with query string with extension
     * @param recordIndex index of record
     * @param queryParameters array of query arguments
     * @return <i>map</i> of table row values
     */
    public static Map<String, String> getTableRecordByIndex(final String queryFileName, final Integer recordIndex,
            final Object... queryParameters) {
        List<Map<String, String>> queryResults = getTableRecords(queryFileName, queryParameters);
        if (queryResults.size() > recordIndex) {
            return queryResults.get(recordIndex);
        }
        return Collections.emptyMap();
    }

    /**
     * Get first selected record from db using query string from <i>*.sql</i> file.
     * Method returns empty <i>Map</i> if corresponding record not present
     *
     * @param queryFileName name of file with query string with extension
     * @param queryParameters array of query arguments
     * @return <i>map</i> of table row values
     */
    public static Map<String, String> getFirstSelectedRecord(final String queryFileName,
            final Object... queryParameters) {
        return getTableRecordByIndex(queryFileName, 0, queryParameters);
    }

    /**
     * Get first selected record from db using query string from <i>*.sql</i> file.
     * Method throws an Exception if corresponding record not present
     *
     * @param queryFileName name of file with query string with extension
     * @return <i>map</i> of table row values
     */
    public static Map<String, String> getFirstSelectedRecord(final String queryFileName) {
        Map<String, String> results = getTableRecordByIndex(queryFileName, 0);
        if (results.isEmpty()) {
            throw new IndexOutOfBoundsException("No records within the " + queryFileName);
        }
        return results;
    }

    /**
     * Get column value of first selected record from db using query string from <i>*.sql</i> file.
     * Method returns <i>null</i> if corresponding field value not present
     *
     * @param queryFileName name of file with query string with extension
     * @param fieldName name of needed column
     * @param queryParameters array of query arguments
     * @return column string value
     */
    public static String getFirstSelectedRecordFieldValue(final String queryFileName, final String fieldName,
            final Object... queryParameters) {
        return getFirstSelectedRecord(queryFileName, queryParameters).get(fieldName);
    }

    public static void updateTableRecordsByQuery(final String queryString,
                                                 final Object... queryParameters) {
        DbQuery.update(String.format(queryString, queryParameters));
    }
}
