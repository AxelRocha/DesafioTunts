import DAO.SheetDAO;
import Utils.Utils;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static Service.SheetService.getService;

public class Main {
    public static void main(String... args) throws IOException, GeneralSecurityException {
        /* Before known sheet name to be read */
        final String sheetName = "engenharia_de_software";

        /* Create Google Sheet Service */
        Sheets gsService = getService();

        /* Create Data Access Object */
        SheetDAO dao = new SheetDAO(gsService, sheetName);

        /* Creates Utils object to calculate grades */
        Utils utils = new Utils(dao);

        /* Analysing the grades */
        utils.GradeAnalysis();

    }
}
