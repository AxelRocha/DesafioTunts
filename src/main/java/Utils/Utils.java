package Utils;

import DAO.SheetDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
    /* Before kwon range to be read */
    final String readRange = "A4:H27";

    final SheetDAO dao;

    public Utils(SheetDAO dao) {
        this.dao = dao;
    }

    /**
     * Get Sheet Data and Assign every student's situation.
     * First checks if the student is already failed by missing its classes
     * If not, then assign its situation.
     * @throws IOException If the data is cant be retrieved by dao
     */
    public void GradeAnalysis() throws IOException {
        List<List<Object>> readValues = dao.getValuesInRange(readRange);

        if (readValues == null || readValues.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : readValues) {
                if (verifyMissedClasses(row, readValues)){
                    AssignStudentSituation(row, readValues);
                }
            }
        }
    }

    /**
     * Assign the student situation.
     * Calculates the mean of his tests and then assign its proper situation
     * @param row The sheet row being read
     * @param values The list of rows
     * @throws IOException If the data is cant be sent by dao
     */
    private void AssignStudentSituation(List<Object> row, List<List<Object>> values) throws IOException {
        int currentRow = getCurrentRow(row, values);
        String writeRange = getWriteRange(currentRow);
        List<String> situation;
        double mean;

        mean = calculateStudentMean(row);

        situation = getSituation(mean);

        List<List<Object>> inputValues = Collections.singletonList(
                Arrays.asList(
                        situation.get(0),
                        situation.get(1)
                )
        );

        dao.setValuesInRange(inputValues, writeRange);
    }

    /**
     * Decide the student's situation.
     * If its mean < 50 => failed
     * 50 < mean < final exam
     * 70 > approved
     * @param mean Value to decide the student's situation
     * @return Student Situation - Pos(0) Situation, Pos(1) Grade to Final Approval
     */
    private List<String> getSituation(double mean) {
        List<String> situation = new ArrayList<>();

        if (mean < 50.0) {
            situation.add("Reprovado por Nota");
            situation.add("0");
        } else if (mean < 70.0) {
            situation.add("Exame Final");
            situation.add(getGradeForFinalApproval(mean));
        } else {
            situation.add("Aprovado");
            situation.add("0");
        }

        return situation;
    }

    /**
     * Verify if the student failed by missing too much classes.
     * @param row The sheet row being read
     * @param values The list of rows
     * @return True if the student did not failed to attend to the class
     * @throws IOException If the data is cant be sent by dao
     */
    private boolean verifyMissedClasses(List<Object> row, List<List<Object>> values) throws IOException {
        double presence = getCellValue(row.get(2));
        double percentalPresence = calculatePresence(presence);
        int currentRow = getCurrentRow(row, values);
        String writeRange = getWriteRange(currentRow);
        boolean passed = true;

        if (percentalPresence > 0.25){
            passed = false;
            List<List<Object>> inputValues = Collections.singletonList(
                    Arrays.asList(
                            "Reprovado por Falta", "0"
                    )
            );
            dao.setValuesInRange(inputValues, writeRange);
        }

        return passed;
    }

    private double getCellValue(Object value){
        if (value == null)
            return 0.0;
        else
            return Integer.parseInt((String) value) * 1.0;
    }

    private String getGradeForFinalApproval(double mean) {
        return String.valueOf(Math.ceil(100.0 - mean));
    }

    private double calculateStudentMean(List<Object> row) {
        return (getCellValue(row.get(3)) + getCellValue(row.get(4)) + getCellValue(row.get(5)))/3;
    }

    private double calculatePresence(double presence) {
        return presence / 60;
    }

    private String getWriteRange(int currentRow) {
        return "G" + currentRow + ":H" + currentRow;
    }

    private int getCurrentRow(List<Object> row, List<List<Object>> values) {
        return values.indexOf(row) + 4;
    }

}
