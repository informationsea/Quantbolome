package jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper;

import au.com.bytecode.opencsv.CSVReader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.CompoundImpl;
import lombok.val;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FixedBaseIntensityLoader {
    public static Map<Compound, Double> loadFixedBaseIntensity(File path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            Map<Compound, Double> result = new HashMap<>();
            CSVReader csvReader = new CSVReader(reader);
            val header = csvReader.readNext();
            if (header.length != 5 || !header[0].equals("M/Z") || !header[1].equals("Retention") || !header[2].equals("neutralMass") || !header[3].equals("Charge") || !header[4].equals("Base Intensity")) {
                throw new RuntimeException("Fixed base intensity CSV should have 5 columns. \"M/Z\",\"Retention\",\"neutralMass\",\"Charge\",\"Base Intensity\"");
            }

            int lineNumber = 1;
            for (val line : csvReader.readAll()) {
                lineNumber += 1;
                if (line.length != 5) {
                    throw new RuntimeException("Number of columns is not correct at line " + lineNumber);
                }
                double mz = Double.parseDouble(line[0]);
                double retention = Double.parseDouble(line[1]);
                Double neutralMass = null;
                if (!line[2].equals("")) {
                    neutralMass = Double.parseDouble(line[2]);
                }
                int charge = Integer.parseInt(line[3]);
                double baseIntensity = Double.parseDouble(line[4]);
                result.put(new CompoundImpl(mz, retention, neutralMass, charge), baseIntensity);
            }

            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
