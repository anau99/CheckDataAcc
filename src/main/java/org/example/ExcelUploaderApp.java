package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUploaderApp extends JFrame {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel timerLabel;
    private Timer timer;
    private long startTime;
    private int elapsedTime = 0; // Thời gian đã trôi qua tính bằng giây
    private JTextArea resultTextArea; // Thêm JTextArea để hiển thị kết quả

    private JTextPane resultTextPane;

    public ExcelUploaderApp() {


        setTitle("Excel Uploader");
        setSize(800, 600); // Tăng kích thước để chứa JTextArea
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JButton uploadButton1 = new JButton("Upload Excel 1");
        uploadButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadExcelFile();

            }
        });

        JPanel uploadPanel = new JPanel();
        uploadPanel.add(uploadButton1);

        add(uploadPanel, BorderLayout.NORTH);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        add(new JScrollPane(resultTextArea), BorderLayout.CENTER);


        timerLabel = new JLabel("Time elapsed: 0s");
        add(timerLabel, BorderLayout.SOUTH);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText("Time elapsed: " + elapsed + "s");
            }
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void uploadExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            new Thread(() -> {
                try {
                    Workbook workbook = new XSSFWorkbook(selectedFile);
                    int sheetCount = workbook.getNumberOfSheets();
                    if (sheetCount != 2) {
                        JOptionPane.showMessageDialog(null, "Số lượng sheet trong file upload phải bằng 2.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Sheet sheetOld = workbook.getSheetAt(0);
                    Sheet sheetNew = workbook.getSheetAt(1);
                    HashMap<String, BigDecimal[]> hashMapA = new HashMap<>();
                    HashMap<String, BigDecimal[]> hashMapB = new HashMap<>();
                    Process process = new Process();

                    SwingUtilities.invokeLater(this::startTimer);
                    ExecutorService executorService = Executors.newFixedThreadPool(2);

                    Callable<Void> taskA = () -> {
                        synchronized (workbook) {
                            process.processData(sheetOld, workbook, hashMapA);
                        }
                        return null;
                    };

                    Callable<Void> taskB = () -> {
                        synchronized (workbook) {
                            process.processData(sheetNew, workbook, hashMapB);
                        }
                        return null;
                    };

                    Future<Void> futureA = executorService.submit(taskA);
                    Future<Void> futureB = executorService.submit(taskB);

                    futureA.get();
                    futureB.get();

                    String comparisonResult = process.compareTwoSheets(hashMapA, hashMapB);
                    executorService.shutdown();

                    SwingUtilities.invokeLater(() -> resultTextArea.setText(comparisonResult));

                    SwingUtilities.invokeLater(this::stopTimer);

                    workbook.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading Excel file: " + ex.getMessage());
                }
            }).start();  // Bắt đầu thread mới
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExcelUploaderApp().setVisible(true);
            }
        });
    }
}