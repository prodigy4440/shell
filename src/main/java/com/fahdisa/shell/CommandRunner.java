package com.fahdisa.shell;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandRunner {

    private ExecutorService executor;
    private Process process;

    public void execute(String... cmds) {
        executor = Executors.newSingleThreadExecutor();
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectErrorStream(true);

        try {
            this.process = processBuilder.start();
            executor.execute(new LogReader(this.process.getInputStream()));
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        if (Objects.nonNull(process)) {
            try {
                process.waitFor();
                process.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    public void forciblyTerminate(){
        if (Objects.nonNull(process)) {
            process.destroy();
        }
        executor.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        CommandRunner commandRunner = new CommandRunner();

        commandRunner.execute("ls","-lh");

        commandRunner.terminate();
    }

    public class LogReader implements Runnable {

        private BufferedReader reader;

        public LogReader(InputStream is) {
            this.reader = new BufferedReader(new InputStreamReader(is));
        }

        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
            }
        }
    }

}
