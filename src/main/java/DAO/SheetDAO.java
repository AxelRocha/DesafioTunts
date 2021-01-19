package DAO;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.List;

public class SheetDAO {
    private static final String spreadsheetId = "1X-SDksJyPlQBXGpqKCSlB-1jORzInWZ6wJZUHYLIb8c";

    private ValueRange lastResult;
    private String lastRange = "";
    private final Sheets service;
    private final String sheetName;

    public SheetDAO(Sheets service, String sheetName) {
        this.service = service;
        this.sheetName = sheetName + "!";
    }

    /**
     * Gets the Data from the Sheet.
     * @param range Range of data to get from the Sheet in A1 notation
     * @return The requested range of data
     * @throws IOException If the service can not execute
     */
    private ValueRange getSheetData(String range) throws IOException {
        lastResult = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .setValueRenderOption("FORMATTED_VALUE")
                .execute();

        return lastResult;
    }

    /**
     * Gets the values from the data retrieve from the range param.
     * @param range Range of data to get from the Sheet in A1 notation
     * @return The data of the requested range
     * @throws IOException If an error occurs getting the values
     */
    public List<List<Object>> getValuesInRange(String range) throws IOException {
        if (lastRange.equals(range)) {
            return lastResult.getValues();
        }

        lastRange = range;
        return getSheetData(buildRange(sheetName, range))
                .getValues();
    }

    /**
     * Set the values on the Sheet in the especified range param
     * @param values values to be written on the Sheet
     * @param range Range of data to where to be written on the Sheet in A1 notation
     * @throws IOException If an error occurs setting the values
     */
    public void setValuesInRange(List<List<Object>> values, String range) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(values);

        service.spreadsheets().values()
                .update(spreadsheetId, buildRange(sheetName, range), body)
                .setValueInputOption("RAW")
                .execute();
    }

    String buildRange(String sheetName, String range){
        return sheetName + range;
    }
}
