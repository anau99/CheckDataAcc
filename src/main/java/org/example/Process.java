package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Process {
    public void processData(Sheet sheet, Workbook workbook, HashMap<String, BigDecimal[]> hashMap ) {
        int numRows = sheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= numRows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                int numColumns = row.getLastCellNum();
                String id = null;
                BigDecimal vlueD = BigDecimal.ZERO;
                BigDecimal vlueCr = BigDecimal.ZERO;

                for (int i = 0; i < numColumns; i++) {
                    if (row.getCell(i) != null) {
                        if (i == 2) {
                            id = row.getCell(i).toString().trim();
                        }
                        if (i == 6) {
                            String cellValue = row.getCell(i).toString().trim();
                            vlueD = parseBigDecimal(cellValue);
                        }
                        if (i == 7) {
                            String cellValue = row.getCell(i).toString().trim();
                            vlueCr = parseBigDecimal(cellValue);
                        }
                    }
                }

                if (id != null && !id.isEmpty()) {
                    if (hashMap.containsKey(id)) {
                        BigDecimal[] temp = hashMap.get(id);
                        if (temp != null) {
                            temp[0] = temp[0].add(vlueD.setScale(8, RoundingMode.HALF_UP));
                            temp[1] = temp[1].add(vlueCr.setScale(8, RoundingMode.HALF_UP));
                            hashMap.put(id, temp); // Cập nhật lại giá trị vào HashMap
                        }
                    } else {
                        BigDecimal[] temp = new BigDecimal[]{
                                vlueD.setScale(8, RoundingMode.HALF_UP),
                                vlueCr.setScale(8, RoundingMode.HALF_UP)
                        };
                        hashMap.put(id, temp);
                    }
                    ;
                }
            }

        }


        try {
            workbook.close();
        } catch (IOException e) {
            System.err.println("Error closing workbook: " + e.getMessage());
            e.printStackTrace();
        }


    }



    public String compareTwoSheets(HashMap<String, BigDecimal[]> hashMapA, HashMap<String, BigDecimal[]> hashMapB) {

        StringBuilder result = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.########");

        for (Map.Entry<String, BigDecimal[]> entry : hashMapA.entrySet()) {
            String keyA = entry.getKey();
            if (hashMapB.containsKey(keyA)) {
                BigDecimal differenceDebit = hashMapB.get(keyA)[0].subtract(hashMapA.get(keyA)[0]);
                BigDecimal differenceCredit = hashMapB.get(keyA)[1].subtract(hashMapA.get(keyA)[1]);
                if (differenceDebit.compareTo(BigDecimal.ZERO) != 0) {
                    result.append("Sự khác biệt tại số hóa đơn: ").append(keyA).append("\n");
                    result.append("Khác tại bên Nợ: ").append(df.format(differenceDebit.setScale(8, RoundingMode.HALF_UP))).append("\n");
                }
                if (differenceCredit.compareTo(BigDecimal.ZERO) != 0) {
                    result.append("Sự khác biệt tại số hóa đơn: ").append(keyA).append("\n");
                    result.append("Khác tại bên Có: ").append(df.format(differenceCredit.setScale(8, RoundingMode.HALF_UP))).append("\n");
                }
            } else {
                result.append("Không có hóa đơn số: ").append(keyA).append(" trong sheet thứ 2\n");
            }
        }


        for (Map.Entry<String, BigDecimal[]> entry : hashMapB.entrySet()) {
            String keyB = entry.getKey();
            if (!hashMapA.containsKey(keyB)) {
                result.append("Không có hóa đơn số: ").append(keyB).append(" trong sheet thứ 1\n");
            }
        }
        return result.toString();
    }




    public BigDecimal parseBigDecimal(String value) {
        try {
            // Chuyển đổi chuỗi thành BigDecimal
            return new BigDecimal(value.isEmpty() ? "0.0" : value);
        } catch (NumberFormatException e) {
            // Xử lý lỗi nếu chuỗi không hợp lệ
            System.err.println("Invalid number format: " + value);
            return BigDecimal.ZERO;
        }

    }

    public void sumOfDebitCredit(HashMap<String,BigDecimal[]> hashMap){
        BigDecimal sD = BigDecimal.ZERO;
        BigDecimal sC = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal[]> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            BigDecimal[] values = entry.getValue();

            if (values != null && values.length == 2) {
                // Cộng dồn giá trị
                sD = sD.add(values[0]);
                sC = sC.add(values[1]);

                // Kiểm tra sự khác biệt
                BigDecimal difference = values[0].subtract(values[1]).abs();
                if (difference.compareTo(new BigDecimal("0.00001")) > 0) {
                    System.out.println(key);
                    System.out.println("Value D: " + values[0].setScale(8, RoundingMode.HALF_UP));
                    System.out.println("Value C: " + values[1].setScale(8, RoundingMode.HALF_UP));
                    System.out.println("Difference: " + difference.setScale(8, RoundingMode.HALF_UP));
                }
            }
        }

        System.out.println("Total D: " + sD.setScale(8, RoundingMode.HALF_UP));
        System.out.println("Total C: " + sC.setScale(8, RoundingMode.HALF_UP));
    }


}