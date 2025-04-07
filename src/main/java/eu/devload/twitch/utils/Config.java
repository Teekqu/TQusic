package eu.devload.twitch.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Scanner;

public class Config {
    private File file;

    public Config() {
        File file = new File("./config.json");
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if(success) {
                    FileWriter writer = new FileWriter(file);
                    writer.write("{}");
                    writer.close();
                }
            } catch (Exception err) {
                err.printStackTrace();
                return;
            }
        }

        this.file = file;
    }

    public Config(File file) {
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if(success) {
                    FileWriter writer = new FileWriter(file);
                    writer.write("{}");
                    writer.close();
                }
            } catch (Exception err) {
                err.printStackTrace();
                return;
            }
        }

        this.file = file;
    }

    public ConfigObject get(String key) {
        try {
            Scanner scanner = new Scanner(this.file);
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }

            JSONObject jo = new JSONObject(sb.toString());
            scanner.close();
            return !jo.isEmpty() && !jo.isNull(key) ? new ConfigObject(jo.get(key)) : null;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public void add(String key, Object value) {
        try {
            Scanner scanner = new Scanner(new FileInputStream("./" + this.file.getName()));
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line);
            }

            JSONObject jo = new JSONObject(sb.toString());
            jo.put(key, value);
            scanner.close();
            FileWriter writer = new FileWriter(this.file);
            writer.write(jo.toString());
            writer.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    public void remove(String key) {
        try {
            Scanner scanner = new Scanner(new FileInputStream("./" + this.file.getName()));
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line);
            }

            JSONObject jo = new JSONObject(sb.toString());
            jo.remove(key);
            scanner.close();
            FileWriter writer = new FileWriter(this.file);
            writer.write(jo.toString());
            writer.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    public class ConfigObject {

        private Object obj;

        public ConfigObject(Object obj) {
            this.obj = obj;
        }

        public Object get() {
            return this.obj;
        }

        public String getAsString() {
            return this.obj.toString();
        }

        public int getAsInt() {
            return Integer.parseInt(this.obj.toString());
        }

        public long getAsLong() {
            return Long.parseLong(this.obj.toString());
        }

        public double getAsDouble() {
            return Double.parseDouble(this.obj.toString());
        }

        public boolean getAsBoolean() {
            return Boolean.parseBoolean(this.obj.toString());
        }

        public JSONObject getAsJSONObject() {
            return new JSONObject(this.obj.toString());
        }

        public JSONArray getAsJSONArray() {
            return new JSONArray(this.obj.toString());
        }

        public boolean isNull() {
            return this.obj == null;
        }

    }

}
