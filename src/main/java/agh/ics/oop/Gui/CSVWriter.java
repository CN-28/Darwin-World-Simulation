package agh.ics.oop.Gui;

import agh.ics.oop.Maps.AbstractWorldMap;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {
    FileWriter csvWriter;
    AbstractWorldMap map;

    public CSVWriter(AbstractWorldMap map){
        this.map = map;
    }

    public void writeToCSV(){
        try {
            this.csvWriter = new FileWriter(this.map.getNameOfCSV());
            double[] sumArr = new double[5];
            int toWriteToCSVLen =  this.map.toWriteToCSV.size();
            for (int i = 0 ; i < toWriteToCSVLen; i++){
                if (i % 5 == 4)
                    csvWriter.append(String.valueOf(this.map.toWriteToCSV.get(i))).append("\n");
                else
                    csvWriter.append(String.valueOf(this.map.toWriteToCSV.get(i))).append(" ");

                sumArr[i % 5] += this.map.toWriteToCSV.get(i);
            }
            int epochs = toWriteToCSVLen / 5;
            csvWriter.append(String.valueOf(sumArr[0] / epochs)).append(" ").append(String.valueOf(sumArr[1] / epochs)).append(" ").append(String.valueOf(sumArr[2] / epochs)).append(" ").append(String.valueOf(sumArr[3] / epochs)).append(" ").append(String.valueOf(sumArr[4] / epochs)).append("\n");

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
