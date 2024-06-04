package app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstrakte Klasse zum Interagieren mit Dateien im System
 */
public abstract class FileSS {

    /**
     * speichert Objekt in Datei
     * @param o     zu speicherndes Objekt
     * @param pPath Pfad im dem das Objekt gespeichert werden soll
     * @return      Erfolg
     */
    public static boolean saveToFile(Object o, String pPath){
        try{
            FileOutputStream fout = new FileOutputStream(pPath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(o);

            oos.close();
            fout.close();
            return true;
        }catch(IOException i){
            i.printStackTrace();
            return false;
        }
    }

    /**
     * lädt Objekt aus Datei
     * @param pPath Pfad im dem das Objekt gespeichert werden soll
     * @return      Objekt in Datei
     */
    public static Object loadFromFile(String pPath) throws Exception{
        FileInputStream fin = new FileInputStream(pPath);
        ObjectInputStream ois = new ObjectInputStream(fin);
            
        Object ret = ois.readObject();

        ois.close();
        fin.close();

        return ret;
    }

    /**
     * Liest String aus Datei in Pfad
     * @param pPath
     * @return
     */
    public static String fileToString(String pPath){

        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(pPath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.startsWith("#"))
                    sb.append(line).append("\n");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Schreibt String in Datei
     * @param pString
     * @param pPath
     * @return Erfolg?
     */
    public static boolean stringToFile(String pString, String pPath){
        try(FileWriter writer = new FileWriter(pPath);) {
            writer.write(pString);
            writer.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
