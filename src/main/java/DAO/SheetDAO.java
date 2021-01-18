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

    private ValueRange getSheetValues(String range) throws IOException {
        lastResult = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .setValueRenderOption("FORMATTED_VALUE")
                .execute();

        return lastResult;
    }

    public List<List<Object>> getValuesInRange(String range) throws IOException {
        if (lastRange.equals(range)) {
            return lastResult.getValues();
        }

        lastRange = range;
        return getSheetValues(buildRange(sheetName, range))
                .getValues();
    }

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
